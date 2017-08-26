package com.example.plainolnotes;

import android.*;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity
implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;
    Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        registerForContextMenu(list);

//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
//                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
//                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
//                startActivityForResult(intent, EDITOR_REQUEST_CODE);
//            }
//        });

//        Button locationButton = (Button) findViewById(R.id.btnViewLocation);
//        locationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent locationViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
//                locationViewIntent.putExtra("latitude", cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude)));
//                locationViewIntent.putExtra("longitude", cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude)));
//                startActivity(locationViewIntent);

//            }
//        });

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreateContextMenu (final ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //final restrantdata student = (restrantdata)
          //      ResturantList.getItemAtPosition(info.position);
        MenuItem EditMenu = menu.add("Edit");
//        EditMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Intent intentEdit = new Intent(MainActivity.this, EditorActivity.class);
//                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
//                intentEdit.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
//                startActivityForResult(intentEdit, EDITOR_REQUEST_CODE);
//                return true;
//            }
//        });

        MenuItem ViewLocMenu = menu.add("View Location");
//        ViewLocMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                return true;
//            }
//        });

        MenuItem delete = menu.add("Delete");

//        delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
//            @Override public  boolean onMenuItemClick(MenuItem item){
//
//                return false;
//            }
//        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Edit") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent intentEdit = new Intent(MainActivity.this, EditorActivity.class);
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            intentEdit.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
//            Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
//            startActivityForResult(intentEdit, EDITOR_REQUEST_CODE);
            startActivity(intentEdit);
//            restartLoader();
        }
        else if (item.getTitle() == "View Location") {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Intent locationViewIntent = new Intent(getApplicationContext(), MapsActivity.class);
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            locationViewIntent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
//
            //Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
            startActivity(locationViewIntent);
           // restartLoader();

        }
        else if (item.getTitle() == "Delete") {
            // TODO Delete action
//            Intent intent = new Intent();
//            Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + info.id);
            String noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            getContentResolver().delete(NotesProvider.CONTENT_URI,
                    noteFilter, null);
            Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
            restartLoader();
        }
        else {
            return false;
        }
        return true;
    }

    private void startActivityForResult(MainActivity mainActivity, Class<EditorActivity> editorActivityClass, int editorRequestCode) {
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_create_sample:
//                insertSampleData();
//                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(MainActivity.this, getString(R.string.all_deleted), Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

//    private void insertSampleData() {
//        insertNote("Simple note");
//        insertNote("Multi-line\nnote");
//        insertNote("Very long note with a lot of text that exceeds the width of the screen");
//        restartLoader();
//    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
//        startActivityForResult(intent, EDITOR_REQUEST_CODE);
        startActivity(intent);
    }



    @Override
    protected void onResume()
    {
        super.onResume();
        restartLoader();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
//            restartLoader();
//        }
//        else{
//
//        }
//    }
}
