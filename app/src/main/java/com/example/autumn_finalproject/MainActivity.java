package com.example.autumn_finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AlertAdapter.OnAlertClickListener {

    TextView weatherState, humidState, tempState, windState;
    ImageView weatherIcon;
    Button lokasi, addAlert;
    private DBHandler dbHandler;
    private AlertDBHandler dbAlerts;
    private RecyclerView weatherRecyclerView; //Dav
    private AlertAdapter alertAdapter;//Dav
    private WeatherModal temp; //Global variable
    private ArrayList<AlertModal> alertModalsArrayList ;

    @Override
    protected void onResume() {//TODO: DAV (not really, biar highlight aja). ini cuman saran aja biar intentnya ga kebanyakan. Terlalu banyak intent menurut gw, jadi gw pake ini
        super.onResume();
        alertModalsArrayList = new ArrayList<>();//Ganti jadi ini karena ada time
        dbHandler = new DBHandler(MainActivity.this);//DB for Weather Modal
        dbAlerts = new AlertDBHandler(MainActivity.this); // DB for Alerts Modal

        try { //Request Permission if not permitted
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dbHandler.addNewWeather(1, "N/A", "N/A", "N/A", "N/A", "N/A");
            temp = dbHandler.readWeathers(1); //Declare Weather modal
        } catch (Exception e) {
            e.printStackTrace();
        }


        //TODO: Testing DBRead
        /*alertModal = dbAlerts.readAlerts(1);
        System.out.println("1"+alertModal.getCity()+alertModal.getWeather());
        Log.i("Yea", "1"+alertModal.getCity()+alertModal.getWeather());
        alertModal = dbAlerts.readAlerts(2);
        System.out.println("2"+alertModal.getCity()+alertModal.getWeather());
        Log.i("Yea", "2"+alertModal.getCity()+alertModal.getWeather());*/

        if(temp.getCity()== null){
            lokasi.setText("Lokasi");
        }else {
            lokasi.setText(temp.getCity());
        }
        weatherState.setText(temp.getWeather());
        tempState.setText(temp.getTemper());
        humidState.setText(temp.getHumid());
        windState.setText(temp.getWind());
        if(temp.getWeather().contains("Clear")){
            weatherIcon.setImageResource(R.drawable.sunny1);
        }else if(temp.getWeather().contains("Clouds")){
            weatherIcon.setImageResource(R.drawable.overcast1);
        }else if(temp.getWeather().contains("Rain")){
            weatherIcon.setImageResource(R.drawable.rain1);
        }else if(temp.getWeather().contains("Thunderstorm")){
            weatherIcon.setImageResource(R.drawable.storm1);
        }else{
            weatherIcon.setImageResource(R.drawable.overcast1);
        }
        //TODO: Set auto detect juml Alerts. Use AlertModal (Udah)
        alertModalsArrayList = dbAlerts.readAlerts(); //DAV baca semua data di dalam database yg dihandle AlertDBHandler
//        weatherModalArrayList.add(dbHandler.readWeathers(2));//DAV ini ubah aja sesuka hati

        alertAdapter = new AlertAdapter(alertModalsArrayList, getApplicationContext(), this); //Dav masukin arr list dan context main ke adapter

        weatherRecyclerView.setAdapter(alertAdapter);

        alertAdapter.setOnAlertClickListener(this); //DAV set onclicklistener palsu

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherState = (TextView) findViewById(R.id.text_weather);
        humidState = (TextView) findViewById(R.id.text_humidity);
        tempState = (TextView) findViewById(R.id.text_temperature);
        weatherIcon = (ImageView) findViewById(R.id.img_cuaca);
        windState = (TextView) findViewById(R.id.text_wind);
        lokasi = (Button) findViewById(R.id.btn_location);
        addAlert = (Button) findViewById(R.id.btn_addAlert);
        weatherRecyclerView = (RecyclerView) findViewById(R.id.weatherRecyclerView);  //Dav
        weatherRecyclerView.setHasFixedSize(true);//Dav
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));//Dav




        lokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), chooseLocation.class);
                startActivity(i);
            }
        });
        addAlert.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddAlert.class);
                startActivity(i);
            }
        }));
    }

    @Override
    public void onDeleteClick(int position) { //DAV
        dbAlerts.deleteAlert(alertModalsArrayList.get(position).getId());//Dav hapus dari database
        alertModalsArrayList.remove(position) ;
        alertAdapter.notifyItemRemoved(position);
    }//DAV
}