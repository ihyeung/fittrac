package com.utahmsd.cs6018.instyle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.db.repo.WeatherRepository;

import java.util.List;
import java.util.Random;

public class WeatherListViewModel extends AndroidViewModel {

    private final MediatorLiveData<List<Weather>> m_observableWeatherList;

    private final WeatherRepository m_weatherRepository;

    public WeatherListViewModel(@NonNull Application application) {
        super(application);

        InStyleDatabase database = InStyleDatabase.getDatabaseInstance(application);
        m_weatherRepository = WeatherRepository.getInstance(database);

        m_observableWeatherList = new MediatorLiveData<>();

        configureMediatorLiveData();
    }
    private void configureMediatorLiveData() {
        m_observableWeatherList.setValue(null);

        LiveData<List<Weather>> weatherData = m_weatherRepository.getAllWeather();

        m_observableWeatherList.addSource(weatherData, m_observableWeatherList::setValue);
    }

    public LiveData<List<Weather>> getWeatherDataFromDatabase() {
        return  m_observableWeatherList;
    }

}
