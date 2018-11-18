package com.utahmsd.cs6018.instyle.db.repo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.dao.WeatherDao;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.weather.WeatherClient;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.utahmsd.cs6018.instyle.util.WeatherUtils.DEFAULT_CITY;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.DEFAULT_COUNTRY;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.formatCaseCity;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.formatCaseCountryCodeFromCountryName;
import static com.utahmsd.cs6018.instyle.weather.WeatherClient.fetchCurrentWeather;

/**
 * Repository/model class for the Weather entity that handles all business logic associated
 * handling Weather data.
 * This class interfaces with the in-memory database, and handles API calls to the
 * OpenWeatherMap API to retrieve live weather data. Data that is retrieved and/or modified is
 * passed to the WeatherViewModel class.
 */

public class WeatherRepository {

    private static final String LOG_TAG = WeatherRepository.class.getSimpleName();

    public static final long DATA_REFRESH_INTERVAL = 300_000L; //If weather data is older than 5 minutes, refetch data
    private static final String DUMMY_CITY = "Salt Lake City";
    private static final String DUMMY_COUNTRY = "United States";

    private WeatherDao mWeatherDao;
    private InStyleDatabase inStyleDatabase;
    private static WeatherRepository weatherRepository;
    private static WeatherClient weatherClient = new WeatherClient(); //For fetching real-time weather data from API

    private MediatorLiveData<List<Weather>> m_observableWeatherList;
    private MediatorLiveData<Weather> m_observableWeather;

    private WeatherRepository(final InStyleDatabase database) {
        inStyleDatabase = database;
        mWeatherDao = inStyleDatabase.weatherDao();

//        asyncResetWeatherDatabase();

        //Retrieve additional data to insert into the database by making API calls to fetch real-time weather
//
//        asyncFetchWeatherFromApi("Tokyo", "Japan", false);
//        asyncFetchWeatherFromApi("Seoul", "Korea", false);
//        asyncFetchWeatherFromApi("Hong Kong", "Hong Kong", false);
//        asyncFetchWeatherFromApi("NEW YORK", "United States", false);
//        asyncFetchWeatherFromApi("Salt Lake City", "United States", false);
//        asyncFetchWeatherFromApi("Rome", "Italy", false);

        addLiveDataListenerSources();

        asyncLoadWeatherDataFromDatabase();

    }

    private void addLiveDataListenerSources() {
        m_observableWeatherList = new MediatorLiveData<>();
        m_observableWeatherList.setValue(null);

        m_observableWeather = new MediatorLiveData<>();
        m_observableWeather.setValue(null);

        m_observableWeatherList.addSource(mWeatherDao.loadAllWeather(),
                weatherList -> {
                    if (weatherList != null) {
                        if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
                            m_observableWeatherList.removeSource(mWeatherDao.loadAllWeather());
                            m_observableWeatherList.setValue(weatherList);

                            List<String> weatherCities = new ArrayList<>();
                            Log.d(LOG_TAG, "Weather REPOSITORY HAS FINISHED LOADING WEATHER DATA FROM DATABASE");
                            Log.d(LOG_TAG, "Number of records in Weather database: " + weatherList.size());

                            Log.d(LOG_TAG, "------------------------------------------");
                            Log.d(LOG_TAG, "------------------------------------------");

                            Log.d(LOG_TAG, "PRINTING WEATHER IN WEATHER DATABASE");
                            Log.d(LOG_TAG, "\n");

                            weatherList.forEach(w -> {
                                weatherCities.add(w.getCity());
                                Log.d(LOG_TAG, "\nWeather data record: " + w.getId() + "\t'" + w.getCity() + "'\t'" + w.getCountryCode() + "'\t'" + w.getLastUpdated() + "'\t'" + "'");
                            });

                            Log.d(LOG_TAG, "\n");
                            Log.d(LOG_TAG, "------------------------------------------");
                            Log.d(LOG_TAG, "------------------------------------------");

                            if (!weatherCities.contains(DUMMY_CITY)) {
                                Log.d(LOG_TAG, "Adding dummy weather data");
                                asyncFetchWeatherFromApi(DUMMY_CITY, DUMMY_COUNTRY, false);
                            }
                        }

                    }
                });

    }


    /**
     * Static method to ensure only one instance of the WeatherRepository is instantiated.
     * @param database
     * @return
     */
    public static WeatherRepository getInstance(final InStyleDatabase database) {
        if (weatherRepository == null) {
            synchronized (WeatherRepository.class) {
                if (weatherRepository == null) {
                    weatherRepository = new WeatherRepository(database);
                }
            }
        }
        return weatherRepository;
    }



    private boolean isWeatherDataExpired(Weather weather) {
        if (weather == null) {
            return false;
        }
        Instant lastUpdated = weather.getLastUpdated().toInstant();
        long differential = Instant.now().toEpochMilli() - lastUpdated.toEpochMilli();
        Log.d(LOG_TAG, "Weather data was last fetched " + differential/1000 + " seconds ago");

        return  differential > DATA_REFRESH_INTERVAL;
    }

    public void fetchWeatherDataFromDataSource(String city, String country) {
        String cityScrubbed = formatCaseCity(city);
        String countryScrubbed = formatCaseCountryCodeFromCountryName(country);

        LiveData<Weather> result = findInDatabase(city, country);

        m_observableWeather.addSource(result, r-> {
            m_observableWeather.removeSource(result);

                if (result.getValue() != null && result.getValue().getCity().equals(cityScrubbed) && result.getValue().getCountryCode().equals(countryScrubbed)) {
                    Log.d(LOG_TAG, String.format("Existing weather data was found in database for %s, %s. " +
                            "Now just need to check to see whether weather data is more than 5 minutes old. ", cityScrubbed, countryScrubbed));
                    if (isWeatherDataExpired(result.getValue())) {
                        Log.d(LOG_TAG, "WEATHER DATA IN DATABASE IS EXPIRED");

                        asyncFetchWeatherFromApi(city, country, true);
                    } else { //Weather data is in database and is not expired yet

                        Log.d(LOG_TAG, "WEATHER DATA IS STILL VALID");
                        //No need to do anything

                    }
                } else {
                    Log.d(LOG_TAG, String.format("No existing weather data record for %s, %s exists in the database", cityScrubbed, countryScrubbed));

                    Log.d(LOG_TAG, "Fetching data for the first time from OpenWeatherAPI . . .");

                    asyncFetchWeatherFromApi(city, country, false);
                }
        });

    }

    public void loadRandomWeatherData(int recordNum) {
        asyncLoadRandomWeatherData(recordNum);
    }

    ////////////////////////// GETTERS /////////////////////////////


    public LiveData<Weather> getWeather() { return m_observableWeather; }

    public LiveData<List<Weather>> getAllWeather() {
        return m_observableWeatherList;
    }

    ////////////////// CRUD Database Operations ////////////////////

    public void insert(Weather weather) {
        weather.setCity(formatCaseCity(weather.getCity()));
        weather.setCountryCode(formatCaseCountryCodeFromCountryName(weather.getCountryCode()));

        asyncInsertWeather(weather);
    }

    public LiveData<Weather> findInDatabase(String city, String country) {
        String cityScrubbed = formatCaseCity(city);
        String countryScrubbed = formatCaseCountryCodeFromCountryName(country);
        m_observableWeather.addSource(
                mWeatherDao.findWeatherByLocation(cityScrubbed, countryScrubbed), weather -> {
                    if (weather != null) {

                        m_observableWeather.removeSource(mWeatherDao.findWeatherByLocation(cityScrubbed, countryScrubbed));

                        if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
                            Log.d(LOG_TAG, "Broadcasting findWeatherByLocation() result to its observers... ");
                            m_observableWeather.setValue(weather);
                            Log.d(LOG_TAG, String.format("findWeatherByLocation() for %s,%s LiveData<User> onChanged",
                                    cityScrubbed,
                                    countryScrubbed));
                        }

                    }
                });

        asyncLoadWeatherFromDatabase(cityScrubbed, countryScrubbed);

        return m_observableWeather;
    }

    ///////////// ASYNC TASKS FOR FETCHING REAL-TIME WEATHER DATA FROM WEATHER API ///////////////

    @SuppressLint("StaticFieldLeak")
    private void asyncFetchWeatherFromApi(String city, String country, boolean isDataRefresh) {
        new AsyncTask<String, Void, Weather>() {
            @Override
            protected Weather doInBackground(String... params) {
                String city = params[0];
                String country = params[1];

                Log.d(LOG_TAG, String.format(
                        "Fetching real-time weather data from OpenWeatherMap API for %s, %s", city, country));

                Weather weatherData = fetchCurrentWeather(city, country);

                if (isDataRefresh) { //Update existing data record for previously fetched location
                    Log.d(LOG_TAG, String.format(
                            "Updating weather data fetched from OpenWeatherMap API for %s, %s " +
                                    "into database to replace existing data record with new time last updated as %s",
                            weatherData.getCity(), weatherData.getCountryCode(), weatherData.getLastUpdated().toString()));
//                        mWeatherDao.updateWeather(weatherData);
                } else { //Weather data not previously fetched before, insert
                    Log.d(LOG_TAG, String.format(
                            "Inserting weather data fetched from OpenWeatherMap API for %s, %s " +
                                    "as new record into database with time last updated as %s",
                            weatherData.getCity(), weatherData.getCountryCode(), weatherData.getLastUpdated().toString()));
                }
                mWeatherDao.insertWeather(weatherData);

                return weatherData;
            }

            @Override
            protected void onPostExecute(Weather weather) {
                m_observableWeather.setValue(weather);
            }
        }.execute(city, country);
    }

    //////////// ASYNC TASKS FOR A SINGLE WEATHER RECORD /////////////

    @SuppressLint("StaticFieldLeak")
    private void asyncLoadWeatherFromDatabase(String city, String country) {
        new AsyncTask<String, Void, Weather>() {
            @Override
            protected Weather doInBackground(String... params) {
                String city = params[0];
                String country = params[1];
                Log.d(LOG_TAG, String.format("Retrieving weather record for %s, %s from database", city, country));

                LiveData<Weather> weather = mWeatherDao.findWeatherByLocation(city, country);
                return weather.getValue();
            }

            @Override
            protected void onPostExecute(Weather weather) {
                m_observableWeather.setValue(weather);
            }
        }.execute(city, country);
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncInsertWeather(Weather weather) {
        new AsyncTask<Weather, Void, Void>() {
            @Override
            protected Void doInBackground(Weather... params) {
                Weather weatherToInsert = params[0];
                Log.d(LOG_TAG, String.format("Inserting weather record for %s, %s into database",
                        weatherToInsert.getCity(),
                        weatherToInsert.getCountryCode()));

                mWeatherDao.insertWeather(weatherToInsert);

                Log.d(LOG_TAG, "Inserting Weather data . . .");
                return null;
            }
        }.execute(weather);
    }

    ////////////////// ASYNC TASKS FOR A MULTIPLE WEATHER RECORDS //////////////////

    @SuppressLint("StaticFieldLeak")
    private void asyncLoadWeatherDataFromDatabase() {
        new AsyncTask<Void, Void, List<Weather>>() {
            @Override
            protected List<Weather> doInBackground(Void... params) {
                Log.d(LOG_TAG, "Loading weather from database");
                LiveData<List<Weather>> listLiveData = mWeatherDao.loadAllWeather();


                return listLiveData.getValue();
            }

            @Override
            protected void onPostExecute(List<Weather> weatherList) {
                m_observableWeatherList.setValue(weatherList);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncDeleteAllWeatherDataFromDatabase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(LOG_TAG, "Deleting all weather from database");
                mWeatherDao.deleteAllWeather();
                return null;
            }

            @Override
            protected void onPostExecute(Void voidResult) {
                m_observableWeatherList.setValue(Collections.emptyList());
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncLoadRandomWeatherData(int recordNumber) {
        new AsyncTask<Void, Void, Weather>() {
            @Override
            protected Weather doInBackground(Void... params) {
                Log.d(LOG_TAG, String.format("Loading weather record number %d", recordNumber));

                LiveData<Weather> weatherResult = mWeatherDao.findWeatherById(recordNumber);
                return weatherResult.getValue();
            }

            @Override
            protected void onPostExecute(Weather weather) {
                m_observableWeather.setValue(weather);
            }
        }.execute();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////

    public static Weather createTempWeatherDatabaseRecord() {
        Weather weather = new Weather();
        weather.setCity(formatCaseCity(DEFAULT_CITY));
        weather.setCountryCode(formatCaseCountryCodeFromCountryName(DEFAULT_COUNTRY));
        weather.setLatitude(Float.parseFloat("40.77"));
        weather.setLongitude(Float.parseFloat("-111.89"));
        weather.setForecastMain("Clouds");
        weather.setForecastDescription("broken clouds");
        Weather.Temperature temperature =
                weather.createTemp(
                        Float.parseFloat("277.37"),
                        Float.parseFloat("276.15"),
                        Float.parseFloat("278.75"));

        weather.setTemperature(temperature);
        weather.setPressure(Integer.parseInt("1027"));
        weather.setHumidity(Integer.parseInt("13"));
        weather.setWindSpeed(Float.parseFloat("4.1"));
        weather.setLastUpdated(Date.from(Instant.now()));

        return weather;
    }
}