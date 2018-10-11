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
import com.google.firebase.auth.FirebaseAuth;

public class BusInterface extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_interface);

        Button btnMap = (Button) findViewById(R.id.btn_bus_map);
        Button btnSignOut = (Button) findViewById(R.id.btn_sign_out);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(BusInterface.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    private void init(){

        if(isServicesOK()){
            Intent intent = new Intent(BusInterface.this, BusMap.class);
            startActivity(intent);
        }else{

        }
    }

    public boolean isServicesOK(){
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(BusInterface.this);
        if(availability == ConnectionResult.SUCCESS){
            Toast.makeText(BusInterface.this,"Google Play Services are working",Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availability)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(BusInterface.this,availability,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
