package com.utahmsd.cs6018.instyle.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.dao.FitnessProfileDao;
import com.utahmsd.cs6018.instyle.db.dao.UserDao;
import com.utahmsd.cs6018.instyle.db.dao.WeatherDao;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.db.entity.User;
import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.util.DataTypeConverters;

import java.util.List;

/**
 * Class that serves as the main access point for the underlying connection to the application's
 * persisted, relational data.
 *
 * Access to the application's database follows the singleton design pattern when instantiating the
 * database (i.e., static method getDatabaseInstance() will reference a single instance of the database,
 * as opposed to generating multiple database instances).
 */
@Database(entities = {User.class, FitnessProfile.class, Weather.class}, version = 1)
@TypeConverters({DataTypeConverters.class})
public abstract class InStyleDatabase extends RoomDatabase {

    private static final String LOG_TAG = InStyleDatabase.class.getSimpleName();

    private static final String DATABASE_NAME = "instyle_database";

    //Abstract getters for each DAO
    public abstract UserDao userDao();

    public abstract FitnessProfileDao fitnessProfileDao();

    public abstract WeatherDao weatherDao();

    //DATABASE IS A SINGLETON
    private static volatile InStyleDatabase INSTANCE;

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    /**
     * Static method that ensures only one instance of the database is instantiated. If
     * another instance of the database exists, this method returns a reference to that database.
     * Otherwise, this method creates the database.
     * @param context
     * @return
     */
    public static InStyleDatabase getDatabaseInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (InStyleDatabase.class) {
                if (INSTANCE == null) {
                    Log.d(LOG_TAG, "Instantiating InStyle Database . . .");
                    //Create single instance of database with FitnessProfile, User, and Weather tables
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            InStyleDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration() //Clear database every time schema is changed
                            .build();

                    INSTANCE.updateIsDatabaseCreatedFlag(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    /**
     * If instance of database already exists, update mIsDatabaseCreated flag, which will post
     * this change to observers of this MutableLiveData.
     *
     * @param context
     */
    private void updateIsDatabaseCreatedFlag(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            //Post task to main thread to set mIsDatabaseCreated flag
            setDatabaseCreatedFlag();
        }
    }

    private void setDatabaseCreatedFlag() {
        mIsDatabaseCreated.postValue(true);
    }

    /**
     * Populates database with test/dummy data.
     *
     * @param database
     * @param userList           List of test user accounts.
     * @param fitnessProfileList List of test fitness profile records.
     * @param weatherList        List of test weather forecast data records.
     */
    private static void insertTestData(final InStyleDatabase database,
                                       final List<User> userList,
                                       final List<FitnessProfile> fitnessProfileList,
                                       final List<Weather> weatherList) {
        database.runInTransaction(() -> {
            database.userDao().insertAllUsers(userList);
            database.weatherDao().insertAllWeather(weatherList);
            database.fitnessProfileDao().insertAll(fitnessProfileList);
        });
    }

    /**
     * Helper method to check whether an instance of the database already exists.
     *
     * @return
     */
    public LiveData<Boolean> isDatabaseCreated() {
        return mIsDatabaseCreated;
    }

}
