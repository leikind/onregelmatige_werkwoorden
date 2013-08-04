package org.leikind.verbs.dutch;


import android.database.Cursor;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class VerbViewBinder implements ViewBinder {


  private String fragment;
  private int fragmentLength;

  MainViewCalculations calculations;

  public VerbViewBinder(String forFragment, MainViewCalculations calculations) {
    this.calculations = calculations;

    if (forFragment == null) {
      fragment = null;
    } else {
      fragment = forFragment.toLowerCase();
      fragmentLength = fragment.length();
    }
  }


  public boolean setViewValue(View _view, Cursor cursor, int columnIndex) {
    TextView textView = (TextView) _view;

    if (columnIndex > 0 && columnIndex < 5) {
      switch (columnIndex) {
        case 1:
          textView.setWidth(calculations.getLengthOfTwoFirstColumns());
          textView.setPadding(calculations.getPadding(), 0, 0, 0);
          break;
        case 2:
          textView.setWidth(calculations.getLengthOfTwoFirstColumns());
          break;
        case 3:
          textView.setPadding(calculations.getLengthOfTwoFirstColumns(), 0, 0, 0);
          textView.setWidth(calculations.getLengthOfTwoFirstColumns());
          break;
        case 4:
          textView.setPadding(0, 0, calculations.getPadding(), 0);
          textView.setWidth(calculations.getLengthOfThirdColumn());

      }

      String verbForm = cursor.getString(columnIndex);
      SpannableString content;
      int offset = 0;

      if (calculations.isLandscape() && columnIndex == 4) {
        String auxiliary = Verb.intToAuxuliary(cursor.getInt(5));
        String auxiliaryVerbChunk = "(" + auxiliary + ") ";
        content = new SpannableString(auxiliaryVerbChunk + verbForm);
        offset = auxiliaryVerbChunk.length();
      } else {
        content = new SpannableString(verbForm);
      }

      addHighlighting(content, verbForm, offset);
      textView.setText(content);

      return true;

    } else {
      return false;
    }
  }


  protected void addHighlighting(SpannableString span, String verbForm, int start) {
    if (fragment != null) {

      if (verbForm.startsWith(fragment)) {
        int end = fragmentLength + start;
        span.setSpan(new UnderlineSpan(), start, end, 0);
        span.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, 0);
      }
    }
  }

}

