package com.example.autumn_finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;


public class chooseLocation extends AppCompatActivity {

    EditText inputLocation;
    Button chooseLoc, getLoc;
    //TextView city, weather, temper, humid, wind;
    //TextView txt_lat, txt_lon;
    private DBHandler dbHandler;
    GpsTracker gpsTracker;
    ProgressDialog progressDialog ;

    private String cityTemp, weatherTemp, temperatureTemp, humidityTemp, windTemp; //DAV pasti ga akan ditampilin di text view sini kan? variable itu bisa buat assign ke database/ pass ke activity main, dll sesuai kebutuhan

    private final String apiKey = "APPID=d558cd5956417860a943c0df0a197172";
    private final String units = "&units=metric"; //Unit metric/imperial
    private final String url1 = "http://api.openweathermap.org/data/2.5/weather?" + apiKey + units;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        inputLocation = findViewById(R.id.inputLocation);

        chooseLoc = findViewById(R.id.btn_chooseLoc);
        getLoc = findViewById(R.id.btn_currentLoc);
        dbHandler = new DBHandler(chooseLocation.this); //DB Access

//        city = (TextView) findViewById(R.id.txt_city);
//        weather = (TextView) findViewById(R.id.txt_weather);
//        temper = (TextView) findViewById(R.id.txt_temp);
//        humid = (TextView) findViewById(R.id.txt_humid);
//        wind = (TextView) findViewById(R.id.txt_wind);
//        txt_lat = (TextView) findViewById(R.id.txt_lat);
//        txt_lon = (TextView) findViewById(R.id.txt_lon);

        cityTemp = null; //DAV kalo" belum ada isinya
        weatherTemp = null;//DAV
        temperatureTemp = null;//DAV
        humidityTemp = null; //DAV
        windTemp = null;//DAV

        chooseLoc.setOnClickListener(new View.OnClickListener() { //Get Weather With City Name
            @Override
            public void onClick(View view) {
                String temp = String.valueOf(inputLocation.getText());
                String param = "&q=" + temp; //DAV parameternya nama kota
                try {
                    getData(param); //DAV fetch JSON data based on url + param (paramnya aja soalnya cuman itu yang beda)
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        getLoc.setOnClickListener(new View.OnClickListener() { //Get Weather with current location
            @Override
            public void onClick(View view) {
                try {
                    gpsTracker = new GpsTracker(chooseLocation.this);
                    if (gpsTracker.canGetLocation()) {
                        progressDialog = new ProgressDialog(chooseLocation.this) ;
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Getting data...");
                        progressDialog.show();
                        Thread.sleep(2000); //Ada bug kalau input kecepetan, maka outputnya Globe. jadi gw pake delay
                        String lat = getLocs(1); //DAV asign getLocs lat
                        String lon = getLocs(2); //DAV asign getLocs lon
    //                  txt_lat.setText(lat);
    //                  txt_lon.setText(lon);

                        String param = "&lon=" + lon + "&lat=" + lat; //DAV parameternya longitude dan latitude. jadi url+ param juga

                        getData(param);//DAV untuk fetch data based on lon and lat
                    }else{
                        gpsTracker.showSettingsAlert();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }

    private void getData(String param) throws UnsupportedEncodingException { //DAV ini fungsi utk get data
        AsyncHttpClient client = new AsyncHttpClient();
        String finalUrl = URLEncoder.encode(url1 + param, "UTF-8"); //DAV encode biar pencariannya lebih cepet
        client.get(finalUrl, new JsonHttpResponseHandler() { //DAV make a request
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //DAV kalau success, get datanya berdasarkan parameter response bentuk JSONObject
                try {
                    //DAV masukin seluruh data yang dibutuhkan ke variabel. Kalau2 mau di assign ke database, dll sesuai kebutuhan
                    cityTemp = response.getString("name");
                    weatherTemp = response.getJSONArray("weather").getJSONObject(0).optString("description");
                    weatherTemp = toTitleCase(weatherTemp); //Title case Weather
                    temperatureTemp = response.getJSONObject("main").optString("temp") + "°C"; //current temp add °C
                    humidityTemp = response.getJSONObject("main").optString("humidity") + "%"; //Add %
                    int windDeg = Integer.valueOf(response.getJSONObject("wind").optString("deg"));
                    String winDir_s = wind_direction(windDeg);
                    windTemp = response.getJSONObject("wind").optString("speed") + " m/s " + winDir_s;
                    //DAV set data tadi ke text view. Bisa dihapus kalau mau
//                    city.setText(cityTemp);
//                    weather.setText(weatherTemp);
//                    temper.setText(temperatureTemp);
//                    humid.setText(humidityTemp);
//                    wind.setText(windTemp);
                    //abis ini bisa update databasenya atau pass ke main activity via intent.
                    dbHandler.updateWeather(1, cityTemp, weatherTemp, temperatureTemp, humidityTemp, windTemp); //Update DB after API request

                    //DAV engga perlu intent
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(chooseLocation.this, "Failed to get data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getLocs(int ID) { //ID  1 = lat, ID 2 = lon
        String t_lat = "0";
        String t_lon = "0";
        gpsTracker = new GpsTracker(chooseLocation.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            t_lat = String.valueOf(latitude);
            t_lon = String.valueOf(longitude);
        } else {
            gpsTracker.showSettingsAlert();
        }
        if (ID == 1) {
            return t_lat;
        } else if (ID == 2) {
            return t_lon;
        } else {
            return "0";
        }
    }

    public static String toTitleCase(String input) {//Capilalize Weather Name
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }

    public String wind_direction(int degree) {
        String temp = "n/a";
        if (degree >= 336 || degree <= 25) {
            temp = "N";
        } else if (degree >= 26 && degree <= 65) {
            temp = "NE";
        } else if (degree >= 66 && degree <= 115) {
            temp = "E";
        } else if (degree >= 116 && degree <= 155) {
            temp = "SE";
        } else if (degree >= 156 && degree <= 205) {
            temp = "S";
        } else if (degree >= 206 && degree <= 245) {
            temp = "SW";
        } else if (degree >= 246 && degree <= 295) {
            temp = "W";
        } else if (degree >= 296 && degree <= 335) {
            temp = "NW";
        }
        return temp;
    }

}
