package com.example.dilansachintha.googlemaps;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;


public class PassengerMap extends AppCompatActivity {

    private static final String TAG = "PassengerMap";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String[] bus_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passenger);

        Button btn_route_search = (Button) findViewById(R.id.btn_search_route);
        final TextInputEditText txt_route = (TextInputEditText)findViewById(R.id.txt_route_no);
        //final TextView txt_bus = (TextView) findViewById(R.id.txt_list_bus);
        final Button btn_map = (Button) findViewById(R.id.btnMap);
        btn_map.setVisibility(View.INVISIBLE);

        btn_route_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String route = txt_route.getText().toString();

                if(route == ""){
                    Toast.makeText(PassengerMap.this, "Enter a Route No", Toast.LENGTH_SHORT).show();
                }else {
                    db.collection("routes").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());

                                            //Toast.makeText(PassengerMap.this, document.getId(), Toast.LENGTH_SHORT).show();

                                            Map<String,Object> data = document.getData();
                                            //System.out.println(data.containsKey("Bus")+"fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");

                                            List<String> group = (List<String>) data.get("Bus");

                                            if(document.getId().equals(route)){
                                                Toast.makeText(PassengerMap.this, "Found the Route No", Toast.LENGTH_SHORT).show();

                                                Toast.makeText(PassengerMap.this, group.get(0), Toast.LENGTH_SHORT).show();

                                                LinearLayout routeLayout = (LinearLayout) findViewById(R.id.linear_layout_bus);

                                                bus_id = new String[group.size()];

                                                for(int i=0; i< group.size();i++){
                                                    TextView textView = new TextView(PassengerMap.this);
                                                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                                    textView.setText(group.get(i));
                                                    textView.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
                                                    routeLayout.addView(textView);

                                                    bus_id[i] =(String ) group.get(i);
                                                }

                                                btn_map.setVisibility(View.VISIBLE);

                                            }else{
                                                Toast.makeText(PassengerMap.this, "Coudn't find a Route No", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }


            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });

    }

    private void init(){

        if(isServicesOK()){
            Intent intent = new Intent(PassengerMap.this, MapActivity.class);
            intent.putExtra("bus_id", bus_id);
            startActivity(intent);
        }else{

        }
    }

    public boolean isServicesOK(){
        Log.d(TAG,"isServicesOK: checking google services version");
        int availability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(PassengerMap.this);
        if(availability == ConnectionResult.SUCCESS){
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            Toast.makeText(PassengerMap.this,"Google Play Services are working",Toast.LENGTH_SHORT).show();
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(availability)){
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(PassengerMap.this,availability,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
