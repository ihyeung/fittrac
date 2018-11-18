package com.utahmsd.cs6018.instyle.fragment;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.activity.ProfileEntryActivity;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.util.Constants;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;

import java.util.Locale;

import static com.utahmsd.cs6018.instyle.util.FitnessProfileUtils.calculateAge;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.formatCaseCity;

public class ProfileSummaryFragment extends Fragment
        implements View.OnClickListener {

    private static final String LOG_TAG = ProfileSummaryFragment.class.getSimpleName();

    private UserViewModel m_userViewModel;
    private FitnessProfileViewModel m_fitnessProfileViewModel;

    private Button m_editButton;
    private ImageButton m_profilePhoto;
    private TextView m_firstName, m_lastName, m_sex, m_age, m_heightFeet, m_heightInches, m_location,
            m_weight, m_activity, m_weightGoal;

    public ProfileSummaryFragment() { }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE);
        super.onCreate(savedInstanceState);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE_VIEW);

        View view = inflater.inflate(R.layout.fragment_profile_summary, container, false);
        initViewElements(view);
        configureViewModels();
        return view;
    }

    private void initViewElements(View v) {
        m_firstName = v.findViewById(R.id.txtv_fname);
//        m_lastName = v.findViewById(R.id.txtv_lname);
        m_sex = v.findViewById(R.id.txtv_sex);
        m_age = v.findViewById(R.id.txtv_dob);
//        m_heightFeet = v.findViewById(R.id.txtv_feet);
//        m_heightInches = v.findViewById(R.id.txtv_inches);
        m_weight = v.findViewById(R.id.txtv_weight);
        m_location = v.findViewById(R.id.txtv_city);
        m_activity = v.findViewById(R.id.radiogp_lifestyle);
        m_weightGoal = v.findViewById(R.id.radiogp_weightGoal);
        m_editButton = v.findViewById(R.id.btn_edit);
        m_profilePhoto = v.findViewById(R.id.btn_img_takeImage);

        m_editButton.setOnClickListener(this);

    }

    private void configureViewModels() {
        m_userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        m_fitnessProfileViewModel = ViewModelProviders.of(getActivity()).get(FitnessProfileViewModel.class);

        observeUserViewModel();
    }
    private void observeUserViewModel() {
        Observer userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    m_userViewModel.getUser().removeObserver(this);
                    Log.d(LOG_TAG, "User view model not null, now looking up fitness profile from user id");

                    observeFitnessProfileViewModel(user);

                }
            }
        };

        m_userViewModel.getUser().observe(this, userObserver);
    }

    private void observeFitnessProfileViewModel(User user) {
        Observer fpObserver = new Observer<FitnessProfile>() {
            @Override
            public void onChanged(@Nullable FitnessProfile fitnessProfile) {
                if (fitnessProfile != null) {
                    m_fitnessProfileViewModel.getFitnessProfile(user.getId()).removeObserver(this);

                    if (fitnessProfile.getUserId() == user.getId() &&
                            fitnessProfile.getM_fName().equals(user.getFirstName()) &&
                            fitnessProfile.getM_lName().equals(user.getLastName())) {

                        autofillExistingFitnessProfileData(fitnessProfile);
                    } else {
                        displayNoExistingFitnessProfileAlertDialog();
                    }
                } else {
                    displayNoExistingFitnessProfileAlertDialog();
                }
            }
        };
        m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fpObserver);
    }

    private void autofillExistingFitnessProfileData(FitnessProfile fp) {
        Log.d(LOG_TAG, "Autofilling existing FitnessProfile data");

        m_firstName.setText(String.format(Locale.US, "%s %s.",
                formatCaseCity(fp.getM_fName()), fp.getM_lName().toUpperCase().charAt(0)));
//        m_lastName.setText(formatCaseCity(fp.getM_lName()));
        m_sex.setText(fp.getM_sex().toUpperCase());
        m_age.setText(String.format(Locale.US, "%dy", calculateAge(fp.getM_dob())));
//        m_heightFeet.setText(String.format(Locale.US, "%d ft.,", fp.getM_heightFeet()));
//        m_heightInches.setText(String.format(Locale.US, " %d in.", fp.getM_heightInches()));
        m_weight.setText(String.format(Locale.US, "%d lbs", fp.getM_weightInPounds()));

        m_location.setText(String.format(Locale.US, "%s, %s", formatCaseCity(fp.getM_city()),
                formatCaseCity(fp.getM_country())));
//        m_country.setText("");
        m_activity.setText(String.format("%s", formatCaseCity(fp.getM_lifestyleSelection().toLowerCase())));
        if (fp.getM_weightGoal().equalsIgnoreCase("MAINTAIN")) {
            m_weightGoal.setText("Maintain current weight");
        } else {
            m_weightGoal.setText(String.format(Locale.US, "%s %d lbs/week",
                    formatCaseCity(fp.getM_weightGoal()), Math.abs(fp.getM_lbsPerWeek())));
        }

    }

//        m_userViewModel.getUser().observe(this, user -> {
//            if (user != null) {
//                m_userViewModel.getUser().removeObservers(this);
//
//                m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fp -> {
//                    if (fp != null) {
//                        m_userViewModel.getUser().removeObservers(this);
//
//                        if (user.getFirstName().equals(fp.getM_fName()) && user.getLastName().equals(fp.getM_lName())) {
//                            m_firstName.setText(formatCaseCity(fp.getM_fName()));
//                            m_lastName.setText(formatCaseCity(fp.getM_lName()));
//                            m_sex.setText(fp.getM_sex().toUpperCase());
//                            m_age.setText(String.format(Locale.US, "%dy", calculateAge(fp.getM_dob())));
//                            m_heightFeet.setText(String.format(Locale.US, "%d ft.,", fp.getM_heightFeet()));
//                            m_heightInches.setText(String.format(Locale.US, " %d in.", fp.getM_heightInches()));
//                            m_weight.setText(String.format(Locale.US, "%d ", fp.getM_weightInPounds()));
//                            m_location.setText(String.format(Locale.US, "%s, %s",
//                                    formatCaseCity(fp.getM_city()), formatCaseCity(fp.getM_country())));
//                            m_country.setText("");
//                            m_activity.setText(String.format("Activity Level: ", fp.getM_lifestyleSelection().toLowerCase()));
//                            m_weightGoal.setText(String.format(Locale.US, "Fitness Goal: %s %d lbs/week",
//                                    fp.getM_weightGoal(), Math.abs(fp.getM_lbsPerWeek())));
//                        } else {
//                            Log.d(LOG_TAG, "Fitness profile is not null but user first/last name doesnt match fitness profile first/last name");
//                            Log.d(LOG_TAG, String.format(Locale.US, "Name of fitness profile in view model: '%s' '%s'", fp.getM_fName(), fp.getM_lName()));
//                        }
//                    } else {
//                        Log.d(LOG_TAG, "FitnessProfileViewModel is null, display dialog");
//                        displayNoExistingFitnessProfileAlertDialog();
//                    }
//                });
//            } else {
//                Log.d(LOG_TAG, "UserViewModel is null, display dialog");
//                displayNoExistingFitnessProfileAlertDialog();
//            }
//        });
//    }


    private void displayNoExistingFitnessProfileAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_message);
        builder.setIcon(R.drawable.ic_directions_run);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "Dialog OK button clicked");
                viewTransitionHandler();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "Dialog cancel button clicked");
                //Do nothing
            }
        });

        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "Edit button onClick");

        viewTransitionHandler();
    }

    private void viewTransitionHandler() {
        if (!getResources().getBoolean(R.bool.isWideDisplay)) {
            Intent intent = new Intent(getContext(), ProfileEntryActivity.class);
            startActivity(intent);
        } else {
            FragmentTransaction m_fTrans = getActivity().getSupportFragmentManager().beginTransaction();
            m_fTrans.replace(R.id.fl_master_wd, new DashboardFragment(), "v_frag_dashboard");
            m_fTrans.replace(R.id.fl_detail_wd, new ProfileEntryFragment(), "v_frag_fitness");            m_fTrans.commit();
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d(LOG_TAG, Constants.ATTACH);
        super.onAttach(context);
    }

}
