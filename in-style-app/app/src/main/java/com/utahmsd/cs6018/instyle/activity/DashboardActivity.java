package com.utahmsd.cs6018.instyle.activity;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.fragment.AccountSettingsFragment;
import com.utahmsd.cs6018.instyle.fragment.LoginFragment;
import com.utahmsd.cs6018.instyle.ui.RV_Adapter;
import com.utahmsd.cs6018.instyle.util.GeocoderLocationUtils;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.fragment.DashboardFragment;
import com.utahmsd.cs6018.instyle.fragment.FitnessDetailsFragment;
import com.utahmsd.cs6018.instyle.fragment.ProfileSummaryFragment;
import com.utahmsd.cs6018.instyle.fragment.WeatherFragment;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.WeatherViewModel;

import java.util.Locale;

import static com.utahmsd.cs6018.instyle.util.Constants.ON_CLICK;
import static com.utahmsd.cs6018.instyle.util.GeocoderLocationUtils.DEFAULT_COORDINATES;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isNotNullOrEmpty;

public class DashboardActivity extends AppCompatActivity implements RV_Adapter.OnAdapterDataChannel
{

    private final static String LOG_TAG = DashboardActivity.class.getSimpleName();

    private FragmentTransaction m_fTrans;
    private FitnessProfile m_fitnessProfile;
    private FitnessProfileViewModel m_fitnessProfileViewModel;
    private WeatherViewModel weatherViewModel;
    private UserViewModel userViewModel;
    private final double m_threashold = 0.8;
    private Sensor m_sensorAccel;
    private SensorManager m_sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        restoreDefaultDashboardView();

        initializeViewModels();

        setSensors();

    }

    private void setSensors() {
        m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        m_sensorAccel = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.d("Accel: ", "The sensor: " + m_sensorAccel.getStringType());
    }

    private SensorEventListener m_AccelListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double z = event.values[2];

            if (x > m_threashold || z > m_threashold) {
                Log.d(LOG_TAG, "onSensorChanged values beyond defined thresholds");
                //TODO: FIX THIS BEHAVIOR, CALLING THE FITNESS DETAILS BUTTON HANDLER CAUSES AN INFINITE LOOP
                fitnessDetailsButtonHandler();

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (m_sensorAccel != null){
            m_sensorManager.registerListener(m_AccelListener, m_sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (m_AccelListener != null){
            m_sensorManager.unregisterListener(m_AccelListener);
        }
    }

    private void initializeViewModels() {
        m_fitnessProfileViewModel = ViewModelProviders.of(this).get(FitnessProfileViewModel.class);
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        observeUserViewModel();
    }
    private void observeUserViewModel() {
        Observer userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    userViewModel.getUser().removeObserver(this);
                    Log.d(LOG_TAG, "User view model not null, now looking up fitness profile from user id");

                    observeFitnessProfileViewModel(user);

                } else {
                    Log.d(LOG_TAG, "user view model is null");

                }
            }
        };

        userViewModel.getUser().observe(this, userObserver);
    }

    private void observeFitnessProfileViewModel(User user) {
        Observer fpObserver = new Observer<FitnessProfile>() {
            @Override
            public void onChanged(@Nullable FitnessProfile fitnessProfile) {
                if (fitnessProfile != null) {
                    m_fitnessProfileViewModel.getFitnessProfile(user.getId()).removeObserver(this);

                    Log.d(LOG_TAG, "USER IS ASSOCIATED WITH A FITNESS PROFILE RECORD");
                    Log.d(LOG_TAG, String.format(Locale.US, "USER: '%d' '%s' '%s' '%s'", user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()));
                    Log.d(LOG_TAG, String.format(Locale.US, "FITNESS PROFILE RECORD: " +
                            "userid(FK):%d' '%s' '%s' '%s, %s' ", fitnessProfile.getUserId(), fitnessProfile.getM_fName(), fitnessProfile.getM_lName(), fitnessProfile.getM_city(), fitnessProfile.getM_country()));

                } else {
                    Log.d(LOG_TAG, "fitness profile view model is null");
                }
            }
        };
        m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fpObserver);
    }

    @Override
    public void onAdapterDataPass(int position) {
        executeDashboardButtonHandler(position);
    }

    private void executeDashboardButtonHandler(int buttonPosition) {
        switch (buttonPosition) {
            case 0:
                fitnessDetailsButtonHandler();
                break;
            case 1:
                hikingButtonHandler();
                break;
            case 2:
                fitnessProfileButtonHandler();
                break;
            case 3:
                weatherButtonHandler();
                break;
            case 4:
                settingsButtonHandler();
                break;
            case 5:
                logoutButtonHandler();
                break;
        }
    }

    private void fitnessDetailsButtonHandler() {
        Log.d(LOG_TAG, "fitnessDetails " + ON_CLICK);
        if (!isWideDisplay()) {
            Intent intent = new Intent(this, FitnessDetailsActivity.class);
            startActivityForResult(intent, Activity.RESULT_OK);
        } else {
            m_fTrans = getSupportFragmentManager().beginTransaction();

            m_fTrans.replace(R.id.fl_detail_wd, new FitnessDetailsFragment());
            m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }
    }

    private void hikingButtonHandler() {
        Log.d(LOG_TAG, "Hiking " + ON_CLICK);

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fp -> {
                    if (fp != null) {
                        String coords = GeocoderLocationUtils.asyncFetchCoordinatesFromApi(fp.getM_city(), fp.getM_country());
                        if (!isNotNullOrEmpty(coords)) {
                            coords = DEFAULT_COORDINATES;
                        }
                        Uri searchUri = Uri.parse("geo:" + coords + "?q=hikes");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, searchUri);
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                });
            }
        });
    }

    private void fitnessProfileButtonHandler() {
        Log.d(LOG_TAG, ON_CLICK);

        if (!isWideDisplay()) {
            Intent intent = new Intent(this, ProfileSummaryActivity.class);
            startActivity(intent);
        } else { //Tablet
            m_fTrans = getSupportFragmentManager().beginTransaction();

            m_fTrans.replace(R.id.fl_detail_wd, new ProfileSummaryFragment());
//            m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }



    }

    private void weatherButtonHandler() {
        Log.d(LOG_TAG, ON_CLICK);

        if (!isWideDisplay()) {
            Log.d(LOG_TAG, "weatherButtonHandler mobileView");
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);

        } else { //Tablet
            Log.d(LOG_TAG, "weatherButtonHandler tabletView");
            FragmentManager manager = getSupportFragmentManager();
            m_fTrans = manager.beginTransaction();


            m_fTrans.replace(R.id.fl_detail_wd, WeatherFragment.newInstance()).setTransition(15);
//                    m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }

    }

    private void settingsButtonHandler() {
        Log.d(LOG_TAG, "Settings " + ON_CLICK);
        if (!getResources().getBoolean(R.bool.isWideDisplay)) {
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            startActivityForResult(intent, Activity.RESULT_OK);
        } else {
            FragmentTransaction m_fTrans = getSupportFragmentManager().beginTransaction();

            m_fTrans.replace(R.id.fl_detail_wd, new AccountSettingsFragment());
            m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }
    }

    private void logoutButtonHandler() {
        Log.d(LOG_TAG, "Log out " + ON_CLICK);

        if (!getResources().getBoolean(R.bool.isWideDisplay)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, Activity.RESULT_OK);
        } else {
            FragmentTransaction m_fTrans = getSupportFragmentManager().beginTransaction();

            m_fTrans.replace(R.id.fl_detail_wd, new LoginFragment());
            m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }

    }

    /**
     * Helper method to restore the default app view to
     * make sure user doesn't end up with a blank app screen.
     * from pressing back repeatedly in the app.
     */
    private void restoreDefaultDashboardView() {
        m_fTrans = getSupportFragmentManager().beginTransaction();

        if (!isWideDisplay()) {
            m_fTrans.replace(R.id.fl_master_nd, new DashboardFragment(), "v_frag_dashboard");
            m_fTrans.commit();
        } else { //Tablet default: master fragment left, detail fragment right
            m_fTrans.replace(R.id.fl_master_wd, new DashboardFragment(), "v_frag_dashboard");
            m_fTrans.replace(R.id.fl_detail_wd, new FitnessDetailsFragment(), "v_frag_fitness");
            m_fTrans.addToBackStack(null);
            m_fTrans.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");
        restoreDefaultDashboardView();
    }

    private boolean isWideDisplay() {
        return getResources().getBoolean(R.bool.isWideDisplay);
    }


}