package com.example.mcresswell.project01;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mcresswell.project01.userProfile.UserProfile;
import com.example.mcresswell.project01.userProfile.UserProfileViewModel;
import com.example.mcresswell.project01.util.Constants;
import com.example.mcresswell.project01.util.UserProfileUtils;

import java.util.Locale;

import static com.example.mcresswell.project01.util.Constants.CREATE_VIEW;
import static com.example.mcresswell.project01.util.UserProfileUtils.calculateCalories;

public class FitnessDetailsFragment extends Fragment {

    private static final String LOG = FitnessDetailsFragment.class.getSimpleName();

    private static final String DEFAULT_CALORIES = "2000 calories/day";
    private static final String DEFAULT_BMR = "1500 calories/day";
    private static final int DEFAULT_HEIGHT = 65;
    private static final int DEFAULT_WEIGHT = 120;

    private TextView m_tvcalsToEat, m_tvBMR, m_bodyMassIndex, m_tvbmiClassification;
    private UserProfileViewModel viewModel;
    private UserProfile m_userProfile;



    public FitnessDetailsFragment() {
        // Required empty public constructor
    }

    public static FitnessDetailsFragment newInstance(UserProfile userProfile) {
        Log.d(LOG, Constants.NEW);
        FitnessDetailsFragment fragment = new FitnessDetailsFragment();
        Bundle args = new Bundle();
        if (userProfile != null) {
            Log.d(LOG, "NEW INSTANCE WITH NON-NULL userProfile!!");
            args.putParcelable("profile", userProfile);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, Constants.CREATE);
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);
        viewModel.getUserProfile().observe(this, nameObserver);

        if (getArguments() != null) {
            m_userProfile = getActivity().getIntent().getParcelableExtra("profile");

            loadUserProfileData(m_userProfile);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG, CREATE_VIEW);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fitness_details, container, false);

        m_tvcalsToEat =  view.findViewById(R.id.tv_calPerDay);
        m_tvBMR = view.findViewById(R.id.tv_BMR);
        m_bodyMassIndex = view.findViewById(R.id.tv_bmi);

        if (m_userProfile == null) {
            String defaultBmi = String.format(Locale.US, "%.1f", UserProfileUtils.calculateBmi(DEFAULT_HEIGHT, DEFAULT_WEIGHT));
            m_tvcalsToEat.setText(DEFAULT_CALORIES);
            m_tvBMR.setText(DEFAULT_BMR);
            m_bodyMassIndex.setText(defaultBmi);
        } else {
            loadUserProfileData(m_userProfile);
        }

        return view;
    }

    final Observer<UserProfile> nameObserver  = new Observer<UserProfile>() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onChanged(@Nullable final UserProfile userProfile) {
            if (userProfile != null) { //Weather data has finished being retrieved
                userProfile.printUserProfileData();

                double caloricIntake = calculateCalories(userProfile);
                m_tvcalsToEat.setText(String.format(Locale.US,"%.1f calories", caloricIntake));
                m_tvBMR.setText(String.format(Locale.US, "%.1f calories/day", userProfile.getM_bmr()));
                m_bodyMassIndex.setText(String.format(Locale.US, "%.1f", userProfile.getM_bmi()));

            }


        }
    };

    private void loadUserProfileData(UserProfile userProfile){
        Log.d(LOG, "loadUserProfileData");

        //pass the user profile in to the view model
        viewModel.setUserProfile(userProfile);
    }

}
