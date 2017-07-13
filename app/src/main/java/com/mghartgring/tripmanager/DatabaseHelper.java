package com.mghartgring.tripmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * Created by MarkPC on 12-7-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TABLE_CREATE =
            "CREATE TABLE " + "trips" + " (" +
                    "tripdate" + " DATETIME, " +
                    "tripname" + " TEXT, "+
                    "length" + " REAL);";
    DatabaseHelper(Context context){
        super(context, "TripDatabase", null, 2);

    }

    /**
     * Inserts trip into database
     * @param TripName Name of the trip
     * @param distance Travelled distance during the trip
     */
    public void InsertItem(String TripName, double distance)
    {
        String currentDate = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
        getWritableDatabase().execSQL("INSERT INTO trips VALUES ('" + currentDate + "','" + TripName + "', " + String.valueOf(distance) + ")");
    }

    /**
     * Removes all trips from the database
     */
    public void RemoveAll()
    {
        getWritableDatabase().execSQL("DELETE FROM trips");
    }

    /**
     * Gets all the trips from the database
     * @return An ArrayList containing all the trips
     */
    public ArrayList<Trip> GetData()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT  * FROM trips", null);
        ArrayList<Trip> data = new ArrayList<Trip>();
        if (cursor.moveToFirst()) {
            do {
                Trip current = new Trip(getValue(cursor, "tripdate"),getValue(cursor, "tripname"),Double.valueOf(getValue(cursor, "length")));
                data.add(current);
            } while (cursor.moveToNext());
        }
        return data;
    }

    /**
     * Gets the value from a Cursor
     * @param c The cursor
     * @param cName The name of the value
     * @return
     */
    private String getValue(Cursor c, String cName){
        return c.getString(c.getColumnIndex(cName));
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
        System.out.println("Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
