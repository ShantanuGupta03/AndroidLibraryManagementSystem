package com.quicklib.quicklib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class signup extends AppCompatActivity {

    private EditText email1, password1, fullname, phonenumber;
    private FirebaseAuth mAuth;
    private String value;
    private SharedPreferences sharedPreferences;
    private Button register;
    private RelativeLayout progresss;
    private NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email1 = (EditText) findViewById(R.id.email1);
        mAuth = FirebaseAuth.getInstance();
        password1 = (EditText) findViewById(R.id.password1);
        fullname = (EditText) findViewById(R.id.fullname);
        register=(Button)findViewById(R.id.register);
        phonenumber = (EditText) findViewById(R.id.phonenumber);
        progresss = (RelativeLayout) findViewById(R.id.progresss);
        manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{ "Student","Faculty"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner1.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                value=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                value="Student";
            }
        });
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("users");
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email1.getText().toString().equals("") && validEmail(email1.getText().toString())) {
                    if (!password1.getText().toString().equals("") && password1.getText().toString().length() >= 8) {
                        if (!fullname.getText().toString().equals("") && fullname.getText().toString().length() >= 3) {
                            if (!phonenumber.getText().toString().equals("") && phonenumber.getText().toString().length() >= 10) {
                                if(CheckInternetConnection.checkConnection(getApplicationContext()))
                                {
                                    progresss.setVisibility(View.VISIBLE);
                                    mAuth.createUserWithEmailAndPassword(email1.getText().toString(),password1.getText().toString()).addOnCompleteListener(
                                            new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        ref.child(email1.getText().toString().replaceAll("[.]","_")).child("email").setValue(email1.getText().toString());
                                                        ref.child(email1.getText().toString().replaceAll("[.]","_")).child("fullname").setValue(fullname.getText().toString());
                                                        ref.child(email1.getText().toString().replaceAll("[.]","_")).child("phonenumber").setValue(phonenumber.getText().toString());
                                                        ref.child(email1.getText().toString().replaceAll("[.]","_")).child("designation").setValue(value);
                                                        ref.child(email1.getText().toString().replaceAll("[.]","_")).child("notify").push().setValue("Welcome to QuickLib.You are all set.Start searching now.");
                                                        NotificationCompat.Builder n=new NotificationCompat.Builder(getApplicationContext()).setContentTitle("QuickLib").setSmallIcon(R.drawable.smallicon).setContentText("Welcome to QuickLib.You are all set.Start searching now.");

                                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                                                        {
                                                            int importance = NotificationManager.IMPORTANCE_HIGH;
                                                            NotificationChannel notificationChannel = new NotificationChannel("10001", "NOTIFICATION_CHANNEL_NAME", importance);
                                                            notificationChannel.enableLights(true);
                                                            notificationChannel.setLightColor(Color.RED);
                                                            notificationChannel.enableVibration(true);
                                                            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                                            assert manager != null;
                                                            n.setChannelId("10001");
                                                            manager.createNotificationChannel(notificationChannel);
                                                        }
                                                        manager.notify(0,n.build());
                                                        sharedPreferences.edit().putString("email",email1.getText().toString()).apply();
                                                        sharedPreferences.edit().putString("password",password1.getText().toString()).apply();
                                                        sharedPreferences.edit().putString("fullname",fullname.getText().toString()).apply();
                                                        sharedPreferences.edit().putString("phonenumber",phonenumber.getText().toString()).apply();
                                                        sharedPreferences.edit().putString("designation",value).apply();
                                                        sharedPreferences.edit().putString("login","1").apply();
                                                        progresss.setVisibility(View.GONE);
                                                        startActivity(new Intent(getApplicationContext(),landingpage.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(getApplicationContext(), "Failed to register.", Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            }
                                    );
                                }else
                                {
                                    Toast.makeText(getApplicationContext(), "No Internet.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Enter a valid phone number.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter a valid Full name.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Password must be minimum 8 character.", Toast.LENGTH_LONG).show();
                    }
                } else {
                   Toast.makeText(getApplicationContext(), "Enter valid email.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
