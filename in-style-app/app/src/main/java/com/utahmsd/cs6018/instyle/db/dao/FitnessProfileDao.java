package com.utahmsd.cs6018.instyle.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;

import java.util.List;

/**
 * Data Access Object (DAO) for querying FitnessProfile table in database.
 * Three scenarios exist where the DAO will be used:
 * 1. An existing user views their fitness data
 * 2. An existing user edits their fitness data
 * 3. An new user enters their fitness data for the first time
 *
 *  * An MutableLiveData<FitnessProfile> is returned in the case where no fitness profile account exists
 *  for the m_userID that is given from the User table.  This prevents an exception from being thrown.
 */
@Dao
public interface FitnessProfileDao {

// -------- Note of more functionality to add back in when FitnessProfile is working --------
//    -Had to (temporarily) modify database schema because FitnessProfile
// repo/entity/dao hasn't finished being implemented yet and foreign key
// constraint in User table fails (Temporarily removed foreign key constraint
// in User table), --> this change caused an IllegalStateException to be
// thrown so added in .fallbackToDestructiveMigration() method in
// InStyleDatabase builder (this clears the database when schema is modified).
// Also modified allowBackup field in androidManifest.xml.
// Will add foreign key constraint back to User entity once FitnessProfile
// is working.


//    A few things that I need from the database DAO to do is

//2- When I call the database findByuserID (and other queries that return data). I need to have a complete FitnessProfile, or specifically Null.
//3- When I call the database insert, delete and such, I need a boolean returned to flag if it succeeded or failed.


    //scenario 1
    @Query("SELECT * FROM FitnessProfile fp WHERE fp.user_id = :m_userID")
    LiveData<FitnessProfile> findByuserID(int m_userID);

    //scenario 2
    @Update
    void updateExistingFitnessProfileData(FitnessProfile fitnessProfile);

    //1- When I call the database insertNewFitnessProfile . I need it to check if the record is there, and then just replace if it exists with the new record changes, or insert it if it does not. There is a database option to do that.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertNewFitnessProfile(FitnessProfile fitnessProfile);
    long insertNewFitnessProfile(FitnessProfile fitnessProfile);

    @Delete
    void deleteFitnessProfileData(FitnessProfile fitnessProfile);

    @Insert
    void insertAll(List<FitnessProfile> fitnessProfiles);

    @Query("SELECT * FROM FitnessProfile") //YOU DONT NEED TO EXPLICITLY ORDER THIS BY ID, THATS THE DEFAULT ORDERING.
    List<FitnessProfile> getAllFitnessProfileData();
}

