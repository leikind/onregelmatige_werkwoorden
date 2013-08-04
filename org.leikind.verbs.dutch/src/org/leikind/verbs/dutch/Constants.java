package org.leikind.verbs.dutch;


import static android.provider.BaseColumns._ID;

public interface Constants {
  public static final String TAG = "verbs";

  public static final String KEY_FOR_DETAILED_INFO_ACTIVITY_INTENT = "id";

  public static final String DATABASE_NAME = "verbs.db";
  public static final int DATABASE_VERSION = 5;


  public static final String TABLE_NAME = "verbs";

  // Column names
  public static final String INFINITIVE = "infinitive";
  public static final String PAST_SINGULAR = "past_singular";
  public static final String PAST_PLURAL = "past_plural";
  public static final String PAST_PARTICIPLE = "past_participle";
  public static final String AUXILIARY_VERB = "auxiliary_verb";

  public static final String EN = "en";
  public static final String DE = "de";
  public static final String FR = "fr";
  public static final String ORDER_BY = INFINITIVE + " ASC";

  public static String[] DEFAULT_FROM_FIELDS = {_ID, INFINITIVE, PAST_SINGULAR, PAST_PLURAL, PAST_PARTICIPLE, AUXILIARY_VERB};

  public static String[] FROM_FIELDS_EN_LOCALE = {_ID, INFINITIVE, PAST_SINGULAR, PAST_PLURAL, PAST_PARTICIPLE, AUXILIARY_VERB, EN};
  public static String[] FROM_FIELDS_DE_LOCALE = {_ID, INFINITIVE, PAST_SINGULAR, PAST_PLURAL, PAST_PARTICIPLE, AUXILIARY_VERB, DE};
  public static String[] FROM_FIELDS_FR_LOCALE = {_ID, INFINITIVE, PAST_SINGULAR, PAST_PLURAL, PAST_PARTICIPLE, AUXILIARY_VERB, FR};
  public static String VERBS_FILENAME = "verbs.txt";


}
