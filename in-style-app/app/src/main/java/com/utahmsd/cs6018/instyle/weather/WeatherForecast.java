package com.utahmsd.cs6018.instyle.weather;

import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import static com.utahmsd.cs6018.instyle.util.WeatherUtils.convertAndFormatKelvinTemp;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.kelvinToFarenheit;

public class WeatherForecast {

    private Weather weather;

    private String coords;
    private String forecastMain;
    private String forecastDescription;

    private String temp;
    private String pressure;
    private String humidity;
    private String temp_min;
    private String temp_max;

    private String windSpeed;

    private String countryCode;

    private String city;

    public WeatherForecast() {}

    public Weather initWeather (String response) {

        //FIXME: Clean this up later if you have time
        JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();

        JsonObject coord = jsonObject.get("coord").getAsJsonObject();
        JsonObject weather = jsonObject.get("weather").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject main = jsonObject.get("main").getAsJsonObject();
        JsonObject wind = jsonObject.get("wind").getAsJsonObject();

        coords = String.valueOf(coord.get("lat")) + "," + String.valueOf(coord.get("lon"));
        forecastMain = String.valueOf(weather.get("main")).replace("\"", "");
        forecastDescription = String.valueOf(weather.get("description")).replace("\"", "");

        temp = convertAndFormatKelvinTemp(main.get("temp").getAsDouble());
        pressure = String.valueOf(main.get("pressure"));
        humidity = String.valueOf(main.get("humidity")) + "%";
        temp_min = convertAndFormatKelvinTemp(main.get("temp_min").getAsDouble());
        temp_max = convertAndFormatKelvinTemp(main.get("temp_max").getAsDouble());

        windSpeed = String.format(Locale.US, "%.1f", wind.get("speed").getAsDouble());

        countryCode = String.valueOf(jsonObject.get("sys").getAsJsonObject().get("country")).replace("\"", "");;
        city = String.valueOf(jsonObject.get("name")).replace("\"", "");

        //////////////////////////////////////////////////

        this.weather = new Weather();
        this.weather.setTemperature(
                this.weather.createTemp(main.get("temp").getAsDouble(),
                                        main.get("temp_min").getAsDouble(),
                                        main.get("temp_max").getAsDouble()));
        this.weather.setCity(city);
        this.weather.setCountryCode(countryCode);
        this.weather.setLatitude(coord.get("lat").getAsFloat());
        this.weather.setLongitude(coord.get("lon").getAsFloat());
        this.weather.setForecastMain(forecastMain);
        this.weather.setForecastDescription(forecastDescription);
        this.weather.setWindSpeed(wind.get("speed").getAsFloat());
        this.weather.setPressure(main.get("pressure").getAsInt());
        this.weather.setHumidity(main.get("humidity").getAsInt());
        this.weather.setLastUpdated(Date.from(Instant.now()));
        return this.weather;

    }

//    protected WeatherForecast(Parcel in) {
//        coords = in.readString();
//        forecastMain = in.readString();
//        forecastDescription = in.readString();
//        temp = in.readString();
//        pressure = in.readString();
//        humidity = in.readString();
//        temp_min = in.readString();
//        temp_max = in.readString();
//        windSpeed = in.readString();
//        countryCode = in.readString();
//        city = in.readString();
//    }

//    public static final Creator<WeatherForecast> CREATOR = new Creator<WeatherForecast>() {
//        @Override
//        public WeatherForecast createFromParcel(Parcel in) {
//            return new WeatherForecast(in);
//        }
//
//        @Override
//        public WeatherForecast[] newArray(int size) {
//            return new WeatherForecast[size];
//        }
//    };


    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    //FIXME: Clean this up later if you have time

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
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

    public void setForecastDescription(String forecastDescription) {
        this.forecastDescription = forecastDescription;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    //    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(FARENHEIT);
//        dest.writeString(coords);
//        dest.writeString(forecastMain);
//        dest.writeString(forecastDescription);
//        dest.writeString(temp);
//        dest.writeString(pressure);
//        dest.writeString(humidity);
//        dest.writeString(temp_min);
//        dest.writeString(temp_max);
//        dest.writeString(windSpeed);
//        dest.writeString(countryCode);
//        dest.writeString(city);
//    }
}
