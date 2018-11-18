package com.utahmsd.cs6018.instyle.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.utahmsd.cs6018.instyle.activity.ProfileEntryActivity;
import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.viewmodel.FitnessProfileViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.UserListViewModel;
import com.utahmsd.cs6018.instyle.viewmodel.UserViewModel;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.util.Constants;

import java.time.Instant;
import java.util.Date;

import static com.utahmsd.cs6018.instyle.util.Constants.SAVE_INSTANCE_STATE;
import static com.utahmsd.cs6018.instyle.util.Constants.VIEW_STATE_RESTORED;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isNotNullOrEmpty;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidAlphaChars;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidEmail;

/**
 * Fragment class for screen that user sees when the Create Account button is pressed in the Login screen.
 */
public class CreateAccountFragment extends Fragment {

    private static final String LOG_TAG = CreateAccountFragment.class.getSimpleName();

    private Button m_btn_createAccount;
    private EditText m_email, m_password, m_firstName, m_lastName;
    private UserViewModel userViewModel;
    private FitnessProfileViewModel fitnessProfileViewModel;
    private UserListViewModel userListViewModel;

    public CreateAccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE);
        super.onCreate(savedInstanceState);

        configureViewModels();
    }

    private void configureViewModels() {
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);

//        userViewModel.getUser().observe(this, user -> {
//            Log.d(LOG_TAG, "UserViewModel observer for getUser()");
//        });


        userListViewModel = ViewModelProviders.of(getActivity()).get(UserListViewModel.class);
        fitnessProfileViewModel = ViewModelProviders.of(getActivity()).get(FitnessProfileViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, Constants.CREATE_VIEW);

        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        initializeFragmentView(view);

        setOnClickListeners();

        return view;
    }

    private void initializeFragmentView(View view){
        m_email = view.findViewById(R.id.text_email_create_account);
        m_password = view.findViewById(R.id.text_password_create_account);
        m_firstName = view.findViewById(R.id.text_first_name_create_account);
        m_lastName = view.findViewById(R.id.text_last_name_create_account);
        m_btn_createAccount = view.findViewById(R.id.btn_create_account);
    }

    private void setOnClickListeners() {
        m_btn_createAccount.setOnClickListener(listener -> createAccountButtonHandler());
    }

    private void createAccountButtonHandler() {
        if (isAccountDataValid()) {
            if (!isUniqueUserLogin(m_email.getText().toString())) {
                Toast.makeText(getContext(), "A user account with that email already exists.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            User newUser = createNewUser();
            //Add new user to database
            userViewModel.createUser(newUser);
            Observer observer = new Observer<User>() {
                @Override
                public void onChanged(@Nullable User user) {
                    if (user != null) {
                        if (newUser.getEmail().equals(user.getEmail())) {

                            userViewModel.getUser().removeObserver(this);
                            viewTransitionHandler();
                        }
                    }
                }
            };
            userViewModel.getUser().observe(this, observer);
//            //Add observer that logs all of the records in the database after this user has been added
//            userListViewModel.getUserList().observe(this, userList -> {
//                if (userList != null) {
//                    userList.forEach(each -> {
//                        Log.d(LOG_TAG, String.format(
//                                "User record: id=%d, email='%s', firstName='%s', lastName='%s'",
//                                each.getId(), each.getEmail(), each.getFirstName(), each.getLastName()));
//                    });
//                }
            }
        }

        private void viewTransitionHandler() {
            if (getResources().getBoolean(R.bool.isWideDisplay)) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fl_master_wd, new DashboardFragment());
                fragmentTransaction.replace(R.id.fl_detail_wd, new ProfileEntryFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                Intent intent = new Intent(getActivity(), ProfileEntryActivity.class);
                startActivity(intent);
            }
        }

        private User createNewUser() {
            User newUser = new User();
            newUser.setEmail(m_email.getText().toString());
            newUser.setPassword(m_password.getText().toString());
            newUser.setFirstName(m_firstName.getText().toString());
            newUser.setLastName(m_lastName.getText().toString());
            newUser.setJoinDate(Date.from(Instant.now()));
            return newUser;
        }

        private boolean isUniqueUserLogin(String email) {
            LiveData<User> userResult = userViewModel.findUser(email);
            if (userResult.getValue() != null && userResult.getValue().getEmail().equals(email)) {
                Log.d(LOG_TAG, "Email is not unique. A user with that email already exists.");
//            Log.d(LOG_TAG, "EMAIL: " + userViewModel.getUser().getValue().getEmail());
//            Log.d(LOG_TAG, "PASSOWRD: " + userViewModel.getUser().getValue().getPassword());
//            Log.d(LOG_TAG, "First name: " +userViewModel.getUser().getValue().getFirstName());
//            Log.d(LOG_TAG, "Last Name: " + userViewModel.getUser().getValue().getLastName());
//            Log.d(LOG_TAG, "Date joined: " +userViewModel.getUser().getValue().getJoinDate());

                Log.d(LOG_TAG, "EMAIL: " + userResult.getValue().getEmail());
                Log.d(LOG_TAG, "PASSOWRD: " + userResult.getValue().getPassword());
                Log.d(LOG_TAG, "First name: " + userResult.getValue().getFirstName());
                Log.d(LOG_TAG, "Last Name: " + userResult.getValue().getLastName());
                Log.d(LOG_TAG, "Date joined: " + userResult.getValue().getJoinDate());

                return false;
            }
            return true;
        }

        private boolean isAccountDataValid () {
            if (!isValidEmail(m_email.getText().toString())) {
                Toast.makeText(getContext(), "Invalid email.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!isNotNullOrEmpty(m_password.getText().toString())) {
                Toast.makeText(getContext(), "Invalid password.", Toast.LENGTH_SHORT).show();

            }
            else if (!isValidAlphaChars(m_firstName.getText().toString())) {
                Toast.makeText(getContext(), "Invalid first name.", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if (!isValidAlphaChars(m_lastName.getText().toString())) {
                Toast.makeText(getContext(), "Invalid last name.", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        @Override
        public void onViewStateRestored (Bundle savedInstanceState) {
            Log.d(LOG_TAG, VIEW_STATE_RESTORED);
            super.onViewStateRestored(savedInstanceState);

        }

        @Override
        public void onSaveInstanceState(Bundle bundle) {
            Log.d(LOG_TAG, SAVE_INSTANCE_STATE);

            super.onSaveInstanceState(bundle);
        }
    }
