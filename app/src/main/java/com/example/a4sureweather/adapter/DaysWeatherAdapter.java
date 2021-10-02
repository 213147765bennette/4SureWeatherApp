package com.example.a4sureweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a4sureweather.R;
import com.example.a4sureweather.entity.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * created by {Bennette Molepo} on {10/2/2021}.
 */
public class DaysWeatherAdapter  extends  RecyclerView.Adapter<DaysWeatherAdapter.MoreWeatherViewHolder>{

    private List<Weather> moreWeatherList = new ArrayList<>();
    private RecycleViewItemClickInterface recycleViewItemClickInterface;

    public DaysWeatherAdapter(List<Weather> moreWeatherList, RecycleViewItemClickInterface itemClickInterface) {
        this.moreWeatherList = moreWeatherList;
        this.recycleViewItemClickInterface = itemClickInterface;
    }

    @NonNull
    @Override
    public MoreWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewHolder = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_weather_days_listview,parent,false);
        return new MoreWeatherViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreWeatherViewHolder holder, int position) {

        holder.bind(moreWeatherList.get(position),recycleViewItemClickInterface);



    }


    public void setMoreWeather(List<Weather> moreWeatherList){
        this.moreWeatherList = moreWeatherList;
    }

    @Override
    public int getItemCount() {
        return moreWeatherList.size();
    }



    class MoreWeatherViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_weekdays;
        private ImageView tempIcon;
        private TextView txt_temperature;

        public MoreWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_weekdays = itemView.findViewById(R.id.txt_weekdays);
            tempIcon = itemView.findViewById(R.id.img_forecast);
            txt_temperature = itemView.findViewById(R.id.txt_temperature);



        }


        public void bind(final Weather data,final RecycleViewItemClickInterface listner){

            txt_weekdays.setText(data.getWeekDay());
            txt_temperature.setText(data.getTemperature());

            itemView.setOnClickListener(v -> {
                listner.onItemClicked(data,getAdapterPosition());
            });

            //will use the returned weather text to set the icon
            //txt_weekdays.setText(data.getWeekDay());
        }
    }

    public interface RecycleViewItemClickInterface{
        void onItemClicked(Weather weather, int adapterPosition);
    }
}
