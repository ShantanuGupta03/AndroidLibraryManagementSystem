package com.quicklib.quicklib;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class searchresult extends AppCompatActivity {

    private RelativeLayout norecord,searchprog;
    private ArrayList<searchclass> searchclasses1=new ArrayList<>();
    private GridView gridview;
    private Gridviewadapters grid;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        Intent intes=getIntent();
        type=intes.getStringExtra("type");
        final String keyword=intes.getStringExtra("keyword");
        norecord=(RelativeLayout)findViewById(R.id.norecord);
        searchprog=(RelativeLayout)findViewById(R.id.searchprog);
        searchprog.setVisibility(View.VISIBLE);
        gridview=(GridView)findViewById(R.id.gridview);
        grid=new Gridviewadapters(searchclasses1);
        gridview.setAdapter(grid);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Books");
            ref.child("Title").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot s3:dataSnapshot.getChildren())
                        {
                            HashMap<String,String> nmap=(HashMap<String, String>) s3.getValue();
                            if(type.equals("Title Search")) {
                                if (nmap.get("name").toLowerCase().contains(keyword.toLowerCase())) {
                                    Log.i("search matched", nmap.get("name") + "key" + s3.getKey());
                                    searchclasses1.add(new searchclass(nmap.get("name"), s3.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
                                }
                            }else if(type.equals("Author Search"))
                            {
                                if (nmap.get("author").toLowerCase().contains(keyword.toLowerCase())) {
                                    Log.i("search matched", nmap.get("name") + "key" + s3.getKey());
                                    searchclasses1.add(new searchclass(nmap.get("name"), s3.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
                                }
                            }
                            else if(type.equals("Semester search"))
                            {
                                if (nmap.get("semester").toLowerCase().contains(keyword.toLowerCase()) || nmap.get("Description").toLowerCase().contains(keyword.toLowerCase())) {
                                    Log.i("search matched", nmap.get("name") + "key" + s3.getKey());
                                    searchclasses1.add(new searchclass(nmap.get("name"), s3.getKey(), nmap.get("ISBN"), nmap.get("Description"), nmap.get("semester"), nmap.get("image"), nmap.get("author")));
                                }
                            }
                        }
                        setadapterks();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    public class Gridviewadapters extends BaseAdapter {

        public ArrayList<searchclass> mainl;

        public Gridviewadapters(ArrayList<searchclass> mainl)
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
                    Intent intent = new Intent(searchresult.this, viewbook.class);
                    intent.putExtra("bookcode",lists.idsearch );
                    intent.putExtra("from","search");
                    startActivity(intent);
                }
            });
            return view;
        }
    }

    public void setadapterks()
    {
        grid.notifyDataSetChanged();
        if(searchclasses1.size()==0)
        {
            norecord.setVisibility(View.VISIBLE);
        }
        searchprog.setVisibility(View.INVISIBLE);
    }
}
