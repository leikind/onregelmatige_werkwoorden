require 'rubygems'
require "bundler/setup"
require 'sqlite3'
require 'active_support/core_ext'

ActiveSupport::JSON::Encoding::Encoder

class IrregularVerbsDatabaseBilder

  HEBBEN = 'hebben'
  ZIJN   = 'zijn'
  BOTH   = 'hebben/zijn'

  AUX_VERBS_LABELS = {1 => HEBBEN, 2 => ZIJN, 3 => BOTH}
  AUX_VERBS_LABELS_REVERSED = Hash[AUX_VERBS_LABELS.to_a.map{|t1, t2| [t2, t1]}]

  VERBS_SOURCE="verbs.in.txt"

  TEST_DB = "verbs.db"
  SQL_OUT = "verbs.out.sql"
  VERBS_OUT = "verbs.out.txt"
  VERBS_OUT_JS = "verbs.js"

  CACHED_FILE = 'translations.in.yaml'

  EXISTING_LOCALES = ['en', 'de', 'fr']

  def initialize
    @db = SQLite3::Database.new(TEST_DB)
  end

  def create
    load_translations
    estimate_lengths
    sql =  %!create table verbs (\n! +
    %!   id integer primary key autoincrement,\n! +
    %!   infinitive      varchar(#{@lengths[:inf]}),\n! +
    %!   past_singular   varchar(#{@lengths[:past_sing]}),\n! +
    %!   past_plural     varchar(#{@lengths[:past_plural]}),\n! +
    %!   past_participle varchar(#{@lengths[:past_participle]}),\n! +
    %!   auxiliary_verb  integer,\n! +
    EXISTING_LOCALES.collect{|locale|
      %!   #{locale}              varchar(#{@longest_translation_sizes[locale]})!
    }.join(",\n") +
    %!\n);!
    log_sql sql
    @db.execute sql
  end

  def log_sql sql
    @sql_out ||= File.new(SQL_OUT, 'w')
    @sql_out.puts sql
  end

  # inf, past_sing, past_plural, past_participle
  def log_verbs *fields
    @js_struct ||= []
    @js_struct << fields[0..4]
    @verbs_out ||= File.new(VERBS_OUT, 'w')
    @verbs_out.puts fields.join("\t")
  end


  def insert(inf, past_sing, past_plural, past_participle, aux, translations)

    aux = case aux
      when nil
        AUX_VERBS_LABELS_REVERSED[HEBBEN]
      when HEBBEN
        AUX_VERBS_LABELS_REVERSED[HEBBEN]
      when ZIJN
        AUX_VERBS_LABELS_REVERSED[ZIJN]
      when BOTH
        AUX_VERBS_LABELS_REVERSED[BOTH]
    else
      raise "invalid auxiliary verb #{aux}"
    end

    past_plural = '' if past_plural == '_'
    past_participle = '' if past_participle == '_'

    log_verbs(inf, past_sing, past_plural, past_participle, aux, *translations)

    sql = %!insert into verbs! +
    %! (infinitive, past_singular, past_plural, past_participle, auxiliary_verb! +

    if EXISTING_LOCALES.empty?
      ''
    else
      ',' + EXISTING_LOCALES.join(', ')
    end +

    %!)  values! +
    %! ("#{inf}", "#{past_sing}", "#{past_plural}", "#{past_participle}", #{aux}! +

    if EXISTING_LOCALES.empty?
      ''
    else
      ',"' + translations.join('", "') + '"'
    end +

    %!);!
    log_sql sql
    @db.execute sql
  end

  def select
    @db.execute("select * from verbs ") do |row|
      yield row
    end
  end

  def delete_all
    @db.execute 'delete from verbs; '
  end


  def query(fragment)
    fragment = fragment.strip.downcase.gsub(/\s+/, ' ')
    sql = "select * from verbs where infinitive like ? or past_singular like ? or past_plural like ? or past_participle like ?"

    @db.execute(sql, [fragment + '%']*4){|row|
      row[5] = AUX_VERBS_LABELS[row[5]].downcase
      yield row
    }
  end


  def each_source_word
    File.open(VERBS_SOURCE) do |f|
      while line = f.gets

        line = line.chomp unless line.nil?

        fields = line.split(/\t/)

        if fields.size != 4 && fields.size != 5
          raise "invalid line:\nline\nsplit into:\n#{forms.inspect}"
        end

        yield fields

      end
    end
  end

  def populate
    delete_all
    each_source_word do |inf, past_sing, past_plural, past_participle, aux|
      translations =  EXISTING_LOCALES.map do |locale|

        unless @translations.has_key?(locale)
          raise "Translations for #{locale} not found!!!"
        end

        unless @translations[locale].has_key?(inf)
          raise "Translation for #{inf} #{locale} not found!!!"
        end

        translation = @translations[locale][inf]
        if translation.nil? || translation.empty?
          STDERR.puts "!!!  Translation for #{inf} #{locale} is empty  !!!"
        end
        translation
      end

      insert inf, past_sing, past_plural, past_participle, aux, translations

    end

    # File.open(VERBS_OUT_JS, 'w'){|f|
    #   f.puts @js_struct.to_json
    # }

  end

  def estimate_lengths
    string_fields = [:inf, :past_sing, :past_plural, :past_participle]
    @lengths = Hash.new()
    string_fields.each{|f| @lengths[f] = 0}

    each_source_word do |words|
      string_fields.each_with_index do |f, i|
        if words[i].size > @lengths[f]
          @lengths[f] = words[i].size
        end
      end
    end
  end

  def load_translations
    @translations = if File.exist?(CACHED_FILE)
      YAML.load_file(CACHED_FILE)
    else
      Hash.new
    end

    @longest_translation_sizes = Hash.new
    EXISTING_LOCALES.each do |locale|

      longest_translation =
        @translations[locale].values.inject('') do |accum, word|
          if word.nil? || accum.size > word.size
            accum
          else
            word
          end
        end
      @longest_translation_sizes[locale] = longest_translation.size
      # puts "#{locale} longest translation: '" + longest_translation + "'"
      # puts "#{@longest_translation_sizes[locale]} characters"
    end
  end

  def self.create
    File.unlink TEST_DB if FileTest.exist?(TEST_DB)
    File.unlink SQL_OUT if FileTest.exist?(SQL_OUT)
    File.unlink VERBS_OUT if FileTest.exist?(VERBS_OUT)
    builder = self.new
    builder.create
    builder.populate
    puts "copy #{VERBS_OUT} to the android app"
    # puts "check field sizes in VerbsList#createTable"
  end

  def self.query_test
    builder = self.new

    ['et', 'weten', '  wete', 'vrij', 'liep', 'kwam aan'].each do |q|
      puts "   === '#{q}' ==="
      builder.query(q){|row| p row}
    end
  end

end

# builder = IrregularVerbsDatabaseBilder.new
# builder.load_translations
IrregularVerbsDatabaseBilder.create
# IrregularVerbsDatabaseBilder.query_test
