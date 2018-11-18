package com.utahmsd.cs6018.instyle.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class GeocoderLocationUtilsUnitTest {

    private final String DEFAULT_CITY = "PROVO";
    private final String DEFAULT_COUNTRY = "US";
    private final String DEFAULT_COORDS = "40.2338438,-111.6585337";

    @Test
    public void getCoordinatesFromCityCountry_validLocation_returnsValidCoordinates(){
        String coordinates = null;
        try {
            coordinates = GeocoderLocationUtils.
                    getCoordinatesFromCityAndCountryCode(DEFAULT_CITY, DEFAULT_COUNTRY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(coordinates);
        assertEquals(coordinates, DEFAULT_COORDS);
    }

    @Test
    public void getCoordinatesFromCityCountry_invalidLocation_returnsNull(){
        String coordinates = null;
        try {
            coordinates = GeocoderLocationUtils.
                    getCoordinatesFromCityAndCountryCode("welisasdeqewjglisej", "US");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNull(coordinates);
    }

}