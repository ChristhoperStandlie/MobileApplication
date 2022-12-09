package com.example.autumn_finalproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.Header;

public class AddAlert extends AppCompatActivity {
    EditText inputLocation, inputTime;
    Button chooseLoc, getLoc;
    private AlertDBHandler dbHandler;
    GpsTracker gpsTracker;
    ProgressDialog progressDialog ;

    private String cityTemp, weatherTemp, temperatureTemp, humidityTemp, windTemp; //DAV pasti ga akan ditampilin di text view sini kan? variable itu bisa buat assign ke database/ pass ke activity main, dll sesuai kebutuhan

    private final String apiKey = "APPID=d558cd5956417860a943c0df0a197172";
    private final String units = "&units=metric"; //Unit metric/imperial
    private final String url1 = "http://api.openweathermap.org/data/2.5/forecast?" + apiKey + units;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);
        inputLocation = findViewById(R.id.inputLocationAlert);
        inputTime = findViewById(R.id.inputTimeAlert);

        chooseLoc = findViewById(R.id.btn_chooseLocAlert);
        getLoc = findViewById(R.id.btn_currentLocAlert);
        dbHandler = new AlertDBHandler(AddAlert.this); //DB Access

        cityTemp = null; //DAV kalo" belum ada isinya
        weatherTemp = null;//DAV
        temperatureTemp = null;//DAV
        humidityTemp = null; //DAV
        windTemp = null;//DAV

        chooseLoc.setOnClickListener(new View.OnClickListener() { //Get Weather With City Name
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(AddAlert.this) ;
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Getting data...");
                progressDialog.show();

                String query = String.valueOf(inputLocation.getText());
                String cnt;
                if(!String.valueOf(inputTime.getText()).equals("")){ //TODO: DAV (highlight aja) kondisinya gw ganti ya ngab
                    cnt = String.valueOf(inputTime.getText());
                    int cnt_n = Integer.valueOf(cnt) * 6;
                    cnt = String.valueOf(cnt_n);
                }else{
                    cnt = "6";
                }
                String param = "&q=" + query + "&cnt=" + cnt; //DAV parameternya nama kota
                try {
                    getData(param, Integer.parseInt(cnt)); //DAV fetch JSON data based on url + param (paramnya aja soalnya cuman itu yang beda)
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            }
        });

        getLoc.setOnClickListener(new View.OnClickListener() { //Get Weather with current location
            @Override
            public void onClick(View view) {
                try {
                    gpsTracker = new GpsTracker(AddAlert.this);
                    if (gpsTracker.canGetLocation()) {
                        progressDialog = new ProgressDialog(AddAlert.this) ;
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Getting data...");
                        progressDialog.show();

                        Thread.sleep(2000) ;//Masih ngebug sih kalau input terlalu cepet
                        //menurut gw harus set delay di sini. Kalau kecepetan klik "Get location" waktu gps baru nyala, datanya ga akan dapet.
                        String lat = getLocs(1); //DAV asign getLocs lat
                        String lon = getLocs(2); //DAV asign getLocs lon
                        String cnt;
                        if (!String.valueOf(inputTime.getText()).equals("")) {
                            cnt = String.valueOf(inputTime.getText());
                            int cnt_n = Integer.valueOf(cnt) * 6;
                            cnt = String.valueOf(cnt_n);
                        } else {
                            cnt = "6";
                        }

                        String param = "&lon=" + lon + "&lat=" + lat + "&cnt=" + cnt; //DAV parameternya longitude dan latitude.
                        getData(param, Integer.parseInt(cnt));//DAV untuk fetch data based on lon and lat
                    //DAV Intentnya jangan di sini. Try itu synchronous, jadi segala statement di luar try bakal dijalanin duluan
                    //DAV databasenya sebetulnya keupdate, tapi keliatannya engga, karena intentnya yang dijalanin terlebih dahulu
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
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

    }

    private void getData(String param, int cnt) throws UnsupportedEncodingException { //DAV ini fungsi utk get data
        AsyncHttpClient client = new AsyncHttpClient();
        String finalUrl = URLEncoder.encode(url1 + param, "UTF-8"); //DAV encode biar pencariannya lebih cepet
        client.get(finalUrl, new JsonHttpResponseHandler() { //DAV make a request
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) { //DAV kalau success, get datanya berdasarkan parameter response bentuk JSONObject
                try {
                    //DAV masukin seluruh data yang dibutuhkan ke variabel. Kalau2 mau di assign ke database, dll sesuai kebutuhan
                    cityTemp = response.getJSONObject("city").getString("name");
                    //System.out.println(cityTemp);
                    weatherTemp = response.getJSONArray("list").getJSONObject(cnt-1).getJSONArray("weather").getJSONObject(0).getString("description");
                    weatherTemp = toTitleCase(weatherTemp); //Title case Weather
                    temperatureTemp = response.getJSONArray("list").getJSONObject(cnt-1).getJSONObject("main").optString("temp") + "°C"; //current temp add °C
                    humidityTemp = response.getJSONArray("list").getJSONObject(cnt-1).getJSONObject("main").optString("humidity") + "%"; //Add %
                    int windDeg = Integer.valueOf(response.getJSONArray("list").getJSONObject(cnt-1).getJSONObject("wind").optString("deg"));
                    String winDir_s = wind_direction(windDeg);
                    windTemp = response.getJSONArray("list").getJSONObject(cnt-1).getJSONObject("wind").optString("speed") + " m/s " + winDir_s;
                    String time = String.valueOf(cnt);
                    dbHandler.addNewAlert(time, cityTemp, weatherTemp, temperatureTemp, humidityTemp, windTemp); //Update DB after API request

                    //DAV engga perlu intent
                    //System.out.println(cityTemp);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    finish();//DAV biar activity ga kebanyakan
                } catch (JSONException e) {
                    e.printStackTrace(); //TODO: DI sini error kalau input location: England dan days ahead: 7. 6 masih bisa
                    Toast.makeText(AddAlert.this, "Reached maximum days ahead! Cannot fetch data", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddAlert.this, "Failed to get data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getLocs(int ID) { //ID  1 = lat, ID 2 = lon
        String t_lat = "0";
        String t_lon = "0";
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
