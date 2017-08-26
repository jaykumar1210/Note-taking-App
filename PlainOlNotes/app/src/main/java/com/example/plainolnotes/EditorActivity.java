package com.example.plainolnotes;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;


public class EditorActivity extends ActionBarActivity implements OnMapReadyCallback {

    private String action;
    private EditText editor;
    private ImageView photoField;
    //private EditText location;
    private String noteFilter;
    private String oldText, oldPath;
    private String photopath;
    public static final int CAMERA_CODE = 567,GALLERY_CODE = 456;
    private MapView mapView;

    private GPSTracker gpsTracker;
    private Location mLocation;
    String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
//        getLocation();


        editor = (EditText) findViewById(R.id.editText);
        photoField = (ImageView) findViewById(R.id.imageView1);
        //location = (EditText) findViewById(R.id.editText);
        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,
                    DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            oldPath = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Photopath));
            editor.setText(oldText);
//            editor.requestFocus();
            //Toast.makeText(this,oldPath, Toast.LENGTH_LONG).show();
            loadPhoto(oldPath);

        }


        Button photoButton = (Button) findViewById(R.id.fromCamera);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photopath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File photofile = new File(photopath);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photofile));
                startActivityForResult(intentCamera, CAMERA_CODE);

            }
        });

//        Button galleryButton = (Button) findViewById(R.id.fromGallery);
//        galleryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                // photopath = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
//                startActivityForResult(photoPickerIntent, GALLERY_CODE);
//
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.getItem(1).setEnabled(false);
        if (action.equals(Intent.ACTION_INSERT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

//    @Override
//    public  boolean onPrepareOptionsMenu(Menu menu) {
//       MenuItem menuItem = (MenuItem) findViewById(R.id.action_save);
//        menuItem.setEnabled(false);
//        return  super.onPrepareOptionsMenu(menu);
//
//
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent mIntent = new Intent(this, MainActivity.class);
                //setResult(RESULT_OK, mIntent);
                startActivity(mIntent);
//                finishEditing();
                break;
//            case R.id.action_delete:
//                deleteNote();
//                break;
            case R.id.action_save:
               // MenuItem menuItem = (MenuItem) findViewById(R.id.action_save);

                String newText = editor.getText().toString().trim();
               String newPhoto = photoField.getTag().toString().trim();
                Toast.makeText(this, photoField.getTag().toString().trim(), Toast.LENGTH_SHORT).show();
                if(newText == "" && newPhoto == "") {

                    Toast.makeText(this,  "Please Enter Note and Select Image", Toast.LENGTH_SHORT).show();
                }
                else {

                   finishEditing();}
                break;
        }


        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI,
                noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        String newPhoto = photoField.getTag().toString().trim();
        Toast.makeText(this, newText + "," + newPhoto, Toast.LENGTH_SHORT).show();
        if(newText == "" || newPhoto == "") {
            Toast.makeText(this,  "Please Enter Note and Select Image", Toast.LENGTH_SHORT).show();
            return;
        }
            switch (action) {
                case Intent.ACTION_INSERT:
                    if (newText.length() == 0) {
                        setResult(RESULT_CANCELED);
                    } else {
                        insertNote(newText, newPhoto, latitude.toString().trim(), longitude.toString().trim());
                    }
                    break;
                case Intent.ACTION_EDIT:
                    if (newText.length() == 0) {
                        deleteNote();
                    } else if (oldText.equals(newText) && oldPath.equals(newPhoto)) {
                        setResult(RESULT_CANCELED);
                    } else {
                        updateNote(newText, newPhoto, latitude.toString().trim(), longitude.toString().trim());
                    }

            }
        finish();
    }

    private void updateNote(String noteText, String photoPath, String latitude, String longitude) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.Photopath, photoPath);
        values.put(DBOpenHelper.Latitude, latitude);
        values.put(DBOpenHelper.Longitude, longitude);

        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText, String photoPath, String latitude, String longitude) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.Photopath, photoPath);
        values.put(DBOpenHelper.Latitude, latitude);
        values.put(DBOpenHelper.Longitude, longitude);

        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
//        String newText = editor.getText().toString().trim();
//        String newPhoto = photoField.getTag().toString().trim();
//
//        super.onBackPressed();
//        setResult(RESULT_CANCELED);
//        finish();

//        Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
        Intent mIntent = new Intent(this, MainActivity.class);
        //setResult(RESULT_OK, mIntent);
        startActivity(mIntent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//        Toast.makeText(this, "Pressed", Toast.LENGTH_SHORT).show();
//            //super.onBackPressed();
//            //NavUtils.navigateUpFromSameTask(this);
//            this.finish();
            return true;
//        }
//        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == Activity.RESULT_OK){

            if (requestCode == CAMERA_CODE){
               // helper.loadPhoto(photopath);
                loadPhoto(photopath);
            }
            else if (requestCode == GALLERY_CODE) {
                Uri selectedImageUri = data.getData();
                String picturePath = getPath( this.getApplicationContext(), selectedImageUri );
                Toast.makeText(this,picturePath,Toast.LENGTH_LONG).show();
                loadPhoto(picturePath);
            }
        }

    }

    public void loadPhoto(String photopath) {
       // Toast.makeText(this,"In load photo", Toast.LENGTH_LONG).show();
       // return;
        Toast.makeText(this,"1",Toast.LENGTH_LONG).show();
        Toast.makeText(this,photopath,Toast.LENGTH_LONG).show();
        if (photopath != null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photopath);
            Toast.makeText(this,"1",Toast.LENGTH_LONG).show();
            Bitmap bitmapReduced = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            photoField.setImageBitmap(bitmapReduced);
            photoField.setScaleType(ImageView.ScaleType.FIT_XY);
            photoField.setTag(photopath);

        }
        getLocation();
    }

//    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        float bitmapRatio = (float)width / (float) height;
//        Toast.makeText(this,"2",Toast.LENGTH_LONG).show();
//        if (bitmapRatio > 1) {
//            Toast.makeText(this,"3",Toast.LENGTH_LONG).show();
//            width = maxSize;
//            height = (int) (width / bitmapRatio);
//        } else {
//            height = maxSize;
//            width = (int) (height * bitmapRatio);
//        }
//        return Bitmap.createScaledBitmap(image, width, height, true);
//    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private void getLocation() {
        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        latitude = String.valueOf(mLocation.getLatitude());
        longitude = String.valueOf(mLocation.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
