package org.leikind.verbs.dutch;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.*;

import static org.leikind.verbs.dutch.Constants.DEFAULT_FROM_FIELDS;
import static org.leikind.verbs.dutch.Constants.KEY_FOR_DETAILED_INFO_ACTIVITY_INTENT;

public class VerbsActivity extends ListActivity {
  private VerbsList verbsList;

  private EditText input;

  private MainViewCalculations calculations;

  private static int[] ADAPTOR_MAPPING = {0, R.id.infinitive, R.id.past_singular, R.id.past_plural, R.id.past_participle, 0};


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    calculations = new MainViewCalculations(getResources(), this);

    initUI();

    verbsList = new VerbsList(this);
    showAllVerbs();


    // DisplayMetrics dm = new DisplayMetrics();
    // getWindowManager().getDefaultDisplay().getMetrics(dm);
    // int widthInPixels = dm.widthPixels;
    //
    // float widthInDip = widthInPixels / dm.density;
    //
    // Log.d("fff", "widthInPixels = " + widthInPixels );
    // Log.d("fff", "densityDpi = " + dm.densityDpi);
    // Log.d("fff", "density = " + dm.density);
    // Log.d("fff", "widthInDip  = " + widthInDip);
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    if (verbsList != null) {
      verbsList.close();
    }
  }


  private void initUI() {


    setContentView(R.layout.main);
    registerForContextMenu(getListView());
    input = (EditText) findViewById(R.id.value_field);
    ImageButton clearButton = (ImageButton) findViewById(R.id.clear_input);
    MyListener listener = new MyListener();

    Button tryWiktionary = (Button) findViewById(R.id.try_wiktionary);

    tryWiktionary.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        wiktionaryLookupForCurrentUserValue();
      }
    });

    getListView().setFastScrollEnabled(true);

    input.setOnKeyListener(listener);
    input.addTextChangedListener(listener);
    clearButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        input.setText("");
      }
    });

  }


  private void showAllVerbs() {
    Cursor cursor = verbsList.getVerbs();
    startManagingCursor(cursor);
    showVerbs(cursor, null);
  }


  private void showVerbs(Cursor cursor, String forFragment) {
    SimpleCursorAdapter adapter =
        new SimpleCursorAdapter(this, R.layout.item, cursor, DEFAULT_FROM_FIELDS, ADAPTOR_MAPPING);

    adapter.setViewBinder(new VerbViewBinder(forFragment, calculations));
    setListAdapter(adapter);
  }


  private void wiktionaryLookupForCurrentUserValue() {
    Tools.wiktionaryLookup(this, getCurrentUserValue());
  }

  private void startDetailedInfo(long id) {
    Intent i = new Intent(this, DetailedInfoActivity.class);
    i.putExtra(KEY_FOR_DETAILED_INFO_ACTIVITY_INTENT, id);
    startActivity(i);
  }


  protected void onListItemClick(ListView l, View v, int position, long id) {
    startDetailedInfo(id);
  }


  private String getCurrentUserValue() {
    return input.getText().toString().trim();
  }


  //              Context Menu


  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.context_menu, menu);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    switch (item.getItemId()) {
      case R.id.copy_to_clipboard:

        Verb verb = verbsList.findVerbForId(info.id, null);
        Tools.copyToClipboard(this, verb.toString());

        return true;
      case R.id.more_info:
        startDetailedInfo(info.id);
        return true;

      case R.id.wiktionary_lookup:
        Tools.wiktionaryLookup(this, verbsList.findVerbForId(info.id, null));
        return true;

      default:
        return super.onContextItemSelected(item);
    }
  }


  //              Options Menu


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.about:
        Tools.displayAbout(this);
        return true;
    }
    return false;
  }


  // Listener

  class MyListener implements OnKeyListener, TextWatcher {
    public boolean onKey(View view, int keyCode, KeyEvent event) {

      if (keyCode == KeyEvent.KEYCODE_ENTER) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
        return true;
      } else {
        return false;
      }
    }

    public void afterTextChanged(Editable arg0) {
      process();
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    private boolean process() {
      String content = getCurrentUserValue();
      if (content.length() == 0) {
        showAllVerbs();
      } else {
        Cursor cursor = verbsList.getVerbs(content);
        startManagingCursor(cursor);
        showVerbs(cursor, content);
      }
      return true;
    }

  }


}
