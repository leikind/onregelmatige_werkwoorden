package org.leikind.verbs.dutch;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Locale;

import static org.leikind.verbs.dutch.Constants.*;

public class DetailedInfoActivity extends Activity {

  ImageButton button;
  Verb verb;

  TextView infinitive;
  TextView pastSingular;
  TextView pastPlural;
  TextView pastParticiple;
  TextView translation;

  boolean showTranslation;

  final private int copyInfinitiveContextMenuItem = 0;
  final private int copyPastSingularContextMenuItem = 1;
  final private int copyPastPluralContextMenuItem = 2;
  final private int copyPastParticipleContextMenuItem = 3;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detailed_info);

    initVerb();
    initUI();
  }

  private void initVerb() {
    Bundle b = getIntent().getExtras();
    long id = b.getLong(KEY_FOR_DETAILED_INFO_ACTIVITY_INTENT);
    String locale = Locale.getDefault().getLanguage();

    showTranslation = locale.equals(EN) || locale.equals(DE) || locale.equals(FR);

    if (!showTranslation) locale = null;

    VerbsList verbsList = new VerbsList(this);
    verb = verbsList.findVerbForId(id, locale);
    verbsList.close();
  }

  private void initUI() {
    TableLayout table = (TableLayout) findViewById(R.id.detailed);


    infinitive = (TextView) findViewById(R.id.infinitive);
    pastSingular = (TextView) findViewById(R.id.past_singular);
    pastPlural = (TextView) findViewById(R.id.past_plural);
    pastParticiple = (TextView) findViewById(R.id.past_participle);
    translation = (TextView) findViewById(R.id.translation);


    if (showTranslation) {
      translation.setText("(" + verb.getTranslation() + ")");
    }

    setTitle(verb.getInfinitive().substring(0, 1).toUpperCase() + verb.getInfinitive().substring(1));

    infinitive.setText(verb.getInfinitive());
    pastSingular.setText(verb.getPastSingular());
    pastPlural.setText(verb.getPastPlural());

    String auxiliaryAndParticiple = verb.getAuxiliaryAndParticiple();
    pastParticiple.setText(auxiliaryAndParticiple);

    registerForContextMenu(table);


    button = (ImageButton) findViewById(R.id.close_info);
    button.setOnClickListener(new OnClickListener() {
      public void onClick(View view) {
        finish();
      }
    });
  }


  //              Context Menu

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    String copy = getResources().getString(R.string.Copy);

    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(ContextMenu.NONE,
        copyInfinitiveContextMenuItem, copyInfinitiveContextMenuItem, copy + " \"" + verb.getInfinitive() + "\"");
    menu.add(ContextMenu.NONE,
        copyPastSingularContextMenuItem, copyPastSingularContextMenuItem, copy + " \"" + verb.getPastSingular() + "\"");
    menu.add(ContextMenu.NONE,
        copyPastPluralContextMenuItem, copyPastPluralContextMenuItem, copy + " \"" + verb.getPastPlural() + "\"");
    menu.add(ContextMenu.NONE,
        copyPastParticipleContextMenuItem, copyPastParticipleContextMenuItem, copy + " \"" + verb.getParticiple() + "\"");
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {


    String toCopy = null;
    switch (item.getItemId()) {
      case copyInfinitiveContextMenuItem:
        toCopy = verb.getInfinitive();
        break;
      case copyPastSingularContextMenuItem:
        toCopy = verb.getPastSingular();
        break;
      case copyPastPluralContextMenuItem:
        toCopy = verb.getPastPlural();
        break;
      case copyPastParticipleContextMenuItem:
        toCopy = verb.getParticiple();
        break;

      default:
        // Log.wtf(TAG, "Unrecognized context menu!!!");
        // Log.e(TAG, "Unrecognized context menu!!!");

    }
    if (toCopy != null) {
      Tools.copyToClipboard(this, toCopy);
      return true;
    } else {
      return super.onContextItemSelected(item);
    }
  }

  //              Options Menu

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.detailed_info_activity_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.wiktionary_lookup:
        Tools.wiktionaryLookup(this, verb);
        return true;

      case R.id.copy_to_clipboard:
        Tools.copyToClipboard(this, verb.toString());
        return true;

      case R.id.about:
        Tools.displayAbout(this);
        return true;

    }
    return false;
  }

}

