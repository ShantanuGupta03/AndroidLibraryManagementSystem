package com.quicklib.quicklib;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class finecalculator extends AppCompatActivity {

    private RelativeLayout fineprog,finenotify;
    private ListView fineview;
    private SharedPreferences sharedPreferences;
    private ArrayList<calclass> notifylists=new ArrayList<>();
    private listviewadapters listds;
    private DatabaseReference ref2;
    private SimpleDateFormat dateFormat;
    private int po=0,p=0;
    private TextView finalfine;
    private Date d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finecalculator);
        fineprog=(RelativeLayout)findViewById(R.id.fineprog);
        finenotify=(RelativeLayout)findViewById(R.id.finenotify);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        fineview=(ListView)findViewById(R.id.listfine);
        listds=new listviewadapters(notifylists);
        fineview.setAdapter(listds);
        fineprog.setVisibility(View.VISIBLE);
        finalfine=(TextView)findViewById(R.id.finalfine);
        dateFormat=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ref2 = FirebaseDatabase.getInstance().getReference().child("Books").child("Title");
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).child("issued");
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot k:dataSnapshot.getChildren())
                    {
                        getdetails1(k.getKey(),k.getValue().toString());
                    }
                }
                else
                {
                    finenotify.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class listviewadapters extends BaseAdapter {

        public ArrayList<calclass> mainl89;

        public listviewadapters(ArrayList<calclass> mainl)
        {
            this.mainl89=mainl;
        }

        @Override
        public int getCount() {
            return mainl89.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LayoutInflater layoutInflater = getLayoutInflater();
            final View view = layoutInflater.inflate(R.layout.finelistview, null);
            final calclass lists=mainl89.get(position);
            final TextView texttitle=(TextView)view.findViewById(R.id.finebooktext);
            texttitle.setText(lists.finebooknames + "    ->   Rs. "+lists.finebookvalue);
            Log.i("fine value","value : "+lists.finebookvalue);
            return view;
        }
    }

    public void getdetails1(String s, final String value)
    {
        try {
            d=dateFormat.parse(value);
//            if(sharedPreferences.getString("designation","").equals("Student"))
//            {
//
//            }else if(sharedPreferences.getString("designation","").equals("Faculty"))
//            {
//                Calendar c=Calendar.getInstance();
//                long daysdiff=printDifference(d,c.getTime());
//                Log.i("fine is","diff "+daysdiff);
//                if(daysdiff > 15)
//                {
//                    p=(((int) daysdiff)-15)*10;
//                    Log.i("fine is","fine"+p);
//                }
//                po=po+p;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar c=Calendar.getInstance();
        long daysdiff=printDifference(d,c.getTime());
        Log.i("fine is","diff "+daysdiff);
        if(daysdiff >= 1)
        {
            p=(((int) daysdiff))*10;
        }
        else if(daysdiff == 0)
        {
            p=0;
            Log.i("fine is","fine less than"+p);
        }
        Log.i("fine is","fine"+p);
        po=po+p;
        final int val=p;
        Log.i("value of key","value"+s);
        ref2.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if(dataSnapshots.exists())
                {
                    HashMap<String,String> nmap=(HashMap<String, String>) dataSnapshots.getValue();
                    Log.i("name of book","value"+nmap.get("name")+ "final fine"+p +"and "+po);
                    notifylists.add(new calclass(nmap.get("name"),String.valueOf(val)));
                }
                setadapterk();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public long printDifference(Date startDate, Date endDate) {
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

    public void setadapterk()
    {
        Log.i("name of book","called");
        finalfine.setText("Rs. "+String.valueOf(po));
        listds.notifyDataSetChanged();
        if(notifylists.size()==0)
        {
            finenotify.setVisibility(View.VISIBLE);
        }
        fineprog.setVisibility(View.INVISIBLE);
    }

}
