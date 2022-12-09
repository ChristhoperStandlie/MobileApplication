package com.example.autumn_finalproject;

public class WeatherModal {
    private int id;
    private String city;
    private String weather;
    private String temper;
    private String humid;
    private String wind;

    //setter-getter

    public int getId() {
        return id;
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
    public WeatherModal(int id, String city, String weather, String temper, String humid, String wind) {
        this.id = id;
        this.city = city;
        this.weather = weather;
        this.temper = temper;
        this.humid = humid;
        this.wind = wind;
    }
}
