package com.utahmsd.cs6018.instyle.fragment;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.activity.DashboardActivity;
import com.utahmsd.cs6018.instyle.activity.ProfileSummaryActivity;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.util.Constants;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isNotNullOrEmpty;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidCity;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidDobFormat;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidHeight;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidName;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidSex;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidWeight;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidWeightPlan;
import static com.utahmsd.cs6018.instyle.util.mapper.CountryCodeMapper.getCountryNames;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEntryFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = ProfileEntryFragment.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int READ_REQUEST_CODE = 42;
    private static final int WIDE_DISPLAY_IMG_DIM = 600;
    private static final int MOBILE_IMG_DIM = 300;


    private FitnessProfileViewModel m_fitnessProfileViewModel;
    private UserViewModel userViewModel;

    //UI Elements
    private EditText etxt_firstName, etxt_lastName, etxt_dob, etxt_sex, etxt_city,
            etxt_weight, etxt_heightFeet, etxt_heightInches, etxt_lbsPerWeek;
    private Button profileEntryButton;
    private ImageButton takeProfileImageButton;
    private Bitmap profileImage;
    private Spinner countrySpinner;
    private SeekBar lifestyleSlider, weightGoalSlider;
    private String imageUriString;

    public ProfileEntryFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE_VIEW);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_entry, container, false);

        initializeViewElements(view);

        setOnClickListeners();

        configureViewModels();

        return view;
    }

    private void setOnClickListeners() {
        profileEntryButton.setOnClickListener(this);
        takeProfileImageButton.setOnClickListener(this);
        etxt_dob.setOnClickListener(l->displayDatePickerDialog());
    }

    private void configureViewModels() {
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        m_fitnessProfileViewModel =
                ViewModelProviders.of(getActivity()).get(FitnessProfileViewModel.class);

        observeUserViewModel();

    }

    private void observeUserViewModel() {
        Observer userObserver = new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null) {
                    userViewModel.getUser().removeObserver(this);

                    etxt_firstName.setText(user.getFirstName());
                    etxt_lastName.setText(user.getLastName());

                    observeFitnessProfileViewModel(user);

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

                    if (fitnessProfile.getUserId() == user.getId()) {
                        autofillExistingFitnessProfileData(fitnessProfile);
                    }
                }
            }
        };
        m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fpObserver);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        Log.d(LOG_TAG, "onClick");
        switch (view.getId()){
            case R.id.btn_img_takeImage: {
                profileImageButtonHandler();
                break;
            }
            case R.id.btn_submit: {
                userProfileSubmitButtonHandler();
                break;
            }
            case R.id.txtv_dob: {
                displayDatePickerDialog();
                break;
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void displayDatePickerDialog() {
        Date defaultDate = Date.from(Instant.now());
        if (isValidDobFormat(etxt_dob.getText().toString())) {
            try {
                defaultDate = new SimpleDateFormat("MM/dd/yyyy").parse(etxt_dob.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(defaultDate);

        Log.d(LOG_TAG, String.format("Default date set on dialog: '%s'", defaultDate.toString()));

        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d(LOG_TAG, String.format(Locale.US, "birthdate was set: %d-%d-%d",
                        year, month, day));

                etxt_dob.setText(String.format(Locale.US, "%d/%d/%d", month+1, day, year));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        int dim = getResources().getBoolean(R.bool.isWideDisplay) ? WIDE_DISPLAY_IMG_DIM : MOBILE_IMG_DIM;
        if (intent != null) {
            if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                uri = intent.getData();
                Log.d(LOG_TAG, "URI: " + uri.toString());
                Log.d(LOG_TAG, "ACTUAL FILE PATH: " + getImageFilePathFromUri(uri));
                imageUriString = uri.toString();

                try {
                    Bitmap bitmap = getBitmapFromUri(uri);
                    takeProfileImageButton.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
                if (intent.getExtras() != null) {
                    profileImage = (Bitmap) intent.getExtras().get("data");
                }
                if (profileImage != null) {
                    takeProfileImageButton.setImageBitmap(profileImage);
                }
            }
            takeProfileImageButton.setMinimumHeight(dim);
            takeProfileImageButton.setMinimumWidth(dim);
        }
    }

    @Override
    public void onViewStateRestored (Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onViewStateRestored");

        super.onViewStateRestored(savedInstanceState);

        if(savedInstanceState != null) {
            profileImage = savedInstanceState.getParcelable("M_IMG_DATA");
            takeProfileImageButton.setImageBitmap(profileImage);
//            imageUriString = savedInstanceState.getString("uri");
        }
    }

    @Override
    public void onSaveInstanceState (Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onSaveInstanceState");

        super.onSaveInstanceState(savedInstanceState);
        if (takeProfileImageButton != null && takeProfileImageButton.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) takeProfileImageButton.getDrawable()).getBitmap();
            savedInstanceState.putParcelable("M_IMG_DATA", bitmap);
//            savedInstanceState.putString("uri", getUriFromBitmap(bitmap).toString());
        }
    }

    private void initializeViewElements(View view) {
        etxt_firstName = view.findViewById(R.id.txtv_fname);
        etxt_lastName = view.findViewById(R.id.txtv_lname);
        etxt_dob = view.findViewById(R.id.txtv_dob);
        etxt_sex = view.findViewById(R.id.txtv_sex);
        etxt_city = view.findViewById(R.id.txtv_city);
        etxt_weight = view.findViewById(R.id.txtv_weight);
        etxt_heightFeet = view.findViewById(R.id.txtv_feet);
        etxt_heightInches = view.findViewById(R.id.txtv_inches);
        etxt_lbsPerWeek = view.findViewById(R.id.txtv_weight_goal_weekly);

        profileEntryButton = view.findViewById(R.id.btn_submit);
        takeProfileImageButton = view.findViewById(R.id.btn_img_takeImage);

        countrySpinner = view.findViewById(R.id.spinner_country);

        ArrayList<String> countryOptions = getCountryNames();
        countryOptions.add(0, "United States");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_item,
                countryOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        lifestyleSlider = view.findViewById(R.id.slider_lifestyle);
        lifestyleSlider.setProgress(1); //Default slider position to 'moderate'
        weightGoalSlider = view.findViewById(R.id.slider_weight_goal);
        lifestyleSlider.setProgress(0); //Default slide position to 'lose'
    }

    private void autofillExistingFitnessProfileData(FitnessProfile fp) {
        Log.d(LOG_TAG, "Autofilling existing FitnessProfile data");

        etxt_firstName.setText(fp.getM_fName());
        etxt_lastName.setText(fp.getM_lName());
        etxt_dob.setText(fp.getM_dob());
        etxt_sex.setText(fp.getM_sex());
        etxt_heightFeet.setText(String.valueOf(fp.getM_heightFeet()));
        etxt_heightInches.setText(String.valueOf(fp.getM_heightInches()));
        etxt_city.setText(fp.getM_city());

        int pos = getCountryNames().indexOf(fp.getM_country());
        countrySpinner.setSelection(pos);

        etxt_weight.setText(String.valueOf(fp.getM_weightInPounds()));
        etxt_lbsPerWeek.setText(String.valueOf(Math.abs(fp.getM_lbsPerWeek())));

        restoreSliderPositions(fp);
    }

    private void restoreSliderPositions(FitnessProfile fp) {
        if (fp.getM_lifestyleSelection().equalsIgnoreCase("ACTIVE")) {
            lifestyleSlider.setProgress(2);
        }
        else if (fp.getM_lifestyleSelection().equalsIgnoreCase("MODERATE")) {
            lifestyleSlider.setProgress(1);
        } else {
            lifestyleSlider.setProgress(0);
        }
        if (fp.getM_weightGoal().equalsIgnoreCase("GAIN")) {
            weightGoalSlider.setProgress(2);
        }
        else if (fp.getM_weightGoal().equalsIgnoreCase("MAINTAIN")) {
            weightGoalSlider.setProgress(1);
        } else {
            assert(fp.getM_weightGoal().equalsIgnoreCase("LOSE"));
            weightGoalSlider.setProgress(0);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void userProfileSubmitButtonHandler() {
        if (!isUserInputDataValid()) {
            Log.d(LOG_TAG, "invalid user data input");

            return;
        }

        FitnessProfile tempFitnessProfile = instantiateFitnessProfile();

        userViewModel.getUser().observe(this, user -> {
            if (user != null) {
                Log.d(LOG_TAG, String.format(
                        "HURRAH, WE CAN NOW CREATE THE FITNESS PROFILE RECORD WITH THE " +
                                "CORRECT VALUE OF THE USER ID, USER ID = %d", user.getId()));

                tempFitnessProfile.setUserId(user.getId());

                m_fitnessProfileViewModel.insertNewFitnessProfile(tempFitnessProfile);

                //Verify that the fitnessProfile record for the user was successfully inserted into the database table
                m_fitnessProfileViewModel.getFitnessProfile(user.getId()).observe(this, fitnessProfile -> {
//                        m_fitnessProfileViewModel.getFitnessProfile(user.getId()).removeObservers(this);
                    if (fitnessProfile != null) {
                        Log.d(LOG_TAG, "Successfully retrieved fitness profile after insertion into database");
                        Log.d(LOG_TAG, "Fitness profile data:");
                        Log.d(LOG_TAG, "First name: " + fitnessProfile.getM_fName());
                        Log.d(LOG_TAG, "Last name: " + fitnessProfile.getM_lName());
                        Log.d(LOG_TAG, "User id: " + fitnessProfile.getUserId());
                        Log.d(LOG_TAG, "Sex: '" + fitnessProfile.getM_sex() + "'");
                        Log.d(LOG_TAG, "Height: " + fitnessProfile.getM_heightFeet() + "Ft " + fitnessProfile.getM_heightInches() + " Inches");

                    }
                    m_fitnessProfileViewModel.getFitnessProfile(user.getId()).removeObservers(this);
                });


            }
            userViewModel.getUser().removeObservers(this);
        });

        viewTransitionHandler();
    }

    @NonNull
    private FitnessProfile instantiateFitnessProfile() {
        FitnessProfile tempFitnessProfile = new FitnessProfile();
        tempFitnessProfile.setM_fName(etxt_firstName.getText().toString());
        tempFitnessProfile.setM_lName(etxt_lastName.getText().toString());
        tempFitnessProfile.setM_dob(etxt_dob.getText().toString());
        tempFitnessProfile.setM_sex(etxt_sex.getText().toString());
        tempFitnessProfile.setM_city(etxt_city.getText().toString());
        tempFitnessProfile.setM_lifestyleSelection(
                lifestyleSlider.getProgress() == 2 ? "ACTIVE" : lifestyleSlider.getProgress() == 1 ? "MODERATE" : "SEDENTARY");

        tempFitnessProfile.setM_weightGoal(weightGoalSlider.getProgress() == 2 ?
                "GAIN" : weightGoalSlider.getProgress() == 1 ? "MAINTAIN" : "LOSE");
        tempFitnessProfile.setM_country(getCountryNames().get(countrySpinner.getSelectedItemPosition()));
        tempFitnessProfile.setM_weightInPounds(Integer.parseInt(etxt_weight.getText().toString()));
        tempFitnessProfile.setM_heightFeet(Integer.parseInt(etxt_heightFeet.getText().toString()));

        String heightInches = etxt_heightInches.getText().toString();
        tempFitnessProfile.setM_heightInches(!isNotNullOrEmpty(heightInches) ? 0 : Integer.parseInt(heightInches));

        tempFitnessProfile.setM_profileImage(imageUriString);

        final int lbsPerWeek = Integer.parseInt(etxt_lbsPerWeek.getText().toString());

        if (tempFitnessProfile.getM_weightGoal().equalsIgnoreCase("Maintain")) {
            tempFitnessProfile.setM_lbsPerWeek(0); //If they're trying to maintain, ignore the lbs/week field, this should be 0
        }
        else if (tempFitnessProfile.getM_weightGoal().equalsIgnoreCase("Lose")) { //If they're trying to lose weight, set this to be negative
            tempFitnessProfile.setM_lbsPerWeek(-1 * lbsPerWeek);
        }
        else {
            tempFitnessProfile.setM_lbsPerWeek(lbsPerWeek);
        }
        return tempFitnessProfile;
    }

    private void viewTransitionHandler() {
        if (!getResources().getBoolean(R.bool.isWideDisplay)){
            Intent intent = new Intent(getContext(), ProfileSummaryActivity.class);
            startActivity(intent);
        } else {
//            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fl_detail_wd, new ProfileSummaryFragment());
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commit();
            Intent intent = new Intent(getContext(), DashboardActivity.class);
            startActivity(intent);
        }
    }

    private boolean isUserInputDataValid() {
        if (!isValidName(etxt_firstName.getText().toString())) {
            Toast.makeText(getContext(), "Invalid first name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidName(etxt_lastName.getText().toString())) {
            Toast.makeText(getContext(), "Invalid last name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidDobFormat(etxt_dob.getText().toString())) {
            Toast.makeText(getContext(), "Invalid date of birth.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidSex(etxt_sex.getText().toString())) {
            Toast.makeText(getContext(), "Invalid sex.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidCity(etxt_city.getText().toString())) {
            Toast.makeText(getContext(), "Invalid city.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidWeight(etxt_weight.getText().toString())) {
            Toast.makeText(getContext(), "Invalid weight.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidHeight(etxt_heightFeet.getText().toString(), etxt_heightInches.getText().toString())) {
            Toast.makeText(getContext(), "Please enter a valid height in feet/inches.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!isValidWeightPlan(etxt_lbsPerWeek.getText().toString())) {
            Toast.makeText(getContext(), "Please enter a valid weekly weight goal (i.e., a maximum weight management change of 5lbs/week)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void displayFilePickerDialog() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private void profileImageButtonHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_profile_image_button);
        builder.setIcon(R.drawable.ic_directions_run);

        builder.setPositiveButton(R.string.dialog_button_file_picker, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "Dialog file picker button clicked");
                displayFilePickerDialog();
            }
        });
        builder.setNeutralButton(R.string.dialog_button_dismiss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "Dialog dismiss button clicked");
            }
        });
        builder.setNegativeButton(R.string.dialog_button_camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(LOG_TAG, "Camera button clicked");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        builder.create().show();
    }
//    public Uri getUriFromBitmap(Context context, Bitmap bitmap) {
    public Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "img", null);
        return Uri.parse(path);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getActivity().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return getResources().getBoolean(R.bool.isWideDisplay) ?
                Bitmap.createScaledBitmap(image, WIDE_DISPLAY_IMG_DIM, WIDE_DISPLAY_IMG_DIM, false):
                Bitmap.createScaledBitmap(image, MOBILE_IMG_DIM, MOBILE_IMG_DIM, false);
    }

    public String getImageFilePathFromUri(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getContext().getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.d(LOG_TAG, "getImagefilePathFromUri Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
