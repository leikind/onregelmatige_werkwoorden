package org.leikind.verbs.dutch;


import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

class MainViewCalculations {
  private boolean landscape;
  private int lengthOfTwoFirstColumns;
  private int lengthOfThirdColumn;
  private int padding;

  public boolean isLandscape() {
    return landscape;
  }

  public int getLengthOfTwoFirstColumns() {
    return lengthOfTwoFirstColumns;
  }

  public int getLengthOfThirdColumn() {
    return lengthOfThirdColumn;
  }

  public int getPadding() {
    return padding;
  }


  public MainViewCalculations(Resources res, VerbsActivity verbsActivity) {

    DisplayMetrics dm = new DisplayMetrics();
    verbsActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);


    this.padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, res.getDisplayMetrics());

    if (res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      this.landscape = true;

      float oneTenth = (float) dm.widthPixels / 10;
      this.lengthOfTwoFirstColumns = (int) oneTenth * 3;
      this.lengthOfThirdColumn = (int) oneTenth * 4;

    } else {
      this.lengthOfTwoFirstColumns = (int) dm.widthPixels / 3;
      this.lengthOfThirdColumn = lengthOfTwoFirstColumns;

      this.landscape = false;
    }
  }
}

