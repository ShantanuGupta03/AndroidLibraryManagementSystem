package com.quicklib.quicklib;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class splash extends AppCompatActivity {

    private Integer count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        int SPLASH_DISPLAY_LENGTH = 2500;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                if(CheckInternetConnection.checkConnection(getApplicationContext()))
                {
                    startActivity(new Intent(getApplicationContext(),login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
                else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(splash.this);
                    builder.setMessage("No Internet.").setTitle("Important Notification");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishAffinity();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void onBackPressed() {
        count++;
        if(count==1)
        {
            Toast.makeText(getApplicationContext(),"Press one more time to exit.",Toast.LENGTH_LONG).show();
        }
        else if(count==2)
        {
            finishAffinity();
        }
    }
}
