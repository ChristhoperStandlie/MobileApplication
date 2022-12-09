package com.example.autumn_finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "autumn_db";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "myCourses"; //IMPORTANT WAJIB

    private static final String ID_COL = "id";
    private static final String city_col = "city";
    private static final String weather_col = "weather";
    private static final String temper_col = "temper";
    private static final String humid_col = "humid";
    private static final String wind_col = "wind";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + city_col + " TEXT, "
                + weather_col + " TEXT, "
                + temper_col + " TEXT, "
                + humid_col + " TEXT, "
                + wind_col + " TEXT )";
        db.execSQL(query); //execute above command

    }

    //insert Weather
    public void addNewWeather(int ID, String city, String weather, String temper, String humid, String wind) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_COL, ID);
        values.put(city_col, city);
        values.put(weather_col, weather);
        values.put(temper_col, temper);
        values.put(humid_col, humid);
        values.put(wind_col, wind);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    //select where ID = n
    public WeatherModal readWeathers(int ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        WeatherModal temp = null;
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE ID = " + ID, null);
        if(c.moveToFirst()){
            temp = new WeatherModal(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5)) ;
        }
        c.close();
        return temp;
    }

    //Update Weather
    public void updateWeather(int ID, String city, String weather, String temper, String humid, String wind) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String id_s = Integer.toString(ID);
        values.put(city_col, city);
        values.put(weather_col, weather);
        values.put(temper_col, temper);
        values.put(humid_col, humid);
        values.put(wind_col, wind);
        db.update(TABLE_NAME, values, "ID=?", new String[]{id_s}); //Change Hardcode ID = 1 to parameter
        db.close();
    }

    //check if table exist
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
