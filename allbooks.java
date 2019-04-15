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

public class allbooks extends AppCompatActivity {

    private RelativeLayout nobooks, allprog;
    private GridView allgrid;
    private SharedPreferences sharedPreferences;
    private Gridviewadapterall grid;
    private ArrayList<searchclass> allclass = new ArrayList<>();
    private DatabaseReference ref2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allbooks);
        allgrid = (GridView) findViewById(R.id.allgrid);
        nobooks = (RelativeLayout) findViewById(R.id.nobooks);
        allprog = (RelativeLayout) findViewById(R.id.allprog);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        grid = new Gridviewadapterall(allclass);
        allgrid.setAdapter(grid);
        ref2 = FirebaseDatabase.getInstance().getReference().child("Books").child("Title");
        allprog.setVisibility(View.VISIBLE);
        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        HashMap<String, String> nmap = (HashMap<String, String>) s.getValue();
                        allclass.add(new searchclass(nmap.get("name"), s.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
                    }
                    updateallbooks();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class Gridviewadapterall extends BaseAdapter {

        public ArrayList<searchclass> mainl;

        public Gridviewadapterall(ArrayList<searchclass> mainl) {
            this.mainl = mainl;
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
            final searchclass lists = mainl.get(position);
            final LinearLayout mainlist = (LinearLayout) view.findViewById(R.id.mainlayout);
            final ImageView image = (ImageView) view.findViewById(R.id.mainimage);
            final TextView texttitle = (TextView) view.findViewById(R.id.texttitle);
            final TextView price = (TextView) view.findViewById(R.id.price);
            Picasso.get().load(lists.imagesearch).resize(170, 145).centerCrop().into(image);
            texttitle.setText(lists.namesearch);
            price.setText(lists.authorsearch);
            mainlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("name", lists.namesearch);
                    Intent intent = new Intent(allbooks.this, viewbook.class);
                    intent.putExtra("bookcode", lists.idsearch);
                    intent.putExtra("from","allbooks");
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    private void updateallbooks()
    {
        grid.notifyDataSetChanged();
        if(allclass.size()==0)
        {
            nobooks.setVisibility(View.VISIBLE);
        }
        allprog.setVisibility(View.INVISIBLE);
    }

}
