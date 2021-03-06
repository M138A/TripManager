package com.mghartgring.tripmanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TripOverviewActivity extends AppCompatActivity {

    private boolean running = false;
    private LocationManager locationManager;
    private TripLocator triplocator;
    public double distance = 0.0;
    public ArrayList<Location> locationList = new ArrayList<Location>();
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("New trip");
        database = new DatabaseHelper(getApplicationContext());
    }

    /**
     * Triggered when the 'Start trip' button is tapped
     * @param view The button
     */
    public void StartButtonClicked(View view) {

        String tripName = ((EditText) findViewById(R.id.tripName)).getText().toString();
        if (tripName.isEmpty()) {
            NoTripName();
            return;
        }
        ((Button) view).setText("Stop trip");

        if(running) {
            EndTrip(distance, tripName);
            locationManager.removeUpdates(triplocator);
            ((Button) view).setText("Start trip");
            ((EditText) findViewById(R.id.tripName)).setText("");
            running = !running;
            locationList.clear();
            return;
        }
        //Ask for all the permissions needed
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        running = !running;

    }

    /**
     * Adds the item to the database and ends the activity
     * @param dist Distance of the trip
     * @param tripName Name of the trip
     */
    private void EndTrip(double dist, String tripName)
    {
        database.InsertItem(tripName, dist);
        finish();
    }

    /**
     * Adds location to the list of locations
     * @param loc
     */
    public void AddPositionToList(Location loc)
    {
        final Location location = loc;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                locationList.add(location);
                UpdateDistance();
            }
        });
        ((TextView)findViewById(R.id.distanceText)).setText(String.valueOf(distance));
    }

    /**
     * Calculates the distance
     */
    private void UpdateDistance()
    {
        if(locationList.size() != 2) return;
        Location l1 =  locationList.get(0);
        Location l2 =  locationList.get(1);
        double dist = l1.distanceTo(l2);
        distance += dist;
        locationList.clear();
        locationList.add(l2);
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    StartTracking();
                } else {
                    Snackbar.make(this.getCurrentFocus(), "App needs permissions to use GPS and storage", Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Shows message that a name should be inserted
     */
    private void NoTripName() {
        Snackbar.make(this.getCurrentFocus(), "Please insert a name for the trip", Snackbar.LENGTH_LONG).show();
    }

    /**
     * Initializes the LocationListener
     */
    private void StartTracking() {
        triplocator = new TripLocator(this);
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, triplocator);
    }

}