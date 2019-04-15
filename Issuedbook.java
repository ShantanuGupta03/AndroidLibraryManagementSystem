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

public class Issuedbook extends AppCompatActivity {


    private RelativeLayout noissued,issueprog;
    private GridView issuegrid;
    private SharedPreferences sharedPreferences;
    private Gridviewadapter grid;
    private ArrayList<searchclass> searchclasses=new ArrayList<>();
    private DatabaseReference ref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuedbook);
        issuegrid=(GridView)findViewById(R.id.issuegrid);
        noissued=(RelativeLayout)findViewById(R.id.noissued);
        issueprog=(RelativeLayout)findViewById(R.id.issueprog);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        grid=new Gridviewadapter(searchclasses);
        issuegrid.setAdapter(grid);
        ref2 = FirebaseDatabase.getInstance().getReference().child("Books").child("Title");
        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("email", "").replaceAll("[.]", "_")).child("issued");
        issueprog.setVisibility(View.VISIBLE);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                  for(DataSnapshot k:dataSnapshot.getChildren())
                  {
                        getdetails(k.getKey());
                  }
                }
                else
                {
                    noissued.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class Gridviewadapter extends BaseAdapter {

        public ArrayList<searchclass> mainl;

        public Gridviewadapter(ArrayList<searchclass> mainl)
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
//            mainlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("name",lists.namesearch);
//                    Intent intent = new Intent(Issuedbook.this, viewbook.class);
//                    intent.putExtra("bookcode",lists.idsearch );
//                    intent.putExtra("from","issued");
//                    startActivity(intent);
//                }
//            });
            return view;
        }
    }


    public void getdetails(String s)
    {
        Log.i("value of key","value"+s);
        ref2.child(s).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshots) {
                if(dataSnapshots.exists())
                {
                    HashMap<String,String> nmap=(HashMap<String, String>) dataSnapshots.getValue();
                    searchclasses.add(new searchclass(nmap.get("name"), dataSnapshots.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
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
        grid.notifyDataSetChanged();
        if(searchclasses.size()==0)
        {
            noissued.setVisibility(View.VISIBLE);
        }
        issueprog.setVisibility(View.INVISIBLE);
    }
}
