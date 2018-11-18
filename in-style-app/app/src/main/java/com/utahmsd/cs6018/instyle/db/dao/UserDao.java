package com.utahmsd.cs6018.instyle.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.utahmsd.cs6018.instyle.db.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for querying User table in database.
 * Four scenarios exist where this DAO will be used:
 * 1. An existing user attempts to log in.
 * 2. A new user registers a new account.
 * 3. An existing user updates their account credentials (changes login email or password)
 * 4. An existing user elects to delete their account.
 *
 *  * An Optional<User> is returned in the case where no user account exists
 * for the email that was entered upon login. This prevents an exception from being thrown.
 *
 * Queries that return a LiveData object can be observed, so when a change in any of the tables
 * is detected, LiveData delivers a notification of that change to the registered observers.
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM User u WHERE u.email LIKE :email")
    LiveData<User> findUserByEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Insert
    void insertAllUsers(List<User> users);

    @Query("DELETE FROM User")
    void deleteAllUsers();


    @Query("SELECT * FROM User ORDER BY id ASC")
    LiveData<List<User>> loadAllUsers();

    @Query("SELECT COUNT(*) FROM User")
    LiveData<Integer> getUserCount();

}
