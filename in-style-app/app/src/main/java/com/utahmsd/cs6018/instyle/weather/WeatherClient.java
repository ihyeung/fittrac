package com.utahmsd.cs6018.instyle.weather;

import android.util.Log;

import com.utahmsd.cs6018.instyle.db.entity.Weather;
import com.utahmsd.cs6018.instyle.util.ValidationUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import javax.inject.Singleton;

import static com.utahmsd.cs6018.instyle.util.WeatherUtils.INVALID_CITY_URL_JSON_RESPONSE;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.buildWeatherApiUrl;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.printWeather;


/**
 * WeatherClient class that interfaces with the OpenWeatherMapAPI to make RESTful API calls to
 * retrieve Weather updates when either of the the following scenarios applies:
 * 1. There is no existing weather data for the given user's location.
 * 2. Existing weather data exists for the given user's location, but the data is more than 5 minutes
 * old.
 */

@Singleton
public class WeatherClient {

    private static final String LOG_TAG = WeatherClient.class.getSimpleName();

    public static Weather fetchCurrentWeather(String city, String country) {
        final URL FETCH_WEATHER_URL = buildWeatherApiUrl(city, country);
        try {
            final String RESPONSE_BODY = getJsonDataFromUrl(FETCH_WEATHER_URL);
            return parseWeatherFromApiResponse(RESPONSE_BODY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Weather parseWeatherFromApiResponse(String jsonResponse) {
        if (!ValidationUtils.isNotNullOrEmpty(jsonResponse) ||
                jsonResponse.equals(INVALID_CITY_URL_JSON_RESPONSE)) {
            return null;
        }
        WeatherForecast forecast = new WeatherForecast();
        Weather weather = forecast.initWeather(jsonResponse);
        if (weather != null) {
            printWeather(weather);
        }
        return weather;
    }

    public static String getJsonDataFromUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        StringBuilder response = new StringBuilder();
        Scanner s = null;
        try {
            s = new Scanner(connection.getInputStream());

            if (connection.getResponseCode() != 200) {
                Log.d(LOG_TAG,"Error ResponseCode: " + connection.getResponseCode());
                return response.append(INVALID_CITY_URL_JSON_RESPONSE).toString();
            }

            while (s.hasNext()) {
                String next = s.nextLine();
                response.append(next);
            }
        } catch (FileNotFoundException e) {
            return response.append(INVALID_CITY_URL_JSON_RESPONSE).toString();
        }
        if (s != null) {
            s.close();
        }
        if (connection != null) {
            connection.disconnect();
        }
        return response.toString();
    }



//    private static Retrofit retrofit;

//    @Singleton
//    public static Retrofit getRetrofitInstance() {
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(API_BASE_URL)
//                    .build();
//        }
//        return retrofit;
//    }

//    public interface WeatherService {
//        @GET("/data/2.5/weather?q={city},{country}&appid={api_key}")
//        Call<WeatherForecast> getnWeatherByCityCountry(@Path("city") String city,
//                                                       @Path("country") String country,
//                                                       @Path("api_key") String apiKey);
//
//        @GET("/data/2.5/weather?q={city}&appid={api_key}")
//        Call<WeatherForecast> getWeatherByCity(@Path("city") String city,
//                                               @Path("api_key") String apiKey);
//
//        @GET("/data/2.5/weather?q=" + DEFAULT_CITY + "," + DEFAULT_COUNTRY + "&appid={api_key}")
//        Call<WeatherForecast> getDefaultWeather(@Path("api_key") String apiKey);
//
//
//    }




}
