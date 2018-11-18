package com.utahmsd.cs6018.instyle.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.WeatherFragment;

public class WeatherActivity extends AppCompatActivity {

    private static final String LOG_TAG = WeatherActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WeatherFragment frag_weather = WeatherFragment.newInstance();
        fragmentTransaction.add(R.id.fl_activity_weather, frag_weather);
        fragmentTransaction.commit();
    }
}
