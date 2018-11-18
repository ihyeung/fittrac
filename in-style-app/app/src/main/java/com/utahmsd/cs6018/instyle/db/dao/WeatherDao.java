package com.utahmsd.cs6018.instyle.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.utahmsd.cs6018.instyle.db.entity.Weather;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for querying Weather table in database.
 * Scenarios where this DAO will be used:
 * 1. A weather forecast for a new location is stored in database.
 * 2. An updated weather forecast for an existing location is stored.
 * 3. The database is queried for a weather forecast for a given location.
 *
 */

@Dao
public interface WeatherDao {

    @Query("SELECT * FROM Weather WHERE city = :city AND (country IS NULL OR country LIKE :country)")
    LiveData<Weather> findWeatherByLocation(String city, String country);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWeather(Weather weather);

    @Insert
    void insertAllWeather(List<Weather> weatherList);

    @Query("DELETE FROM Weather")
    void deleteAllWeather();

    @Query("SELECT * FROM Weather ORDER BY city ASC")
    LiveData<List<Weather>> loadAllWeather();

    @Query("SELECT * FROM Weather where id = :weatherId")
    LiveData<Weather> findWeatherById(int weatherId);

}
