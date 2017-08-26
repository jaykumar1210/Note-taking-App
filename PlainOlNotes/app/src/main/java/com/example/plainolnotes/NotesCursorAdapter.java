package com.example.plainolnotes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NotesCursorAdapter extends CursorAdapter{

    private ImageView photoField;
//    private Button locationButton;
    public NotesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.note_list_item, parent, false
        );
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        String noteText = cursor.getString(
                cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
        String photoPath = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Photopath));
        String latitude = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude));
        String longitude = cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude));

        int pos = noteText.indexOf(10);
        if (pos != -1) {
            noteText = noteText.substring(0, pos) + " ...";
        }

        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        TextView tvl = (TextView) view.findViewById(R.id.tvLocation);
        photoField = (ImageView) view.findViewById(R.id.imageDocIcon);
       // locationButton = (Button) view.findViewById(R.id.btnViewLocation);
        //locationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent locationViewIntent = new Intent(MainActivity.class, MapsActivity.class);
//                locationViewIntent.putExtra("latitude", cursor.getString(cursor.getColumnIndex(DBOpenHelper.Latitude)));
//                locationViewIntent.putExtra("longitude", cursor.getString(cursor.getColumnIndex(DBOpenHelper.Longitude)));
//                startActivity(locationViewIntent);
             //   Toast.makeText(context, "Clicked", Toast.LENGTH_LONG).show();

//            }
//        });

        tv.setText(noteText);
        tvl.setText(latitude +" , " + longitude);
        loadPhoto(photoPath);

    }


    public void loadPhoto(String photopath) {
        // Toast.makeText(this,"In load photo", Toast.LENGTH_LONG).show();
        // return;
        if (photopath != null)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photopath);
            Bitmap bitmapReduced = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            photoField.setImageBitmap(bitmapReduced);
            photoField.setScaleType(ImageView.ScaleType.FIT_XY);
            photoField.setTag(photopath);

        }
    }
}
