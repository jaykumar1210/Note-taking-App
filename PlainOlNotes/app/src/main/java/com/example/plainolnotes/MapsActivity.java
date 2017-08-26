package com.example.plainolnotes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private Location mLocation;
    Double latitude;
    Double longitude;
    private String noteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);





//        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
//        Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,intent.getStringExtra("lat"), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this,intent.getStringExtra("long"), Toast.LENGTH_SHORT).show();

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        Toast.makeText(this,"ready", Toast.LENGTH_LONG).show();
//      latitude = mLocation.getLatitude();
//        longitude = mLocation.getLongitude();
//        lati = intent.getStringExtra("lat");
//        longi = intent.getStringExtra("long");
//        latitude = Double.valueOf(intent.getStringExtra("latitude"));
//        longitude = Double.valueOf(intent.getStringExtra("longitude"));

       //

        //Toast.makeText(this, cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT), Toast.LENGTH_SHORT).show();

//        latitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude)));
//        longitude = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude)));

//        Toast.makeText(this, cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)), Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this,"1", Toast.LENGTH_LONG).show();
        mMap = googleMap;
//        Toast.makeText(this,"2", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
//        Toast.makeText(this,"3", Toast.LENGTH_LONG).show();

       Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
//        Toast.makeText(this,"4", Toast.LENGTH_LONG).show();
        noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
//        Toast.makeText(this,"5", Toast.LENGTH_LONG).show();
        Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
        cursor.moveToFirst();
//        Toast.makeText(this,"6", Toast.LENGTH_LONG).show();
//        Toast.makeText(thisz,"latlong " +cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT)), Toast.LENGTH_LONG).show();
//        Toast.makeText(this,"7", Toast.LENGTH_LONG).show();
//
//        // Add a marker in Sydney and move the camera
        latitude = Double.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude)));
        longitude = Double.valueOf(cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude)));

//        String lat = String.valueOf(latitude);
//        String long1 = String.valueOf(longitude);
//        Toast.makeText(this,lat, Toast.LENGTH_LONG).show();
//        Toast.makeText(this,long1, Toast.LENGTH_LONG).show();
//
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }
}
