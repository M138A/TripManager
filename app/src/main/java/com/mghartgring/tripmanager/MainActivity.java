package com.mghartgring.tripmanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static java.lang.System.in;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseHelper dbh;
    private ArrayList<Trip> arrayOfTrips = new ArrayList<Trip>();
    private MainTripListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final MainActivity t = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(t, TripOverviewActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        InitDataList();
        InitUI();
    }
    private void InitUI()
    {
        double TotalDrivenKilometers = 0.0;
        for(int i = 0; i < adapter.getCount(); i++){
            Trip current = adapter.getItem(i);
            TotalDrivenKilometers += current.Distance;
        }
        ((TextView) findViewById(R.id.travelledDistanceText)).setText(String.valueOf(TotalDrivenKilometers) + " meters");
        double fuelPrice = (Double.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getAll().get("fuel_price").toString()));
        ((TextView) findViewById(R.id.fuelPriceText)).setText("€ " + fuelPrice);
        double amountToPay = fuelPrice * (TotalDrivenKilometers / 1000);
        ((TextView) findViewById(R.id.outstandingText)).setText("€ " + Math.round(amountToPay * 100.0) / 100.0);
    }

    private void InitDataList()
    {
        ListView lv = (ListView) findViewById(R.id.TripList);
        dbh = new DatabaseHelper(getApplicationContext());
        adapter = new MainTripListAdapter(this, arrayOfTrips);
        lv.setAdapter(adapter);
        adapter.addAll(dbh.GetData());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final MainActivity t = this;
            startActivity(new Intent(t, Settings.class));
            return true;
        }
        else if(id == R.id.action_refresh){
            refreshTable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshTable(){
        ArrayList<Trip> trips = dbh.GetData();
        adapter.clear();
        adapter.addAll(trips);
        InitUI();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.trip_overview) {
            refreshTable();
        } else if (id == R.id.settings) {
            final MainActivity t = this;
            startActivity(new Intent(t, Settings.class));
        } else if (id == R.id.export) {
            final MainActivity t = this;
            Intent intent = new Intent(t, ExportActivity.class);
            intent.putExtra("trips", arrayOfTrips);
            startActivity(intent);
            
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
