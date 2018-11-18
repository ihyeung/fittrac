package com.utahmsd.cs6018.instyle.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.ui.DashButton;
import com.utahmsd.cs6018.instyle.ui.RV_Adapter;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.WeatherListViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.utahmsd.cs6018.instyle.util.Constants.CREATE;
import static com.utahmsd.cs6018.instyle.util.Constants.CREATE_VIEW;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private static final String LOG_TAG = DashboardFragment.class.getSimpleName();

    private RecyclerView m_RecyclerView;
    private RecyclerView.Adapter m_Adaptor;
    private RecyclerView.LayoutManager layoutManager;
    private FitnessProfileViewModel fitnessProfileViewModel;
    private UserViewModel userViewModel;
    private WeatherViewModel weatherViewModel;
    private WeatherListViewModel weatherListViewModel;


    private Button m_logoutButton, m_settingsButton;


    public DashboardFragment() { }

    @Override
    public void onCreate(Bundle bundle) {
        Log.d(LOG_TAG, CREATE);

        super.onCreate(bundle);

//        initializeViewModels();
//
//        initializeWeatherListViewModel();

    }

    private void initializeWeatherListViewModel() {
        weatherListViewModel = ViewModelProviders.of(getActivity()).get(WeatherListViewModel.class);
        Observer weatherListObserver = new Observer<List<Weather>>() {
            @Override
            public void onChanged(@Nullable List<Weather> weatherList) {
                if (weatherList != null) {

                    weatherListViewModel.getWeatherDataFromDatabase().removeObserver(this);

                    ArrayList<Integer> idList = new ArrayList<>();
                    Log.d(LOG_TAG, "Update to weather list view model");
                    Log.d(LOG_TAG, "Number of weather records in Weather database: " + weatherList.size());
                    Log.d(LOG_TAG, "------------------------------------------");
                    Log.d(LOG_TAG, "PRINTING WEATHER RECORDS IN WEATHER DATABASE");
                    Log.d(LOG_TAG, "\n");
                    weatherList.forEach(weather -> {
                        idList.add(weather.getId());
                        Log.d(LOG_TAG, "\nWeather Data record: " + weather.getId() + "\t'" +
                                weather.getCity() + "'\t'" + weather.getCountryCode() + "'\t'" + weather.getLastUpdated() + "'");
                    });
                    Log.d(LOG_TAG, "\n");
                    Log.d(LOG_TAG, "------------------------------------------");
                }
            }
        };

        weatherListViewModel.getWeatherDataFromDatabase().observe(this, weatherListObserver);

    }

    private void initializeViewModels() {
        fitnessProfileViewModel = ViewModelProviders.of(getActivity()).get(FitnessProfileViewModel.class);
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        weatherViewModel = ViewModelProviders.of(getActivity()).get(WeatherViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, CREATE_VIEW);

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        initializeRecyclerViewAdapater(view);
        return view;
    }

    private void initializeRecyclerViewAdapater(View view) {
        m_RecyclerView = view.findViewById(R.id.rcycV_List);
        m_RecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        m_RecyclerView.setLayoutManager(layoutManager);

        ArrayList<DashButton> buttons = new ArrayList<>();

        //build image for button
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_img_fitness, null), "FITNESS"));
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_img_hike, null), "HIKING"));
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_img_profile, null), "PROFILE"));
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_img_weather, null), "WEATHER"));
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_launcher_settings, null), "SETTINGS"));
        buttons.add(new DashButton(getResources().getDrawable(R.drawable.ic_launcher_logout, null), "LOG OUT"));


        m_Adaptor = new RV_Adapter(buttons);

        if(!getResources().getBoolean(R.bool.isWideDisplay)){
            m_RecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }
        m_RecyclerView.setAdapter(m_Adaptor);
    }

}
