package com.quicklib.quicklib;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {


    private EditText email, password;
    private FirebaseAuth mAuth;
    private RelativeLayout progress;
    private String name, phone, designation;
    private Integer count = 0;
    private SharedPreferences sharedPreferences;
    private Button email_signup, get_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        if (sharedPreferences.getString("login", "").equals("1")) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                startActivity(new Intent(getApplicationContext(), landingpage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            } else {
                Toast.makeText(getApplicationContext(), "Login session expired", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        } else {
            email = (EditText) findViewById(R.id.email);
            password = (EditText) findViewById(R.id.password);
            progress = (RelativeLayout) findViewById(R.id.progress);
            email_signup = (Button) findViewById(R.id.email_signup);
            get_start = (Button) findViewById(R.id.get_started);
            sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
            email_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                        if (CheckInternetConnection.checkConnection(getApplicationContext())) {
                            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                progress.setVisibility(View.VISIBLE);
                                                ref.child(email.getText().toString().replaceAll("[.]", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            name = dataSnapshot.child("fullname").getValue().toString();
                                                            designation = dataSnapshot.child("designation").getValue().toString();
                                                            phone = dataSnapshot.child("phonenumber").getValue().toString();
                                                            updatedetails(email.getText().toString(), password.getText().toString());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Invalid username/password.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                            );
                        } else {
                            Toast.makeText(getApplicationContext(), "No Internet.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Empty fields.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            get_start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), signup.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            });
        }
    }

    private void updatedetails(String em, String pass) {
        sharedPreferences.edit().putString("phone", phone).apply();
        sharedPreferences.edit().putString("email", em).apply();
        sharedPreferences.edit().putString("password", pass).apply();
        sharedPreferences.edit().putString("fullname", name).apply();
        sharedPreferences.edit().putString("designation", designation).apply();
        sharedPreferences.edit().putString("login", "1").apply();
        progress.setVisibility(View.GONE);
        startActivity(new Intent(getApplicationContext(), landingpage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }


    @Override
    public void onBackPressed() {
        count++;
        if (count == 1) {
            Toast.makeText(getApplicationContext(), "Press one more time to exit.", Toast.LENGTH_LONG).show();
        } else if (count == 2) {
            finishAffinity();
        }
    }
}
