package com.example.autumn_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AlertDBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "alert_DB";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "myCourse"; //IMPORTANT WAJIB

    private static final String ID_COL = "id";
    private static final String time_col = "time";
    private static final String city_col = "city";
    private static final String weather_col = "weather";
    private static final String temper_col = "temper";
    private static final String humid_col = "humid";
    private static final String wind_col = "wind";

    public AlertDBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + time_col + " TEXT, "
                + city_col + " TEXT, "
                + weather_col + " TEXT, "
                + temper_col + " TEXT, "
                + humid_col + " TEXT, "
                + wind_col + " TEXT )";
        db.execSQL(query); //execute above command

    }

    //insert Weather
    public void addNewAlert(String time, String city, String weather, String temper, String humid, String wind) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(time_col, time);
        values.put(city_col, city);
        values.put(weather_col, weather);
        values.put(temper_col, temper);
        values.put(humid_col, humid);
        values.put(wind_col, wind);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //select where ID = n
    public ArrayList<AlertModal> readAlerts() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<AlertModal> alertModalArrayList = new ArrayList<>() ;
        //AlertModal temp;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if(c.moveToFirst()) {
            do {
                alertModalArrayList.add(new AlertModal(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6)));
            } while (c.moveToNext());
            c.close();
        }
        return alertModalArrayList;
    }

    //Update Weather
    public void updateAlert(int ID, String time, String city, String weather, String temper, String humid, String wind) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String id_s = Integer.toString(ID);
        values.put(time_col, time);
        values.put(city_col, city);
        values.put(weather_col, weather);
        values.put(temper_col, temper);
        values.put(humid_col, humid);
        values.put(wind_col, wind);
        db.update(TABLE_NAME, values, "ID=?", new String[]{id_s}); //Change Hardcode ID = 1 to parameter
        db.close();
    }

    public void deleteAlert(int ID) {
        String id_s = String.valueOf(ID);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{id_s});
        db.close();
    }

    //check if table exist
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
