package com.utahmsd.cs6018.instyle;

import com.utahmsd.cs6018.instyle.util.WeatherUtils;
import com.utahmsd.cs6018.instyle.weather.WeatherClient;

import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class WeatherClientUnitTest {

    @Test
    public void getJsonDataFromUrl_defaultLocation_returnsValidJson() {
        String s = null;
        try {
            s = WeatherClient.getJsonDataFromUrl(WeatherUtils.buildDefaultWeatherApiUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(s);
        assertNotEquals(s, WeatherUtils.INVALID_CITY_URL_JSON_RESPONSE);
    }

    @Test
    public void getJsonDataFromUrl_invalidLocation_returnsErrorJson() {
        String s = null;
        try {
            s = WeatherClient.getJsonDataFromUrl(WeatherUtils.buildWeatherApiUrl("INVALIDCITY", null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(s);
        assertEquals(s, WeatherUtils.INVALID_CITY_URL_JSON_RESPONSE);
    }

    @Test
    public void buildWeatherApiUrl() throws MalformedURLException {
        URL url = WeatherUtils.buildWeatherApiUrl("PROVO", "US");
        assertTrue(url.equals(new URL("http://api.openweathermap.org/data/2.5/weather?q=PROVO,US&appid=718324f1015a96a2369287e867133dd9")));

    }

    @Test
    public void buildWeatherApiUrl_invalidCountry_returnsValidUrl() throws MalformedURLException {
        URL url = WeatherUtils.buildWeatherApiUrl("PROVO", null);
        assertTrue(url.equals(new URL("http://api.openweathermap.org/data/2.5/weather?q=PROVO&appid=718324f1015a96a2369287e867133dd9")));
    }

    @Test
    public void buildWeatherApiUrl_invalidCityInvalidCountry_returnsDefaultUrl() {
        URL url = WeatherUtils.buildWeatherApiUrl(" ", null);
        assertTrue(url.equals(WeatherUtils.buildDefaultWeatherApiUrl()));
    }

    @Test
    public void getJsonDataFromUrl_validCity_returnsWeatherForecast() {
        URL url = WeatherUtils.buildWeatherApiUrl("PROVO", null);
        String result = null;
        try {
            result = WeatherClient.getJsonDataFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(WeatherClient.parseWeatherFromApiResponse(result));
    }

    @Test
    public void getJsonDataFromUrl_invalidCity_returnsNullWeatherForecast() throws MalformedURLException {
        URL url = WeatherUtils.buildWeatherApiUrl("INVALIDCITY", null);
        String result = null;
        try {
            result = WeatherClient.getJsonDataFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNull(WeatherClient.parseWeatherFromApiResponse(result));
    }


}