package com.utahmsd.cs6018.instyle.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.WeatherListViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.WeatherViewModel;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.utahmsd.cs6018.instyle.db.repo.WeatherRepository.createTempWeatherDatabaseRecord;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.convertAndFormatKelvinTemp;
import static com.utahmsd.cs6018.instyle.util.mapper.CountryCodeMapper.getCountryName;


/**
 * Weather fragment class.
 */
public class WeatherFragment extends ListFragment {

    private static final String LOG_TAG = WeatherFragment.class.getSimpleName();

    private WeatherViewModel weatherViewModel;
    private FitnessProfileViewModel fitnessProfileViewModel;
    private WeatherListViewModel weatherListViewModel;
    private UserViewModel userViewModel;

    private TextView location;
    Map<String, String> mapper;
    private ArrayList<String> data;
    private ListView m_listView;

    public WeatherFragment() {
    }

    public static WeatherFragment newInstance() {
        Log.d(LOG_TAG, Constants.NEW);
        WeatherFragment fragment = new WeatherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE);
        super.onCreate(savedInstanceState);

        configureWeatherViewModels();

    }

    private void configureWeatherViewModels() {
        weatherListViewModel = ViewModelProviders.of(getActivity()).get(WeatherListViewModel.class);

        weatherViewModel = ViewModelProviders.of(getActivity()).get(WeatherViewModel.class);

        Observer weatherObserver = new Observer <Weather>() {
            @Override
            public void onChanged(@Nullable Weather weather) {
                if(weather != null) {
                    Log.d(LOG_TAG, "Weather view model has changed and isnt null");
                    weatherViewModel.getWeather().removeObserver(this);
                    displayWeatherWidget(weather);

                    return;
                } else {
                    Log.d(LOG_TAG, "weather view model is still null");
                    displayWeatherWidget(createTempWeatherDatabaseRecord());
                }
            }
        };

        weatherViewModel.getWeather().observe(this, weatherObserver);

        Observer weatherListObserver = new Observer <List<Weather>>() {
            @Override
            public void onChanged(@Nullable List<Weather> weatherList) {
                if(weatherList != null) {
                    Log.d(LOG_TAG, "Weather list view model has changed and isnt null");
                    weatherListViewModel.getWeatherDataFromDatabase().removeObserver(this);
                    logWeatherDataFromDatabase(weatherList);

                    return;
                }
            }
        };

        weatherListViewModel.getWeatherDataFromDatabase().observe(this, weatherListObserver);
    }

    private void logWeatherDataFromDatabase(List<Weather> weatherList) {
        ArrayList<Integer> idList = new ArrayList<>();
        Log.d(LOG_TAG, "Update to weather list view model");
        Log.d(LOG_TAG, "Number of weather records in Weather database: " + weatherList.size());
        Log.d(LOG_TAG, "------------------------------------------");
        Log.d(LOG_TAG, "PRINTING WEATHER RECORDS IN WEATHER DATABASE");
        Log.d(LOG_TAG, "\n");
        weatherList.forEach(weather -> {
            idList.add(weather.getId());
            Log.d(LOG_TAG, "\nWeather Data record: " + weather.getId() + "\t'" + weather.getCity() + "'\t'" + weather.getCountryCode() + "'\t'" + weather.getLastUpdated() + "'");
        });
        Log.d(LOG_TAG, "\n");
        Log.d(LOG_TAG, "------------------------------------------");
    }

    private void configureUserAndFitnessProfileViewModels() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        fitnessProfileViewModel = ViewModelProviders.of(this).get(FitnessProfileViewModel.class);

        Observer userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    userViewModel.getUser().removeObserver(this);

                    observeFitnessProfileViewModel(user);

                }
            }

        };
        userViewModel.getUser().observe(this, userObserver);
    }

    private void observeFitnessProfileViewModel(User user) {

        Observer fitnessProfileObserver = new Observer<FitnessProfile>() {
            @Override
            public void onChanged(@Nullable FitnessProfile fitnessProfile) {
                if (fitnessProfile != null) {
                    fitnessProfileViewModel.getFitnessProfile(user.getId()).removeObserver(this);
                    Log.d(LOG_TAG, "OMG OMG OMG FITNESS PROFILE VIEW MODEL IS NOT NULL AND CAN PASS IN CITY/COUNTRY" +
                            "FOR WEATHER DATA!!!!");

                    Log.d(LOG_TAG, String.format("Loading weather for user's location of %s, %s",
                            fitnessProfile.getM_city(), fitnessProfile.getM_country()));

                    //City and country get scrubbed/sanitized in nested repo call
                    weatherViewModel.loadWeather(fitnessProfile.getM_city(), fitnessProfile.getM_country());

                }
            }
        };

        fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fitnessProfileObserver);
    }

    private void displayWeatherWidget(Weather weather) {
        data = new ArrayList<>();
        mapper = createObjectMapper(weather);
        mapper.forEach((key, val) -> {
            if (key.equals("location")) { //
                location.setText(val);
                return;
            }
            data.add(key + "\t" + val);
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                R.layout.item_weather_widget,
                data);
        setListAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE_VIEW);


        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        location = view.findViewById(R.id.weatherLocation);
        m_listView = view.findViewById(android.R.id.list);
        m_listView.setId(android.R.id.list);

        configureUserAndFitnessProfileViewModels();

        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d(LOG_TAG, Constants.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LOG_TAG, Constants.ATTACH);

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.d(LOG_TAG, Constants.DETACH);
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle state){
        Log.d(LOG_TAG, Constants.SAVE_INSTANCE_STATE);
        super.onSaveInstanceState(state);
    }

//    final Observer<Weather> weatherObserver = new Observer<Weather>() {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void onChanged(@Nullable final Weather weather) {
//            if (weather != null) { //Weather data has finished being retrieved
//                Log.d(LOG_TAG, "weatherObserver onChanged listener: weather data changed and is not null");
//
//                WeatherUtils.printWeather(weather);
//
//                data = new ArrayList<>();
//                mapper = createObjectMapper(weather);
//                mapper.forEach((key, val) -> {
//                    if (key.equals("location")) { //
//                        location.setText(val);
//                        return;
//                    }
//                    data.add(key + "\t" + val);
//                });
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
//                        R.layout.item_weather_widget,
//                        data);
//                setListAdapter(adapter);
//
//                mListener.onWeatherDataLoaded(weather);
//
//
//            }
//
//
//        }
//    };

//    private void subscribeToUserProfileModel() {
//        fitnessProfileViewModel.getFitnessProfile().observe(this, fitnessProfile -> {
//            //Now that valid user profile data has been entered, reload
//            if (fitnessProfile != null) {
//                Log.d(LOG_TAG, "subscribeToUserProfileModel: FitnessProfileViewModel onChanged " +
//                        "listener, re-fetching current weather based on user city and country");
//                fetchWeatherDataFromDataSource(fitnessProfile.getM_city(), fitnessProfile.getM_country());
//                dialog = new ProgressDialog(getContext());
//                dialog.setMessage(String.format("Loading weather for %s, %s...", fitnessProfile.getM_city(), fitnessProfile.getM_country()));
//                dialog.show();
//            }
//        });

//    final Observer<WeatherForecast> weatherObserver = new Observer<WeatherForecast>() {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void onChanged(@Nullable final WeatherForecast weatherData) {
//            if (weatherData != null) { //Weather data has finished being retrieved
//                Log.d(LOG_TAG, "weatherObserver onChanged listener: weather data changed and is not null");
//                dialog.dismiss();
//                weatherData.printWeatherForecast();
//
//                data = new ArrayList<>();
//                mapper = createObjectMapper(weatherData);
//                mapper.forEach((key, val) -> {
//                    if (key.equals("")) {
//                        location.setText(val);
//                        return;
//                    }
//                    data.add(key + "\t" + val);
//                });
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
//                        R.layout.item_weather_widget,
//                        data);
//                setListAdapter(adapter);
//                mListener.onWeatherDataLoaded(weatherData);
//
//
//            }
//
//
//        }
//    };

//    private void subscribeToUserProfileModel() {
//        fitnessProfileViewModel.getFitnessProfile().observe(this, fitnessProfile -> {
//            //Now that valid user profile data has been entered, reload
//            if (fitnessProfile != null) {
//                Log.d(LOG_TAG, "subscribeToUserProfileModel: FitnessProfileViewModel onChanged " +
//                        "listener, re-fetching current weather based on user city and country");
//                fetchWeatherDataFromDataSource(fitnessProfile.getM_city(), fitnessProfile.getM_country());
//                dialog = new ProgressDialog(getContext());
//                dialog.setMessage(String.format("Loading weather for %s, %s...", fitnessProfile.getM_city(), fitnessProfile.getM_country()));
//                dialog.show();
//            }
//        });




//    private void fetchWeatherDataFromDataSource(String city, String country){
//        Log.d(LOG_TAG, "fetchWeatherDataFromDataSource");
//
//        //pass the location in to the view model
//        weatherViewModel.setLocation(city, country);
//    }

    public Map<String, String> createObjectMapper(Weather data) {
        Map<String, String> mapper = new HashMap<String, String>();
        mapper.put("location", data.getCity().replace("+", " ") + ", " + getCountryName(data.getCountryCode()));
        mapper.put(getResources().getString(R.string.current_conditions_weather_widget), data.getForecastMain());
        mapper.put(getResources().getString(R.string.forecast_detail_weather_widget), data.getForecastDescription());
        mapper.put(getResources().getString(R.string.temp_weather_widget), convertAndFormatKelvinTemp(data.getTemperature().temp));
        mapper.put(getResources().getString(R.string.temp_min_weather_widget), convertAndFormatKelvinTemp(data.getTemperature().tempMin));
        mapper.put(getResources().getString(R.string.temp_max_weather_widget), convertAndFormatKelvinTemp(data.getTemperature().tempMax));
        mapper.put(getResources().getString(R.string.humidity_weather_widget), String.valueOf(data.getHumidity() + "%"));
        mapper.put(getResources().getString(R.string.wind_weather_widget), String.valueOf(data.getWindSpeed()));
        mapper.put(getResources().getString(R.string.pressure_weather_widget), String.valueOf(data.getPressure()));

        return mapper;
    }
}