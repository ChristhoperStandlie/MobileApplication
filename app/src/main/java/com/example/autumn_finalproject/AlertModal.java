package com.example.autumn_finalproject;

public class AlertModal {
    private int id;
    private String time; //Add time
    private String city;
    private String weather;
    private String temper;
    private String humid;
    private String wind;

    //setter-getter

    public int getId() {
        return id;
    }

    public String getTime() { //Time format 3hrs cycle
        String temp = String.valueOf(Integer.valueOf(time)/6);
        return temp;
    }

    public String getCity() {
        return city;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemper() {
        return temper;
    }

    public String getHumid() {
        return humid;
    }

    public String getWind() {
        return wind;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setTemper(String temper) {
        this.temper = temper;
    }

    public void setHumid(String humid) {
        this.humid = humid;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    //constructor
    public AlertModal(int id, String time, String city, String weather, String temper, String humid, String wind) {
        this.id = id;
        this.time = time;
        this.city = city;
        this.weather = weather;
        this.temper = temper;
        this.humid = humid;
        this.wind = wind;
    }

}
