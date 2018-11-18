package com.utahmsd.cs6018.instyle.util;

import android.util.Log;

import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.util.mapper.CountryCodeMapper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidCity;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidCountryName;
import static com.utahmsd.cs6018.instyle.util.mapper.CountryCodeMapper.getCountryCode;

public class WeatherUtils {

    private static final String LOG = WeatherUtils.class.getSimpleName();

    private static final String API_BASE_URL = "http://api.openweathermap.org";
    private static final String API_ENDPOINT = "/data/2.5/weather?q=";
    private static final String OWM_API_KEY = "718324f1015a96a2369287e867133dd9";
    private static final String API_KEY_QUERY = "&appid=" + OWM_API_KEY;

    public static final String DEFAULT_CITY = "SALT LAKE CITY";
    public static final String DEFAULT_COUNTRY = "United States";
    public static final String INVALID_CITY_URL_JSON_RESPONSE = "{\"cod\":\"404\",\"message\":\"city not found\"}";

    private static final String FARENHEIT = " °F";

    private static String getAbsoluteUrl(String relativeUrl) {
        return API_BASE_URL + API_ENDPOINT + relativeUrl;
    }

    public static URL buildWeatherApiUrl(String city, String countryName){
        if (!isValidCity(city)) {
            return buildDefaultWeatherApiUrl();
        }
        URI uri = null;
        String countryFormatted = formatCaseCountryCodeFromCountryName(countryName);
        if (countryFormatted == null) {
            uri = URI.create(getAbsoluteUrl( city.replace(" ", "+") + API_KEY_QUERY));
        } else {
            uri = URI.create(getAbsoluteUrl(city.replace(" ", "+") + "," + countryFormatted + API_KEY_QUERY));
        }
        Log.d(LOG, "Weather url: " + uri.toString());
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            Log.d(LOG, "Malformed URL: " + uri.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildDefaultWeatherApiUrl() {
        try {
            return URI.create(getAbsoluteUrl(DEFAULT_CITY.replace(" ", "+") + "," +
                    getCountryCode(DEFAULT_COUNTRY) + API_KEY_QUERY)).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printWeather(Weather weather) {
        System.out.println("Weather Forecast for " + weather.getCity() + ", " + weather.getCountryCode()+ ":\n");
        Log.d(LOG, "Weather Forecast for " + weather.getCity() + ", " + weather.getCountryCode() + ":\n");

        System.out.println("coords: " + weather.getLatitude() + ", " + weather.getLongitude());
        System.out.println("forecast: " + weather.getForecastMain());
        System.out.println("forecast description: " + weather.getForecastDescription());
        System.out.println("temp: " + (weather.getTemperature().temp) + " F");
        System.out.println("temp_min: " + convertAndFormatKelvinTemp(weather.getTemperature().tempMin) + " F");
        System.out.println("temp_max: " + convertAndFormatKelvinTemp(weather.getTemperature().tempMax) + " F");
        System.out.println("pressure: " + weather.getPressure());
        System.out.println("humidity: " + weather.getHumidity() + "%");
        System.out.println("windspeed: " + weather.getWindSpeed());
        System.out.println("Last updated: " + weather.getLastUpdated());
    }

    public static double kelvinToFarenheit(double tempInKelvin) {
        return tempInKelvin * (9/5.0) - 459.67;
    }

    public static String convertAndFormatKelvinTemp(double tempInKelvin) {
        return String.format(Locale.US,"%.1f", kelvinToFarenheit(tempInKelvin)) + FARENHEIT;
    }

    public static String formatFarenheitTemp(double farenheitTemp) {
        return String.format(Locale.US, "%.1f", farenheitTemp) + FARENHEIT;
    }

    public static String formatCaseCity(String city) {
        if (!isValidCity(city)) {
            return null;
        }
        String formattedCity = "";
        String[] cityName = city.split( "\\s+");
        for (String each : cityName) {
            String s = each.substring(0,1).toUpperCase() + each.substring(1).toLowerCase();
            formattedCity  +=  s + " ";
        }
        return formattedCity.trim();
    }

    public static String formatCaseCountryCodeFromCountryName(String countryName) {
        return isValidCountryName(countryName) ? getCountryCode(formatCaseCity(countryName)) : null;
    }
}
