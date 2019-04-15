package com.quicklib.quicklib;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class viewbook extends AppCompatActivity {

    private ArrayList<searchclass> searchclasses = new ArrayList<>();
    private String namesearch;
    private String isbnsearch;
    private String descriptionsearch;
    private String semestersearch;
    private String imagesearch;
    private String authorsearch;
    private SharedPreferences sharedPreferences;
    private ImageView imageView;
    private TextView bookname, bookauthor, bookdesc, booksem, isbn,finedetails,bookfine;
    private boolean iss = false;
    private String timeissued,from,date2;
    private Button issue;
    private RelativeLayout detailprog;
    private SimpleDateFormat dateFormat,dateFormat1;
    private NotificationManager manager;
    private int ps=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbook);
        Intent intes = getIntent();
        imageView = (ImageView) findViewById(R.id.imageview);
        bookname = (TextView) findViewById(R.id.bookname);
        bookauthor = (TextView) findViewById(R.id.bookauthor);
        bookdesc = (TextView) findViewById(R.id.bookdescription);
        booksem = (TextView) findViewById(R.id.booksemester);
        finedetails = (TextView) findViewById(R.id.finedetails);
        bookfine=(TextView)findViewById(R.id.bookfine);
        isbn = (TextView) findViewById(R.id.isbn);
        dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateFormat1=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        detailprog = (RelativeLayout) findViewById(R.id.detailprog);
        manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_"));
        issue = (Button) findViewById(R.id.issue);
        final String bookcode = intes.getStringExtra("bookcode");
         from = intes.getStringExtra("from");
        TextView reviewtextbox=(TextView)findViewById(R.id.reviewtextbox);
        reviewtextbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              startActivity(new Intent(getApplicationContext(),feedonbooks.class).putExtra("bookcode",bookcode).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books").child("Title");
        ref.child(bookcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("value name", dataSnapshot.getKey() + "name" + dataSnapshot.child("name").getValue());
                    namesearch = dataSnapshot.child("name").getValue().toString();
                    isbnsearch = dataSnapshot.child("ISBN").getValue().toString();
                    descriptionsearch = dataSnapshot.child("Description").getValue().toString();
                    semestersearch = dataSnapshot.child("semester").getValue().toString();
                    imagesearch = dataSnapshot.child("image").getValue().toString();
                    authorsearch = dataSnapshot.child("author").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("issued").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot k : dataSnapshot.getChildren()) {
                        if (k.getKey() != null) {
                            timeissued=k.getValue().toString();
                            if (k.getKey().equals(bookcode)) {
                                iss = true;
                            }
                        }
                        calculatedate();
                    }
                }
                setvalue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iss) {
                    reference.child("issued").child(bookcode).removeValue();
                    Toast.makeText(getApplicationContext(), "Book returned", Toast.LENGTH_LONG).show();
                    NotificationCompat.Builder n=new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Book Returned").setSmallIcon(R.drawable.smallicon).setContentText("Book Returned named "+namesearch + " on "+dateFormat1.format(Calendar.getInstance().getTime()));

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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(viewbook.this);
                    builder.setMessage("Book Returned successful.").setTitle("Returned");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    reference.child("notify").push().setValue("Book Returned named "+namesearch + " on "+dateFormat1.format(Calendar.getInstance().getTime()));
                    issue.setText("Issue Book");
                    iss=false;
                } else {
                    Calendar c=Calendar.getInstance();
                    String date=dateFormat.format(c.getTime());
                    if(sharedPreferences.getString("designation","").equals("Student")) {
                        c.add(Calendar.DAY_OF_YEAR, 7);
                    }else if(sharedPreferences.getString("designation","").equals("Faculty"))
                    {
                        c.add(Calendar.DAY_OF_YEAR, 15);
                    }
                    date2=dateFormat1.format(c.getTime());
                    reference.child("issued").child(bookcode).setValue(date);
                    NotificationCompat.Builder n=new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Book Issued").setSmallIcon(R.drawable.smallicon).setContentText("Book Issued named "+namesearch+" with return date "+date2);

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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(viewbook.this);
                    builder.setMessage("Book Issued named "+namesearch+" with return date "+date2).setTitle("Returned");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    reference.child("notify").push().setValue("Book Issued named "+namesearch+" with return date "+date2);
                    Toast.makeText(getApplicationContext(), "Book Issued", Toast.LENGTH_LONG).show();
                    issue.setText("Return Book");
                    iss = true;
                }
            }
        });
    }

    private void calculatedate()
    {

        Log.i("time called",timeissued);
        if(iss)
        {
            Log.i("time issued",timeissued);
            Date d= null;
            try {
                d = dateFormat.parse(timeissued);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c1=Calendar.getInstance();
                c1.setTime(d);
                if(sharedPreferences.getString("designation","").equals("Student")) {
                    c1.add(Calendar.DAY_OF_YEAR, 7);
                }else if(sharedPreferences.getString("designation","").equals("Faculty"))
                {
                    c1.add(Calendar.DAY_OF_YEAR, 15);
                }
                String d1=dateFormat1.format(c1.getTime());
                finedetails.setText("Book Return Date > "+ d1);
            Calendar c2=Calendar.getInstance();
            long daysdiff=printDifference1(d,c2.getTime());
            Log.i("fine is","diff "+daysdiff);
            if(daysdiff >= 1)
            {
                ps=(((int) daysdiff))*10;
                Log.i("fine is","fine"+ps);
            }
            bookfine.setVisibility(View.VISIBLE);
            bookfine.setText("Fine value is :"+ ps);
            final AlertDialog.Builder builder = new AlertDialog.Builder(viewbook.this);
            builder.setMessage("Your Fine for this book is Rs. "+ps).setTitle("Fine Alert");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void setvalue() {
        if (iss) {
            issue.setText("Return Book");
        }
        Picasso.get().load(imagesearch).resize(170, 145).centerCrop().into(imageView);
        bookname.setText(namesearch);
        bookdesc.setText(descriptionsearch);
        booksem.setText(semestersearch);
        bookauthor.setText("Author: " + authorsearch);
        isbn.setText("ISBN: " + isbnsearch);
        detailprog.setVisibility(View.GONE);

    }

    public long printDifference1(Date startDate, Date endDate) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        return  elapsedHours;
    }
}
