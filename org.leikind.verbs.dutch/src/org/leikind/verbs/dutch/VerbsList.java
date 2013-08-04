package org.leikind.verbs.dutch;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.*;

import static android.provider.BaseColumns._ID;
import static org.leikind.verbs.dutch.Constants.*;

public class VerbsList extends SQLiteOpenHelper {

  private Context context;
  private SQLiteDatabase dbCached;

  public VerbsList(Context ctx) {
    super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    context = ctx;
  }

  private SQLiteDatabase getCachedDb() {
    if (dbCached == null) {
      dbCached = this.getReadableDatabase();
    }
    return dbCached;
  }


  // Accessors

  public Verb findVerbForId(long id, String locale) {
    SQLiteDatabase db = getCachedDb();
    String[] args = {Long.toString(id)};
    String[] selectFields = DEFAULT_FROM_FIELDS;
    if (locale != null) {
      if (locale.equals(EN)) {
        selectFields = FROM_FIELDS_EN_LOCALE;
      } else if (locale.equals(DE)) {
        selectFields = FROM_FIELDS_DE_LOCALE;
      } else if (locale.equals(FR)) {
        selectFields = FROM_FIELDS_FR_LOCALE;
      }
    }
    Cursor cursor = db.query(TABLE_NAME, selectFields, _ID + " = ?", args, null, null, null);
    return Verb.fromCursor(cursor, locale);
  }

  public Cursor getVerbs() {
    SQLiteDatabase db = getCachedDb();
    return db.query(TABLE_NAME, DEFAULT_FROM_FIELDS, null, null, null, null, ORDER_BY);
  }

  public Cursor getVerbs(String content) {
    SQLiteDatabase db = getCachedDb();

    String arg = content + "%";
    String[] args = {arg, arg, arg, arg};
    return db.query(TABLE_NAME, DEFAULT_FROM_FIELDS,
        "infinitive like ? or past_singular like ? or past_plural like ? or past_participle like ?",
        args, null, null, ORDER_BY);
  }


  //   Migrations

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Log.d(TAG, "creating database");
    createTable(db);
    populate(db);
  }

  private void dropTable(SQLiteDatabase db) {
    db.execSQL("drop table verbs;");
  }


  private void createTable(SQLiteDatabase db) {

    db.execSQL("create table verbs (" +
        _ID + " integer primary key autoincrement," +
        INFINITIVE + " varchar(11)," +
        PAST_SINGULAR + " varchar(10)," +
        PAST_PLURAL + " varchar(12)," +
        PAST_PARTICIPLE + " varchar(12)," +
        AUXILIARY_VERB + " integer,    " +
        EN + " varchar(44)," +
        DE + " varchar(57)," +
        FR + " varchar(63) );"
    );
  }

  private void populate(SQLiteDatabase db) {
    try {
      AssetManager am = context.getAssets();
      InputStream fstream = am.open(VERBS_FILENAME);

      DataInputStream in = new DataInputStream(fstream);

      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String row;
      String sqlStatement;
      String[] chunks;
      while ((row = br.readLine()) != null) {
        chunks = row.split("\t", 8);

        // Log.d(TAG, row);

        sqlStatement = "insert into verbs (" + INFINITIVE + ", " + PAST_SINGULAR +
            ", " + PAST_PLURAL + ", " + PAST_PARTICIPLE + ", " + AUXILIARY_VERB + ", " +
            EN + ", " + DE + ", " + FR +
            ")  values (\"" +
            chunks[0] + "\", \"" +
            chunks[1] + "\", \"" +
            chunks[2] + "\", \"" +
            chunks[3] + "\", " +
            chunks[4] + ", \"" +
            chunks[5] + "\", \"" +
            chunks[6] + "\", \"" +
            chunks[7] + "\");";

        db.execSQL(sqlStatement);
      }

      in.close();
    } catch (FileNotFoundException e) {
      // Log.wtf(TAG, "Error: " + e.getMessage());
      // Log.e(TAG, "Error: " + e.getMessage());
    } catch (IOException f) {
      // Log.wtf(TAG, "Error: " + f.getMessage());
      // Log.e(TAG, "Error: " + f.getMessage());
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Log.d(TAG, "Upgrading database from version" + oldVersion + " to version " + newVersion);
    dropTable(db);
    createTable(db);
    populate(db);
  }
}
