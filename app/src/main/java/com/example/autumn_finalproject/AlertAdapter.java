package com.example.autumn_finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertAdapterViewHolder>{

    //private ArrayList<WeatherModal> courseModalArrayList;
    private ArrayList<AlertModal> alertModalsArrayList ;
    private Context cont ;
    private OnAlertClickListener onAlertClickListener ;//DAV

    public AlertAdapter(ArrayList<AlertModal> alertModalsArrayList, Context cont, OnAlertClickListener onAlertClickListener) {
        this.alertModalsArrayList = alertModalsArrayList;
        this.cont = cont;
        this.onAlertClickListener = onAlertClickListener ;//DAV
    }

    @NonNull
    @Override
    public AlertAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cont).inflate(R.layout.alertitem, parent, false);
        return new AlertAdapterViewHolder(view, onAlertClickListener); //DAV
    }


    @Override
    public void onBindViewHolder(@NonNull AlertAdapterViewHolder holder, int position) {
        AlertModal alertModal = alertModalsArrayList.get(position) ;
        holder.time.setText(alertModal.getTime() + " day(s) ahead"); //Dav days aheadnya ditambah di sini
        holder.city.setText(alertModal.getCity());
        holder.weather.setText(alertModal.getWeather());
        holder.temperature.setText(alertModal.getTemper());
        holder.humidity.setText(alertModal.getHumid());
        holder.wind.setText(alertModal.getWind());
    }

    @Override
    public int getItemCount() {
        return alertModalsArrayList.size();
    }

    public class AlertAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView city, weather, temperature, humidity, wind, time ;
        ImageView deleteBtn ;//DAV
        OnAlertClickListener onAlertClickListener ; //DAV
        public AlertAdapterViewHolder(@NonNull View itemView, OnAlertClickListener onAlertClickListener) { //DAV
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.theTime) ;
            city = (TextView) itemView.findViewById(R.id.theCity);
            weather = (TextView) itemView.findViewById(R.id.theWeather) ;
            temperature = (TextView) itemView.findViewById(R.id.theTemperature) ;
            humidity = (TextView) itemView.findViewById(R.id.theHumidity) ;
            wind = (TextView) itemView.findViewById(R.id.theWind);
            deleteBtn = (ImageView) itemView.findViewById(R.id.btn_delete) ;//DAV

            this.onAlertClickListener = onAlertClickListener ;

            deleteBtn.setOnClickListener(new View.OnClickListener() {//DAV
                @Override
                public void onClick(View view) { //DAV
                    onAlertClickListener.onDeleteClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnAlertClickListener{ //DAV
        void onDeleteClick(int position) ;
    }
    public void setOnAlertClickListener(OnAlertClickListener onAlertClickListener){
        this.onAlertClickListener = onAlertClickListener ;
    }
}
