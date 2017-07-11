package com.mghartgring.tripmanager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.text.CollationElementIterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by MarkPC on 11-7-2017.
 */

public class TripLocator implements LocationListener {

    private TripOverviewActivity toa;

    public TripLocator(TripOverviewActivity act) {toa = act;}

    @Override
    public void onLocationChanged(Location loc) {
        final Location location = loc;
        toa.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toa.AddPositionToList(location);
            }
        });
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
