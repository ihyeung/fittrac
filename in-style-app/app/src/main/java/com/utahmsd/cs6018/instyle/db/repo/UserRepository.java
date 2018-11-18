package com.utahmsd.cs6018.instyle.db.repo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.dao.UserDao;
import com.utahmsd.cs6018.instyle.db.entity.User;

import java.sql.Date;
import java.util.ArrayList;
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

    private static final String TEST_EMAIL = "test@test.com";
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


//        List<User> testUsers = UserGenerator.generateUserData(50);

        //Populate with randomly generated test data
//        asyncPopulateWithUserList(testUsers);
//        Log.d(LOG_TAG, "Users generated and added to database.");

        addLiveDataListenerSources();

        loadUsersInDatabase();

    }

    private void addLiveDataListenerSources() {
        m_observableUserList = new MediatorLiveData<>();
        m_observableUserList.setValue(null);

        //Add listener for livedata source for List<User>
//        m_observableUserList.addSource(mUserDao.loadAllUsers(),
//                users -> {
//                    Log.d(LOG_TAG, "LiveData<List<<User>> loadAllUsers onChanged");
//                    if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
//                        m_observableUserList.setValue(users);
//
////                        if (users.isEmpty()) {
////                            //Only insert test record if database was cleared/reset
////                            asyncInsertTestUser("test@test.com",
////                                    "password", "Hello",
////                                    "Kitty", Date.valueOf("2018-01-01"));
////
////                        }
//                    }
//                });

        m_observableUser = new MediatorLiveData<>();
        m_observableUser.setValue(null);

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
        m_observableUser.setValue(user);
    }

    public LiveData<User> find(String userEmail) {
        m_observableUser.addSource(mUserDao.findUserByEmail(userEmail), user -> {
            if (user != null) {
                m_observableUser.removeSource(mUserDao.findUserByEmail(userEmail));

                Log.d(LOG_TAG, String.format("findUserByEmail() for email  %s LiveData<User> onChanged", user.getEmail()));
                if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
//                    Log.d(LOG_TAG, "Broadcasting findUserByEmail() result to observers... ");
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
        m_observableUser.setValue(null);
    }

    public void deleteAll() {
        asyncDeleteAllUsers();
    }

    public LiveData<List<User>> loadUsersInDatabase() {
        m_observableUserList.addSource(mUserDao.loadAllUsers(), userList -> {
            if (userList != null) {
                if (inStyleDatabase.isDatabaseCreated().getValue() != null) {
                    //            m_observableUserList.removeSource(mUserDao.loadAllUsers());
                    List<String> emailList = new ArrayList<>();
                    Log.d(LOG_TAG, "USER REPOSITORY HAS FINISHED LOADING USERS FROM DATABASE");
                    Log.d(LOG_TAG, "Number of users in User database: " + userList.size());

                    Log.d(LOG_TAG, "------------------------------------------");
                    Log.d(LOG_TAG, "------------------------------------------");

                    Log.d(LOG_TAG, "PRINTING USERS IN USER DATABASE");
                    Log.d(LOG_TAG, "\n");

                    userList.forEach(users -> {
                        emailList.add(users.getEmail());
                        Log.d(LOG_TAG, "\nUser data record: " + users.getId() + "\t'" + users.getEmail() + "'\t'" + users.getFirstName() + "'\t'" + users.getLastName() + "'\t'" + users.getJoinDate() + "'\t'" + "'");
                    });

                    Log.d(LOG_TAG, "\n");
                    Log.d(LOG_TAG, "------------------------------------------");
                    Log.d(LOG_TAG, "------------------------------------------");

                    if (!emailList.contains(TEST_EMAIL)) {
                        Log.d(LOG_TAG, "Testing user account test@test.com does not exist in database, adding to User table.");
                        asyncInsertTestUser("test@test.com",
                                "password", "Hello",
                                "Kitty", Date.valueOf("2018-01-01"));
                    }


                }
            }

        });

        asyncLoadAllUsers();

        return m_observableUserList;
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
                User userVal = user.getValue();
                return userVal;
            }

            @Override
            protected void onPostExecute(User user) {
                //A null check here does not work to check if the user is contained in the database.
                //For some reason, even though this method is supposed to be called after doInBackground()
                //Completes, it is always still initially null briefly while a valid existing user is being fetched.
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
                //A null check here does not work to check if User database table is empty.
                //For some reason, even though this method is supposed to be called after doInBackground()
                //Completes, it is always still initially null briefly while the list is being retrieved.
                //Aka it appears to execute while the database operation is still in progress
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

    }
}
