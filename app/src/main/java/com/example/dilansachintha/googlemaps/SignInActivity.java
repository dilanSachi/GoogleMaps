package com.example.dilansachintha.googlemaps;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        final EditText txtEmail = (EditText) findViewById(R.id.txt_email);
        final EditText txtPassword = (EditText) findViewById(R.id.txt_password);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                Toast.makeText(SignInActivity.this,"Sign in started",Toast.LENGTH_SHORT).show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignInActivity.this,"Database gave data",Toast.LENGTH_SHORT).show();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                } else {
                                    db.collection("users").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            if (document.getId().toString() == email && document.getData().get("Type") == "Passenger") {
                                                                Intent intent = new Intent(SignInActivity.this, PassengerInterface.class);
                                                            } else if (document.getId().toString() == email && document.getData().get("Type") == "Bus") {
                                                                Intent intent = new Intent(SignInActivity.this, BusInterface.class);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                }



                                    //Intent intent = new Intent(SignInActivity.this,)
                                    //Toast.makeText(, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);


                                // ...
                            }
                        });
            }
        });

    }

    public void updateUI(FirebaseUser user){

    }
}
