package com.pluralsight.candycoded;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class InfoActivity extends AppCompatActivity {

    private static final int PERMISSIONS_CALL_PHONE = 123;

    private Intent callIntent = new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:(012) 345-6789"));

    TextView coOrdinateText,phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        coOrdinateText = (TextView) findViewById(R.id.text_view_address);
        phoneText = (TextView) findViewById(R.id.text_view_phone);
        coOrdinateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGoogleMapsActivity();
            }
        });

        phoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIfUserHasPermission()){
                    startActivity(callIntent);
                } else {
                    if (requestAndAcquireCallPermission()){
                        Log.e("TAG", "CALLING NUMBER");
                    } else {
                        makeToastNoApplicationFoundToPerformAction();
                    }
                }
            }
        });

        Uri uri = Uri.parse("android.resource://com.codeschool.candycoded/" + R.drawable.store_front);
        ImageView candyStoreImageView = (ImageView)findViewById(R.id.image_view_candy_store);
        Picasso.with(this).
                load(uri).
                into(candyStoreImageView);


    }

    private void makeToastNoApplicationFoundToPerformAction(){
        Toast.makeText(this, "No application to perform this action", Toast.LENGTH_SHORT).show();
    }

    private boolean checkIfUserHasPermission(){
        return ContextCompat.checkSelfPermission(this,
                "android.permission.CALL_PHONE") == 0;
    }

    //For automatic dialing of numbers
    private boolean requestAndAcquireCallPermission(){
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.CALL_PHONE"},
                PERMISSIONS_CALL_PHONE);
        return ContextCompat.checkSelfPermission(this,
                "android.permission.CALL_PHONE") == 0;
    }

    // ***
    // TODO - Task 2 - Launch the Google Maps Activity
    // ***
    private void startGoogleMapsActivity(){
        Uri mapNigeriaUri = Uri.parse("geo:28.538053, -81.368545");
        Intent googleMapsIntent = new Intent(Intent.ACTION_VIEW, mapNigeriaUri);
        googleMapsIntent.setPackage("com.google.android.apps.maps");
        if (googleMapsIntent.resolveActivity(this.getPackageManager()) != null) {
            startActivity(googleMapsIntent);
        } else {
            makeToastNoApplicationFoundToPerformAction();
        }
    }

    // ***
    // TODO - Task 3 - Launch the Phone Activity
    // ***
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CALL_PHONE) {
            if (grantResults.length != 0 && grantResults[0] == 0) {
                startActivity(callIntent);
            }
        }
    }
}
