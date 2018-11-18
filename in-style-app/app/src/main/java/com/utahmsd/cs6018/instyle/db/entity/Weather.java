package com.utahmsd.cs6018.instyle.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(indices = {@Index(value = {"city", "country"}, unique = true)})
public class Weather {

    public Temperature createTemp(double currTemp, double min, double max) {
        Temperature temperature = new Temperature();
        temperature.temp = currTemp;
        temperature.tempMin = min;
        temperature.tempMax = max;

        return temperature;
    }

    public static class Temperature {
        @ColumnInfo(name = "current_temp")
        public double temp;

        @ColumnInfo(name = "temp_min")
        public double tempMin;

        @ColumnInfo(name = "temp_max")
        public double tempMax;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "country")
    private String countryCode;

    @ColumnInfo(name = "lat")
    private float latitude;

    @ColumnInfo(name = "lon")
    private float longitude;

    @ColumnInfo(name = "summary")
    private String forecastMain;

    @ColumnInfo(name = "detail")
    private String forecastDescription;

    @Embedded
    private Temperature temperature;

    @ColumnInfo(name = "wind")
    private float windSpeed;

    @ColumnInfo(name = "pressure")
    private int pressure;

    @ColumnInfo(name = "humidity")
    private int humidity;

    @ColumnInfo(name = "last_updated")
    private Date lastUpdated;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getForecastMain() {
        return forecastMain;
    }

    public void setForecastMain(String forecastMain) {
        this.forecastMain = forecastMain;
    }

    public String getForecastDescription() {
        return forecastDescription;
    }

    public void setForecastDescription(String forecastDescription) { this.forecastDescription = forecastDescription; }

    public Temperature getTemperature() { return temperature; }

    public void setTemperature(Temperature temperature) { this.temperature = temperature; }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
