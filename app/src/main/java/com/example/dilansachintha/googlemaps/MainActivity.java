package com.example.dilansachintha.googlemaps;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, PassengerMap.class);
        startActivity(intent);
/*
        if(isServicesOK()){
            init();
        }*/
    }
/*
    private void init(){
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if(availability == ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            Toast.makeText(MainActivity.this,"Google Play Services are working",Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availability)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,availability,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
*/
}
