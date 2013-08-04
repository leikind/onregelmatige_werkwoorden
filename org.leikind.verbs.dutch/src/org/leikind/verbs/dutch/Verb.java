package org.leikind.verbs.dutch;

import android.database.Cursor;

public class Verb {

  private String infinitive;
  private String pastSingular;
  private String pastPlural;
  private String participle;
  private String translation;
  private long id;

  private int auxiliary;


  public static Verb fromCursor(Cursor cursor, String locale) {

    if (cursor.moveToNext()) {
      Verb verb = new Verb();
      verb.id = cursor.getInt(0);
      verb.infinitive = cursor.getString(1);
      verb.pastSingular = cursor.getString(2);
      verb.pastPlural = cursor.getString(3);
      verb.participle = cursor.getString(4);
      verb.auxiliary = cursor.getInt(5);

      if (locale != null) {
        verb.translation = cursor.getString(6);
      }
      cursor.close();
      return verb;
    } else {
      cursor.close();
      return null;
    }
  }

  public long getId() {
    return id;
  }


  public String getInfinitive() {
    return infinitive;
  }

  public String getTranslation() {
    return translation;
  }

  public String getPastSingular() {
    return pastSingular;
  }

  public String getPastPlural() {
    return pastPlural;
  }


  public String getParticiple() {
    return participle;
  }


  public String getAuxiliary() {
    return intToAuxuliary(auxiliary);
  }

  public String getAuxiliaryAndParticiple() {
    return "(" + getAuxiliary() + ") " + getParticiple();
  }


  public static String intToAuxuliary(int i) {
    switch (i) {
      case 1:
        return "hebben";
      case 2:
        return "zijn";
      case 3:
        return "hebben/zijn";
      default:
        return "";
    }
  }

  public String toString() {
    return infinitive + " " +
        pastSingular + " " +
        pastPlural + " (" +
        intToAuxuliary(auxiliary) + ") " +
        participle;
  }

}
