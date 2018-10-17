package com.example.mcresswell.project01.db.repo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mcresswell.project01.db.InStyleDatabase;
import com.example.mcresswell.project01.db.dao.UserDao;
import com.example.mcresswell.project01.db.entity.User;
import com.example.mcresswell.project01.util.UserGenerator;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

/**
 * This class exposes User account data to the UI layer via the UserListViewModel.
 *
 * Repository/model class for the User entity that handles all business logic associated
 * handling user account data. This class interfaces with the User entity in the in-memory database.
 * User account data retrieved from the database is passed to the UserListViewModel class.
 */
@SuppressWarnings("ALL")
@Singleton
public class UserRepository {
    private static final String LOG_TAG = UserRepository.class.getSimpleName();

    private final UserDao mUserDao;

    private InStyleDatabase inStyleDatabase;
    private static UserRepository userRepository;

    private MediatorLiveData<List<User>> m_observableUserList;
    private MediatorLiveData<User> m_observableUser;

    private static final int NUM_TEST_USERS = 10; //Number of random test users to generate
    private static final int MAX_USERS = 1000; //Maximum allowable of users in table


    private UserRepository(final InStyleDatabase database) {
        inStyleDatabase = database;
        mUserDao = inStyleDatabase.userDao();

//        asyncInsertTestUser("test@test.com", "password", "Hello", "Kitty", Date.valueOf("2018-01-01"));

//        List<User> testUsers = UserGenerator.generateUserData(50);

        //Populate with randomly generated test data
//        asyncPopulateWithUserList(testUsers);
//        Log.d(LOG_TAG, "Users generated and added to database.");

        addLiveDataListenerSources();

    }

    private void addLiveDataListenerSources() {
        m_observableUserList = new MediatorLiveData<>();
        m_observableUserList.setValue(null);

        //Add listener for livedata source for List<User>
        m_observableUserList.addSource(mUserDao.loadAllUsers(),
                users -> {
                    Log.d(LOG_TAG, "LiveData<List<<User>> loadAllUsers onChanged");
                    if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
                        m_observableUserList.setValue(users);
                    }
                });

        m_observableUser = new MediatorLiveData<>();
        m_observableUser.setValue(null);

        //Add listener for livedata source for User
//        m_observableUser.addSource(mUserDao.findFirstUserRecord(), user -> { //FIXME: ADDED THIS IN PURELY FOR DEBUGGING, REMOVE THIS LATER
//            if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
//
//                Log.d(LOG_TAG, "Broadcasting updated value of LiveData<User> to observers");
//
//                m_observableUser.setValue(user);
//            }
//        });
    }

    /**
     * Static method to ensure only one instance of the UserRepository is instantiated.
     * @param database
     * @return
     */
    public static UserRepository getInstance(final InStyleDatabase database) {
        if (userRepository == null) {
            synchronized (UserRepository.class) {
                if (userRepository == null) {
                    userRepository = new UserRepository(database);
                }
            }
        }
        return userRepository;
    }

    ///////// Getters ///////////

    public LiveData<User> getUser() {
        return m_observableUser;
    }

    public LiveData<List<User>> getUsers() {
        return m_observableUserList;
    }

    ///////// CRUD Operations ///////////

    public void insert(User user) {
        asyncInsertUser(user);
    }

    public LiveData<User> find(String userEmail) {
        m_observableUser.addSource(mUserDao.findUserByEmail(userEmail), user -> {
            if (user != null) {
                Log.d(LOG_TAG, String.format("findUserByEmail() for email  %s LiveData<User> onChanged", user.getEmail()));
                if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
                    Log.d(LOG_TAG, "Broadcasting findUserByEmail() result to observers... ");
                    m_observableUser.setValue(user);
                }
            }
        });
        asyncLoadUser(userEmail);

        return m_observableUser;
    }


    public void update(User user) {
        asyncUpdateUser(user);
    }

    public void delete(User user) {
        asyncDeleteUser(user);
    }

    public void deleteAll() {
        asyncDeleteAllUsers();
    }

    public boolean authenticateUser(User user) {
        if (user == null) {
            return false;
        }
        LiveData<User> result = find(user.getEmail());
        return result.getValue().getPassword().equals(user.getPassword());
    }


    //////////// ASYNC TASKS FOR A SINGLE USER /////////////

    @SuppressLint("StaticFieldLeak")
    private void asyncLoadUser(String email) {
        new AsyncTask<String, Void, User>() {
            @Override
            protected User doInBackground(String... params) {
                String userToLoad = params[0];
                Log.d(LOG_TAG, String.format("Retrieving user record with email %s", userToLoad));

                LiveData<User> user = mUserDao.findUserByEmail(userToLoad);

                return user.getValue();
            }

            @Override
            protected void onPostExecute(User user) {
                m_observableUser.setValue(user);
            }
        }.execute(email);
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncInsertUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                User userToInsert = params[0];
                Log.d(LOG_TAG, String.format("Inserting new user record with email %s into database", userToInsert.getEmail()));
                //Insert new user record into database
                mUserDao.insertUser(userToInsert);

                Log.d(LOG_TAG, "Inserting User data . . .");
                return null;
            }
        }.execute(user);
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncUpdateUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                User userToUpdate = params[0];
                Log.d(LOG_TAG, String.format("Updating existing user record with email %s in database", userToUpdate.getEmail()));

                //Updating existing user record in database
                mUserDao.updateUser(userToUpdate);

                Log.d(LOG_TAG, "Updating User data . . .");
                return null;
            }
        }.execute(user);
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncDeleteUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                User userToDelete = params[0];
                Log.d(LOG_TAG, String.format("Deleting existing user record with email %s from database", userToDelete.getEmail()));

                //Delete user record from database
                mUserDao.deleteUser(userToDelete);
                Log.d(LOG_TAG, "Deleting User data . . .");
                return null;
            }
        }.execute(user);
    }


    ////////////////// ASYNC TASKS FOR A LIST OF USERS //////////////////

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("unchecked")
    public void asyncResetUserDatabase(int numRecords) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (numRecords > MAX_USERS) {
                    Log.d(LOG_TAG, String.format("Number of records in User table exceeds max of %d. Resetting database contents", MAX_USERS));

                    mUserDao.deleteAllUsers();
                }

                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("unchecked")
    public void asyncPopulateWithUserList(List<User> users) {
        new AsyncTask<List<User>, Void, Void>() {
            @Override
            protected Void doInBackground(List<User>... params) {

                Log.d(LOG_TAG, "Inserting test data into database table to populate.");

                //Insert randomly generated user data
                mUserDao.insertAllUsers(params[0]);

                return null;
            }
        }.execute(users);
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncLoadAllUsers() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                Log.d(LOG_TAG, "Loading users from database");
                LiveData<List<User>> listLiveData = mUserDao.loadAllUsers();

                if (listLiveData.getValue() != null) {
                    return listLiveData.getValue();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> userList) {
                m_observableUserList.setValue(userList);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncDeleteAllUsers() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.d(LOG_TAG, "Deleting all users from database");
                mUserDao.deleteAllUsers();

//                asyncInsertTestUser("test@test.com", "password", "Hello", "Kitty", Date.valueOf("2018-01-01"));

                return null;
            }

            @Override
            protected void onPostExecute(Void voidResult) {
                m_observableUserList.setValue(Collections.emptyList());
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void asyncInsertTestUser(String email, String pass, String first, String last, Date date) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                insertTestUser(email, pass, first, last, date);

                return null;

            }
        }.execute();
    }

    private void insertTestUser(String email, String password,
                                String firstName, String lastName, Date joinDate ) {
        User testUser = new User();
        testUser.setEmail(email);
        testUser.setPassword(password);
        testUser.setFirstName(firstName);
        testUser.setLastName(lastName);
        testUser.setJoinDate(joinDate);
        mUserDao.insertUser(testUser);

        Log.d(LOG_TAG, "GENERIC TEST USER successfully inserted into User database");
    }
}
