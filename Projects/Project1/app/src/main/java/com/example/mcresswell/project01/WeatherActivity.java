package com.example.mcresswell.project01;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mcresswell.project01.weather.WeatherForecast;
import com.example.mcresswell.project01.weather.WeatherViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeatherActivity extends AppCompatActivity
        implements WeatherFragment.OnWeatherFragmentInteractionListener {

    private static final String LOG = WeatherActivity.class.getSimpleName();

    private WeatherViewModel weatherViewModel;
    private AtomicBoolean fragmentExists = new AtomicBoolean(false);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

//        FragmentManager manager = getSupportFragmentManager();
//
//        WeatherFragment fragment = savedInstanceState == null ?
//                WeatherFragment.newInstance(null) :
//                (WeatherFragment) manager.findFragmentById(R.id.fl_activity_weather);
//        FragmentTransaction fragmentTransaction = manager.beginTransaction();
////
//        fragmentTransaction.add(R.id.fl_activity_weather, fragment);
//        fragmentTransaction.hide(fragment);
//        fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.commit();

        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        weatherViewModel.getForecastData().observe(this, nameObserver);

        if (!fragmentExists.get() && savedInstanceState != null) {
            fragmentExists.set(true);
        }

        String city = getIntent().getStringExtra("city");
        String country = getIntent().getStringExtra("country");
        loadWeatherData(city, country);
    }

    final Observer<WeatherForecast> nameObserver  = new Observer<WeatherForecast>() {
        @Override
        public void onChanged(@Nullable final WeatherForecast weatherData) {
            if (weatherData != null) { //Weather data has finished being retrieved
                Log.d(LOG,"weatherData onChanged, weatherData no longer null");
                weatherData.printWeatherForecast();
                displayWeatherWidget(weatherData); //Wait til data loads to display fragment
            }
        }
    };

    void loadWeatherData(String city, String country){
        Log.d(LOG, "loadWeatherData");

        //pass the location in to the view model
        weatherViewModel.setLocation(city, country);
    }

    public void displayWeatherWidget(WeatherForecast weatherForecast) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        WeatherFragment fragment = !fragmentExists.get() ?
                WeatherFragment.newInstance(weatherForecast) :
                (WeatherFragment) fragmentManager.findFragmentById(R.id.fl_activity_weather);
        fragmentTransaction.replace(R.id.fl_activity_weather, fragment);
        fragmentTransaction.commit();
    }

//    public Map<String, String> createObjectMapper(WeatherForecast data) {
//        Map<String, String> mapper = new HashMap<String, String>();
//        mapper.put("", data.getCity() + "," + data.getCountryCode());
//        mapper.put(getResources().getString(R.string.current_conditions_weather_widget), data.getForecastMain());
//        mapper.put(getResources().getString(R.string.forecast_detail_weather_widget), data.getForecastDescription());
//        mapper.put(getResources().getString(R.string.temp_weather_widget), data.getTemp());
//        mapper.put(getResources().getString(R.string.temp_min_weather_widget), data.getTemp_min());
//        mapper.put(getResources().getString(R.string.temp_max_weather_widget), data.getTemp_max());
//        mapper.put(getResources().getString(R.string.humidity_weather_widget), data.getHumidity());
//        mapper.put(getResources().getString(R.string.wind_weather_widget), data.getWindSpeed());
//        mapper.put(getResources().getString(R.string.pressure_weather_widget), data.getPressure());
//
//        return mapper;
//    }

    @Override
    public void onWeatherFragmentInteraction(WeatherForecast forecast) {


    }
}
