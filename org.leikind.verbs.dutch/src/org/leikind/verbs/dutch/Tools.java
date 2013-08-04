package org.leikind.verbs.dutch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

public class Tools {

  private static final String WIKTIONARY = "http://nl.wiktionary.org/wiki/";


  public static void wiktionaryLookup(Activity activity, Verb verb) {
    wiktionaryLookup(activity, verb.getInfinitive());
  }

  public static void wiktionaryLookup(Activity activity, String string) {

    Uri uri = Uri.parse(WIKTIONARY + string);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    activity.startActivity(intent);
  }


  public static void copyToClipboard(Activity activity, String what) {
    String copied = activity.getResources().getString(R.string.Copied);
    ClipboardManager mClipboard = (ClipboardManager) activity.getSystemService(Activity.CLIPBOARD_SERVICE);
    mClipboard.setText(what);
    Toast.makeText(activity, copied + ": \"" + what + "\" ", 3000).show();
  }

  public static void displayAbout(Activity activity) {
    String versionName = "";
    try {
      versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      // Log.e(TAG, e.toString());
    }
    Resources res = activity.getResources();
    String about_message = String.format(res.getString(R.string.about_message), versionName);

    final TextView message = new TextView(activity);
    final SpannableString spannable = new SpannableString(about_message);

    int messageFontSize;

    int screen = activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;


    if (screen == Configuration.SCREENLAYOUT_SIZE_SMALL) {
      messageFontSize = 16;
    } else if (screen == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      messageFontSize = 25;
    } else {
      messageFontSize = 18;
    }

    message.setTextSize(TypedValue.COMPLEX_UNIT_SP, messageFontSize);
    message.setTextColor(Color.WHITE);
    message.setPadding(16, 6, 16, 6);
    Linkify.addLinks(spannable, Linkify.EMAIL_ADDRESSES);
    message.setText(spannable);
    message.setMovementMethod(LinkMovementMethod.getInstance());

    AlertDialog.Builder alertBox = new AlertDialog.Builder(activity);
    alertBox.setTitle(R.string.about);
    alertBox.setView(message);

    alertBox.setPositiveButton(R.string.ok, null);

    alertBox.show();
  }

}
