package com.quicklib.quicklib;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class feedonbooks extends AppCompatActivity {


    private RelativeLayout feedreviewprog,feedreviewnotify;
    private ListView listfeed;
    private listviewadapters555 feedreviewlistadaper;
    private ArrayList<feedreview> tempfeed=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private String bookcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedonbooks);
        Intent i=getIntent();
        bookcode=i.getStringExtra("bookcode");
        feedreviewnotify=(RelativeLayout)findViewById(R.id.feedreviewnotify);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        feedreviewprog=(RelativeLayout)findViewById(R.id.feedreviewprog);
        listfeed=(ListView)findViewById(R.id.listfeed);
        feedreviewlistadaper=new listviewadapters555(tempfeed);
        listfeed.setAdapter(feedreviewlistadaper);
        loadreview();
        final EditText feedreviewedit=(EditText)findViewById(R.id.feedreviewedit);
        Button feedreviewbutton=(Button)findViewById(R.id.feedreviewbutton);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Books").child("Title");
        feedreviewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!feedreviewedit.getText().toString().equals("") && feedreviewedit.getText().toString().length()>1)
                {
                    ref1.child(bookcode).child("review").child(sharedPreferences.getString("fullname","")).setValue(feedreviewedit.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loadreview();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please enter a valid string",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadreview(){
        feedreviewprog.setVisibility(View.VISIBLE);
        feedreviewnotify.setVisibility(View.INVISIBLE);
        tempfeed.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books").child("Title");
        ref.child(bookcode).child("review").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot k:dataSnapshot.getChildren()) {
                        tempfeed.add(new feedreview(k.getKey(),k.getValue().toString()));
                    }
                    setvalues();
                }
                else
                {
                    feedreviewnotify.setVisibility(View.VISIBLE);
                    feedreviewprog.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class listviewadapters555 extends BaseAdapter {

        public ArrayList<feedreview> mainl89;

        public listviewadapters555(ArrayList<feedreview> mainl)
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
            final View view = layoutInflater.inflate(R.layout.feedreviewlayout, null);
            final feedreview lists=mainl89.get(position);
            final TextView texttitlename=(TextView)view.findViewById(R.id.feedreviewnames);
            final TextView texttitletext=(TextView)view.findViewById(R.id.feedreviewtext);
            texttitlename.setText(lists.feedreviewname);
            texttitletext.setText(lists.feedreviewstring);
            return view;
        }
    }

    private void setvalues()
    {
        feedreviewlistadaper.notifyDataSetChanged();
        if(tempfeed.size()==0)
        {
            feedreviewnotify.setVisibility(View.VISIBLE);
        }
        feedreviewprog.setVisibility(View.INVISIBLE);
    }
}
