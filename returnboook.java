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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class returnboook extends AppCompatActivity {

    private RelativeLayout noissued3,issueprog3;
    private GridView issuegrid3;
    private SharedPreferences sharedPreferences;
    private Gridviewadapter3 grids;
    private ArrayList<searchclass> searchclasses3=new ArrayList<>();
    private DatabaseReference ref2,ref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returnboook);
        issuegrid3=(GridView)findViewById(R.id.issuegrid3);
        noissued3=(RelativeLayout)findViewById(R.id.noissued3);
        issueprog3=(RelativeLayout)findViewById(R.id.issueprog3);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        grids=new Gridviewadapter3(searchclasses3);
        issuegrid3.setAdapter(grids);
        ref2 = FirebaseDatabase.getInstance().getReference().child("Books").child("Title");
        ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).child("issued");
        load();
    }

    public void load()
    {
        searchclasses3.clear();
        issueprog3.setVisibility(View.VISIBLE);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot k:dataSnapshot.getChildren())
                    {
                        getdetails3(k.getKey());
                    }
                }
                else
                {
                    noissued3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class Gridviewadapter3 extends BaseAdapter {

        public ArrayList<searchclass> mainl;

        public Gridviewadapter3(ArrayList<searchclass> mainl)
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
            final View view = layoutInflater.inflate(R.layout.gridlayout, null);
            final searchclass lists=mainl.get(position);
            final LinearLayout mainlist=(LinearLayout)view.findViewById(R.id.mainlayout);
            final ImageView image=(ImageView)view.findViewById(R.id.mainimage);
            final TextView texttitle=(TextView)view.findViewById(R.id.texttitle);
            final TextView price=(TextView)view.findViewById(R.id.price);
            Picasso.get().load(lists.imagesearch).resize(170,145).centerCrop().into(image);
            texttitle.setText(lists.namesearch);
            price.setText(lists.authorsearch);
            mainlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("name",lists.namesearch);
                    Intent intent = new Intent(returnboook.this, viewbook.class);
                    intent.putExtra("bookcode",lists.idsearch );
                    intent.putExtra("from","return");
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        load();
    }

    public void getdetails3(String s)
    {
        Log.i("value of key","value"+s);
        ref2.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if(dataSnapshots.exists())
                {
                    HashMap<String,String> nmap=(HashMap<String, String>) dataSnapshots.getValue();
                    searchclasses3.add(new searchclass(nmap.get("name"), dataSnapshots.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
                }
                setadapterk();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setadapterk()
    {
        grids.notifyDataSetChanged();
        if(searchclasses3.size()==0)
        {
            noissued3.setVisibility(View.VISIBLE);
        }
        issueprog3.setVisibility(View.INVISIBLE);
    }
}
