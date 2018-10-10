package com.example.dilansachintha.googlemaps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final float DEFAULT_ZOOM = 15;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private boolean mLocationPermissionGranted = false;
    private GoogleMap gMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private static Location myLocation;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting device location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            myLocation = currentLocation;
                            System.out.println(currentLocation.toString());
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: failed to get current location");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving camera to : lnt: " + latLng.latitude + " lng : " + latLng.longitude);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "initMap: Initializing map");
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();

        Button btnStartJourney = (Button) findViewById(R.id.btnStartJourney);
        Button btnGetAnotherLocation = (Button) findViewById(R.id.btnGetOtherLocation);
        final Button btnStopJourney = (Button) findViewById(R.id.btnStopJourney);

        btnStartJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MapUpdater mapUpdater = new MapUpdater(mFusedLocationProviderClient,MapActivity.this, db);

                mapUpdater.start();

                btnStopJourney.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapUpdater.setCheck(false);
                    }
                });


                //Map<String, Object> user = new HashMap<>();

                        //new GeoPoint(myLocation.getLatitude(),myLocation.getLongitude());
                //user.put("name", "Ada");
                //user.put("Location", geoPoint);

                /*db.collection("users")
                        .add(user)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(MapActivity.this,"Added data",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(MapActivity.this,"Failed to add data",Toast.LENGTH_SHORT).show();
                            }
                        });*/
            }
        });

        btnGetAnotherLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DocumentReference docRef = db.collection("users").document("NQGImEUFnXuvLINNSd7K");



                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

                    final Marker marker = gMap.addMarker(new MarkerOptions().position(new LatLng(6.5,79)).title("Hey"));

                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            Toast.makeText(MapActivity.this, "Location get failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            Log.d(TAG, "Current data: " + snapshot.getData());
                            Map<String,Object> data = snapshot.getData();
                            GeoPoint loc = (GeoPoint) data.get("Location");
                            Toast.makeText(MapActivity.this,"longi "+loc.getLatitude(), Toast.LENGTH_SHORT).show();

                            marker.setPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
                            Toast.makeText(MapActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                            //marker.setTitle("Hello world");

                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });


                /*

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        GeoPoint point = document.getGeoPoint("Location");
                                        gMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                                                .title("Hello world"));
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });*/
            }
        });
    }

    private void getLocationPermission() {

        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(this.getApplicationContext()
                , FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission denied");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        Log.d(TAG, "onMapReady: Map is Ready");
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
}
