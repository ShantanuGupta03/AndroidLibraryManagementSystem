package com.quicklib.quicklib;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class landingpage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String searchvalue;
    private SharedPreferences sharedPreferences;
    private Button searchq;
    private Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landingpage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("quick.com", MODE_PRIVATE);
        final AutoCompleteTextView searchkeyword = (AutoCompleteTextView) findViewById(R.id.searchkeyword);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        String[] items = new String[]{"Title Search", "Author Search", "Semester search"};
        searchq = (Button) findViewById(R.id.searchq);
        String[] countries = getResources().getStringArray(R.array.countries_array);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner1.setAdapter(adapter);
        ArrayAdapter<String> adapterl =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries);
        searchkeyword.setAdapter(adapterl);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchvalue = parent.getItemAtPosition(position).toString();
                Log.i("quick1", searchvalue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchvalue = "Title Search";
                Log.i("quick1", searchvalue);
            }
        });

        searchq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchkeyword.getText().toString().equals("") && searchkeyword.getText().toString().length() > 0) {
                    Intent intent = new Intent(landingpage.this, searchresult.class);
                    intent.putExtra("type", searchvalue);
                    intent.putExtra("keyword", searchkeyword.getText().toString());
                    startActivity(intent);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView usernames = (TextView) header.findViewById(R.id.nameuser);
        usernames.setText(sharedPreferences.getString("fullname", ""));
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (count == 1) {
                Toast.makeText(getApplicationContext(), "Press one more time to exit.", Toast.LENGTH_SHORT).show();
            } else {
                finishAffinity();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.landingpage, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), notofication.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.action_allbooks) {
            startActivity(new Intent(getApplicationContext(), allbooks.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_issue) {
            startActivity(new Intent(getApplicationContext(), Issuedbook.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(getApplicationContext(), myprofile.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.nav_allbooks) {
            startActivity(new Intent(getApplicationContext(), allbooks.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else if (id == R.id.nav_help) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(landingpage.this);
            builder.setMessage("Mail us at quicklib@gmail.com or call us at +181656585225.").setTitle("Get in Touch");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if (id == R.id.nav_logout) {
            sharedPreferences.edit().putString("login", "0").apply();
            startActivity(new Intent(getApplicationContext(), login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        else if (id == R.id.nav_return) {
            startActivity(new Intent(getApplicationContext(), returnboook.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        else if (id == R.id.nav_feedback) {
            startActivity(new Intent(getApplicationContext(), feedback.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        else if (id == R.id.nav_fine) {
            startActivity(new Intent(getApplicationContext(), finecalculator.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
