package com.quicklib.quicklib;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myprofile extends AppCompatActivity {

    private EditText nameupdate, phoneupdate;
    private RelativeLayout updateprog;
    private SharedPreferences sharedPreferences;
    private String names, phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        nameupdate = (EditText) findViewById(R.id.nameupdate);
        Button update = (Button) findViewById(R.id.updatenow);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        phoneupdate = (EditText) findViewById(R.id.phoneupdate);
        updateprog = (RelativeLayout) findViewById(R.id.updateprog);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_"));
        updateprog.setVisibility(View.VISIBLE);
        ref.child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    names = dataSnapshot.child("fullname").getValue().toString();
                    phones = dataSnapshot.child("phonenumber").getValue().toString();
                    updatedetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nameupdate.getText().toString().equals("") && nameupdate.getText().toString().length() >= 3) {
                    if (!phoneupdate.getText().toString().equals("") && phoneupdate.getText().toString().length() >= 10) {
                         ref1.child("fullname").setValue(nameupdate.getText().toString());
                         ref1.child("phonenumber").setValue(phoneupdate.getText().toString());
                        sharedPreferences.edit().putString("fullname",nameupdate.getText().toString()).apply();
                        sharedPreferences.edit().putString("phonenumber",phoneupdate.getText().toString()).apply();
                        Toast.makeText(getApplicationContext(), "Details updated.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Enter a valid phone number.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter a valid Full name.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updatedetails() {
        nameupdate.setText(names);
        phoneupdate.setText(phones);
        updateprog.setVisibility(View.GONE);
    }
}
