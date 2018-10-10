package com.example.dilansachintha.googlemaps;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class MapUpdater extends Thread {

    private boolean check = true;

    public void setCheck(boolean check) {
        this.check = check;
    }

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Activity activity;
    private FirebaseFirestore dbase;

    public MapUpdater(FusedLocationProviderClient mFusedLocationProvider, Activity activity, FirebaseFirestore dbase){
        this.mFusedLocationProviderClient = mFusedLocationProvider;
        this.activity = activity;
        this.dbase = dbase;
    }

    public void update() {

        final Activity myActivity = activity;

        final FirebaseFirestore db = dbase;

        if (ActivityCompat.checkSelfPermission(myActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(myActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task location = mFusedLocationProviderClient.getLastLocation();

        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Location location1 = (Location) task.getResult();

                    GeoPoint geoPoint = new GeoPoint(location1.getLatitude(), location1.getLongitude());

                    DocumentReference docRef = db.collection("users").document("NQGImEUFnXuvLINNSd7K");

                    docRef.update("Location", geoPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(myActivity, "Updated location successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(myActivity, "Updating location failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(myActivity, "Updating failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void run() {

        while (check) {
            try {
                Thread.sleep(2000);
                update();
                //Toast.makeText(MapActivity.this,"Thread wakened",Toast.LENGTH_SHORT).show();
                System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Toast.makeText(activity,"Thread wakened",Toast.LENGTH_SHORT).show();
    }
}
