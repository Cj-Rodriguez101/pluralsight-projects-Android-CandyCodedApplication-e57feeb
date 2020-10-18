package com.pluralsight.candycoded;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pluralsight.candycoded.DB.CandyContract;
import com.pluralsight.candycoded.DB.CandyContract.CandyEntry;
import com.pluralsight.candycoded.DB.CandyDbHelper;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String SHARE_DESCRIPTION = "Look at this delicious candy from Candy Coded - ";
    public static final String HASHTAG_CANDYCODED = " #candycoded";
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1232;
    String mCandyImageUrl = "";
    String candyName = "";
    String candyPrice = "";
    String candyDesc = "";
    ImageView imageView;
    Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = DetailActivity.this.getIntent();

        if (intent != null && intent.hasExtra("position")) {
            int position = intent.getIntExtra("position", 0);

            CandyDbHelper dbHelper = new CandyDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM candy", null);
            cursor.moveToPosition(position);

            candyName = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyContract.CandyEntry.COLUMN_NAME_NAME));
            candyPrice = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_PRICE));
            mCandyImageUrl = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_IMAGE));
            candyDesc = cursor.getString(cursor.getColumnIndexOrThrow(
                    CandyEntry.COLUMN_NAME_DESC));


            TextView textView = (TextView) this.findViewById(R.id.text_view_name);
            textView.setText(candyName);

            TextView textViewPrice = (TextView) this.findViewById(R.id.text_view_price);
            textViewPrice.setText(candyPrice);

            TextView textViewDesc = (TextView) this.findViewById(R.id.text_view_desc);
            textViewDesc.setText(candyDesc);

            imageView = (ImageView) this.findViewById(
                    R.id.image_view_candy);
            Picasso.with(this).load(mCandyImageUrl).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.share_detail){
            if(checkIfStorageHasPermission()){
                shareCurrentCandy();
            } else {
                if (requestAndAcquireStoragePermission()){
                    Log.e("TAG", "CALLING NUMBER");
                } else {
                    Log.e("TAG", "MESSAGE");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //share the candy with picture after getting permissions
    private void shareCurrentCandy(){
        //different implementation for different APIs
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap = bitmapDrawable .getBitmap();
        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                "title", null);
        if (Build.VERSION.SDK_INT < 29) {
            shareIntent = new Intent();
            shareIntent.setAction("android.intent.action.SEND");
            shareIntent.setType("*/*");

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmapPath));
            shareIntent.putExtra("android.intent.extra.TEXT", "Candy Name: " +candyName +"\n\n" +
                    "Candy Description: " +candyDesc + "\n\n"+
                    "Candy Price: " +candyPrice + "\n\n"//+
            );
            shareIntent.putExtra("android.intent.extra.SUBJECT", "Taste "+ candyName+"!!");
            shareIntent = Intent.createChooser(shareIntent, (CharSequence)candyName);
            if (shareIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivity(shareIntent);
            }
        } else {
            shareIntent = new Intent();
            shareIntent.setAction("android.intent.action.SEND");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(bitmapPath));
            shareIntent.putExtra("android.intent.extra.TEXT", "Candy Name: " +candyName +"\n\n" +
                    "Candy Description: " +candyDesc + "\n\n"+
                    "Candy Price: " +candyPrice + "\n\n"//+
                    );
            shareIntent.putExtra("android.intent.extra.SUBJECT", "Taste "+ candyName+"!!");
            //shareIntent.putExtra("android.intent.extra.TITLE", "Kindly join my team for effective spares tracking");
            shareIntent = Intent.createChooser(shareIntent, (CharSequence)candyName);
            if (shareIntent.resolveActivity(this.getPackageManager()) != null) {
                this.startActivity(shareIntent);
            }
        }
    }

    //cannot share image without storage permission
    private boolean checkIfStorageHasPermission(){
        return ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    //get permission to write storage for sharing picture
    private boolean requestAndAcquireStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        return ContextCompat.checkSelfPermission(this,
                "android.permission.CALL_PHONE") == 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length != 0 && grantResults[0] == 0) {
                shareCurrentCandy();
            } else {
                Toast.makeText(this, "Cannot share picture without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ***
    // TODO - Task 4 - Share the Current Candy with an Intent
    // ***
}
