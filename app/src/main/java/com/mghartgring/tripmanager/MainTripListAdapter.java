package com.mghartgring.tripmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MarkPC on 12-7-2017.
 */

public class MainTripListAdapter extends ArrayAdapter<Trip> {

    public MainTripListAdapter(Context context, ArrayList<Trip> trips) {
        super(context, 0, trips);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Trip t = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_list_layout, parent, false);
        }
        TextView TripName = (TextView) convertView.findViewById(R.id.tripNameListLabel);
        TextView TripLength = (TextView) convertView.findViewById(R.id.tripLengthListLabel);
        TripName.setText(t.TripName);
        TripLength.setText("Length: " + String.valueOf((int)Math.floor(t.Distance)) + " meters");
        return convertView;
    }
}
