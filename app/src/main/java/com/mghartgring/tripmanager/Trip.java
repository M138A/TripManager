package com.mghartgring.tripmanager;

import java.io.Serializable;

/**
 * Created by MarkPC on 12-7-2017.
 */

public class Trip implements Serializable {
    public String Date;
    public String TripName;
    public double Distance;

    public Trip(String Date, String TripName, double Distance)
    {
        this.Date = Date;
        this.TripName = TripName;
        this.Distance = Distance;
    }

}
