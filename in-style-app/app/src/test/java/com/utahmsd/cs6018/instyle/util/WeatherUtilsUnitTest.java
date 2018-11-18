package com.utahmsd.cs6018.instyle.util;

import org.junit.Test;

import static com.utahmsd.cs6018.instyle.util.WeatherUtils.convertAndFormatKelvinTemp;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.formatCaseCity;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.formatCaseCountryCodeFromCountryName;
import static com.utahmsd.cs6018.instyle.util.WeatherUtils.kelvinToFarenheit;
import static org.junit.Assert.*;

public class WeatherUtilsUnitTest {

    private static final double DELTA = 0.0003;

    @Test
    public void testKelvinToFarenheit() {
        assertEquals(kelvinToFarenheit(273.15), 32.0, DELTA);
    }

    @Test
    public void testConvertAndFormatKelvinTemp() {
        assertEquals(convertAndFormatKelvinTemp(273.15), "32.0 °F");
        assertEquals(kelvinToFarenheit(273.15), 32.0, DELTA);
    }

    @Test
    public void testFormatCaseCity() {
        assertEquals(formatCaseCity("new   york"), "New York");
        assertEquals(formatCaseCity("SALT LAKE CITY"), "Salt Lake City");
        assertEquals(formatCaseCity("provo"), "Provo");
        assertNull(formatCaseCity(null));

    }

    @Test
    public void testFormatCaseCountryCodeFromCountryName() {
        assertEquals(formatCaseCountryCodeFromCountryName("us"), "US");
        assertEquals(formatCaseCountryCodeFromCountryName("United States"), "US");
        assertEquals(formatCaseCountryCodeFromCountryName("US"), "US");
        assertEquals(formatCaseCountryCodeFromCountryName("INVALID COUNTRY CODE"), "US");
        assertNull(formatCaseCountryCodeFromCountryName(null));


    }
}