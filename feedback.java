package com.quicklib.quicklib;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class feedback extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        final EditText feeed=(EditText)findViewById(R.id.feededit);
        Button feedbutton=(Button)findViewById(R.id.feedbutton);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("feedback");
        feedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!feeed.getText().toString().equals("") && feeed.getText().toString().length()>1)
                {
                  ref1.child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).setValue(feeed.getText().toString());
                    Toast.makeText(getApplicationContext(),"Feedback Submitted",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter a valid string",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
