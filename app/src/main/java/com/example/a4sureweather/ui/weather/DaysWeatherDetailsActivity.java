package com.example.a4sureweather.ui.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.a4sureweather.R;
import com.example.a4sureweather.adapter.DaysWeatherAdapter;
import com.example.a4sureweather.entity.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DaysWeatherDetailsActivity extends AppCompatActivity implements DaysWeatherAdapter.RecycleViewItemClickInterface{

    private DaysWeatherDetailsViewModel mViewModel;
    List<Weather> moreWeatherList;
    RecyclerView recyclerView;
    DaysWeatherAdapter daysWeatherAdapter;
    private String TAG = "DaysWeatherDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_weather_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("Days forecast");

        recyclerView = (RecyclerView) findViewById(R.id.moreCustomableListview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        moreWeatherList = new ArrayList<>();
        //for demo testing data
        for(int k=0; k<4; k++){
            moreWeatherList.add(k,new Weather("Monday","Sun","25"));
        }

        daysWeatherAdapter = new DaysWeatherAdapter(moreWeatherList,this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(daysWeatherAdapter);

        daysWeatherAdapter.setMoreWeather(moreWeatherList);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClicked(Weather weather, int adapterPosition) {

        Log.d(TAG,"LISt is clicked...at position: "+adapterPosition);
        Intent intent = new Intent(this,MoreDetailsActivity.class);

        startActivity(intent);
    }
}