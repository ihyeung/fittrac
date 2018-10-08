package com.example.mcresswell.project01.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.mcresswell.project01.db.entity.FitnessProfile;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for querying FitnessProfile table in database.
 * Three scenarios exist where the DAO will be used:
 * 1. An existing user views their fitness data
 * 2. An existing user edits their fitness data
 * 3. An new user enters their fitness data for the first time (same query as scenario 2)
 *
 *  * An Optional<FitnessProfile> is returned in the case where no fitness profile account exists
 *  for the m_userID that is given from the User table.  This prevents an exception from being thrown.
 */
@Dao
public interface FitnessProfileDao {
    @Query("SELECT * FROM FitnessProfile "  +
            "JOIN User ON FitnessProfile.m_userID = User.profile_id " +
            "WHERE FitnessProfile.m_userID = User.profile_id")
    Optional<FitnessProfile> findByuserID(int userID);

    @Insert
    void insertNewUserData(FitnessProfile fitnessProfile);

    @Update
    void updateExistingFitnessProfileData(FitnessProfile fitnessProfile);

    @Query("SELECT * FROM FitnessProfile ORDER BY m_userID ASC")
    List<FitnessProfile> getAllFitnessProfileData();
}

