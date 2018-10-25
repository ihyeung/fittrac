package com.example.mcresswell.project01.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mcresswell.project01.R;
import com.example.mcresswell.project01.db.entity.User;
import com.example.mcresswell.project01.viewmodel.FitnessProfileViewModel;
import com.example.mcresswell.project01.db.entity.FitnessProfile;
import com.example.mcresswell.project01.util.Constants;
import com.example.mcresswell.project01.viewmodel.UserViewModel;

import java.util.Locale;

import static com.example.mcresswell.project01.util.Constants.CREATE_VIEW;
import static com.example.mcresswell.project01.util.FitnessProfileUtils.calculateAge;
import static com.example.mcresswell.project01.util.FitnessProfileUtils.calculateBMR;
import static com.example.mcresswell.project01.util.FitnessProfileUtils.calculateBmi;
import static com.example.mcresswell.project01.util.FitnessProfileUtils.calculateCalories;
import static com.example.mcresswell.project01.util.FitnessProfileUtils.calculateHeightInInches;

public class FitnessDetailsFragment extends Fragment {

    private static final String LOG_TAG = FitnessDetailsFragment.class.getSimpleName();

    private static final double DEFAULT_CALS = 2000;
    private static final double DEFAULT_BMR  = 1500;
    private static final double DEFAULT_BMI  = 20.0;
    private static final int STEP_COUNT_PLACEHOLDER = 789; //Temp placeholder for step count

    private static final String DOUBLE_FORMAT = "%.1f";
    private static final String INT_FORMAT = "%d";

    private static final String CALORIC_INTAKE = " calories/day";
    private static final String BMR = " calories/day";
    private static final String STEPS = " steps";

    private TextView m_tvcalsToEat, m_tvBMR, m_bodyMassIndex, m_stepCount;
    private TextView  m_tvbmiClassification; //Implement this later when the fitness profile is working
    private FitnessProfileViewModel m_fitnessProfileViewModel;
    private UserViewModel m_userViewModel;
    private FitnessProfile m_fitnessProfile;
    private User m_user;


    public FitnessDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, CREATE_VIEW);

        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);

        m_tvcalsToEat =  view.findViewById(R.id.tv_calPerDay);
        m_tvBMR = view.findViewById(R.id.tv_BMR);
        m_bodyMassIndex = view.findViewById(R.id.tv_bmi);
        m_stepCount = view.findViewById(R.id.tv_step_count);


        if (m_fitnessProfile == null) {
            m_tvcalsToEat.setText(String.format(Locale.US, DOUBLE_FORMAT + CALORIC_INTAKE, DEFAULT_CALS));
            m_tvBMR.setText(String.format(Locale.US, DOUBLE_FORMAT + BMR, DEFAULT_BMR));
            m_bodyMassIndex.setText(String.format(Locale.US, DOUBLE_FORMAT, DEFAULT_BMI));
            m_stepCount.setText(String.format(Locale.US, INT_FORMAT + STEPS, STEP_COUNT_PLACEHOLDER));
        } else {
            double caloricIntake = calculateCalories(m_fitnessProfile);
            m_tvcalsToEat.setText(String.format(Locale.US,"%.1f calories", caloricIntake));
            m_tvBMR.setText(String.format(Locale.US, "%.1f calories/day", m_fitnessProfile.getM_bmr()));
            m_bodyMassIndex.setText(String.format(Locale.US, "%.1f", m_fitnessProfile.getM_bmi()));
        }

        configureViewModels();


        return view;
    }

    private void configureViewModels() {
        final Observer<FitnessProfile> fitnessProfileObserver = fitnessProfile -> m_fitnessProfile = fitnessProfile;
        m_fitnessProfileViewModel = ViewModelProviders.of(getActivity())
                .get(FitnessProfileViewModel.class);

        m_userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        m_userViewModel.getUser().observe(this, user -> {
            Log.d(LOG_TAG, "UserViewModel observer for getUser()");
            if (user != null) {
                m_fitnessProfileViewModel.getLDFitnessProfile(user.getId()).observe(this, fp -> {
                    if (fp != null) {
                        int calcAge = calculateAge(fp.getM_dob());
                        double basalMetabolicRate = calculateBMR(fp.getM_heightFeet(),
                                fp.getM_heightInches(),
                                fp.getM_sex(),
                                fp.getM_weightInPounds(),
                                calcAge);
                        double bodyMassIndex = calculateBmi(calculateHeightInInches(
                                fp.getM_heightFeet(),
                                fp.getM_heightInches()),
                                fp.getM_weightInPounds());
                        m_tvcalsToEat.setText(String.format(Locale.US, DOUBLE_FORMAT + CALORIC_INTAKE, calculateCalories(fp)));
                        m_tvBMR.setText(String.format(Locale.US, DOUBLE_FORMAT + BMR, basalMetabolicRate));
                        m_bodyMassIndex.setText(String.format(Locale.US, DOUBLE_FORMAT, bodyMassIndex));
                        m_stepCount.setText(String.format(Locale.US, INT_FORMAT + STEPS, STEP_COUNT_PLACEHOLDER));
                    }
                });

            }
        });

    }




//    final Observer<FitnessProfile> nameObserver  = new Observer<FitnessProfile>() {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void onChanged(@Nullable final FitnessProfile fitnessProfile) {
//            if (fitnessProfile != null) { //Weather data has finished being retrieved
//                printUserProfileData(fitnessProfile);
////
////                double caloricIntake = calculateCalories(fitnessProfile);
////                m_tvcalsToEat.setText(String.format(Locale.US,"%.1f calories", caloricIntake));
////                m_tvBMR.setText(String.format(Locale.US, "%.1f calories/day", fitnessProfile.getM_bmr()));
////                m_bodyMassIndex.setText(String.format(Locale.US, "%.1f", fitnessProfile.getM_bmi()));
//
//                loadUserProfileData(m_fitnessProfile);
//
//            }
//
//
//        }
//    };
//
//    private void loadUserProfileData(FitnessProfile fitnessProfile){
//        Log.d(LOG_TAG, "loadUserProfileData");
//
//        //pass the user profile in to the view model
////        viewModel.setFitnessProfile(fitnessProfile);
//    }

}
