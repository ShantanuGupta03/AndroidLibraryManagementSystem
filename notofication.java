package com.quicklib.quicklib;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class notofication extends AppCompatActivity {


    private ListView listView;
    private RelativeLayout notprog,nonotify;
    private ArrayList<notifyclass> notifylist=new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private listviewadapter listd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notofication);
        listView=(ListView)findViewById(R.id.listnotify);
        notprog=(RelativeLayout)findViewById(R.id.notprog);
        nonotify=(RelativeLayout)findViewById(R.id.nonotify);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        listd=new listviewadapter(notifylist);
        listView.setAdapter(listd);
        notprog.setVisibility(View.VISIBLE);
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).child("notify");
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot k:dataSnapshot.getChildren()){
                        notifylist.add(new notifyclass(k.getValue().toString()));
                    }
                    setadapterks();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public class listviewadapter extends BaseAdapter {

        public ArrayList<notifyclass> mainl;

        public listviewadapter(ArrayList<notifyclass> mainl)
        {
            this.mainl=mainl;
        }

        @Override
        public int getCount() {
            return mainl.size();
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
            final View view = layoutInflater.inflate(R.layout.semesterlist, null);
            final notifyclass lists=mainl.get(position);
            final TextView texttitle=(TextView)view.findViewById(R.id.semstertext);
            texttitle.setText(lists.notifystring);
            return view;
        }
    }

    public void setadapterks()
    {
        listd.notifyDataSetChanged();
        if(notifylist.size()==0)
        {
            nonotify.setVisibility(View.VISIBLE);
        }
        notprog.setVisibility(View.INVISIBLE);
    }
}
