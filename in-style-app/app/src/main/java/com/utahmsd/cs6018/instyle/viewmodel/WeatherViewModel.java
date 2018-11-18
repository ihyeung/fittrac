package com.utahmsd.cs6018.instyle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.db.repo.WeatherRepository;

import java.util.ArrayList;
import java.util.Random;

public class WeatherViewModel extends AndroidViewModel {

    private static final String LOG = WeatherViewModel.class.getSimpleName();

    private final MediatorLiveData<Weather> m_observableWeather;

    private final WeatherRepository m_weatherRepository;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        InStyleDatabase database = InStyleDatabase.getDatabaseInstance(application);
        m_weatherRepository = WeatherRepository.getInstance(database);

        m_observableWeather = new MediatorLiveData<>();

        configureMediatorLiveData();
    }

    private void configureMediatorLiveData() {
        m_observableWeather.setValue(null);
        m_observableWeather.addSource(m_weatherRepository.getWeather(), data -> {
            if (data != null) {
                m_observableWeather.setValue(data);
            }
//            m_observableWeather.removeSource(m_weatherRepository.getWeather());

        });
    }

    public void loadWeather(String city, String country) {
        m_weatherRepository.fetchWeatherDataFromDataSource(city, country);
    }


    public void loadRandomWeather(ArrayList<Integer> weatherIdList) {
        Random random = new Random();
        m_weatherRepository.
                loadRandomWeatherData(weatherIdList.get(random.nextInt(weatherIdList.size())));

    }

    public LiveData<Weather> getWeather() {
        return m_observableWeather;
    }


}