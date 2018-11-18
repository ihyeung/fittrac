package com.utahmsd.cs6018.instyle.util;

import org.junit.Test;

import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isNotNullOrEmpty;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidCity;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidCountryName;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidDobFormat;
import static com.utahmsd.cs6018.instyle.util.ValidationUtils.isValidEmail;
import static org.junit.Assert.*;

public class ValidationUtilsUnitTest {

    @Test
    public void inputIsNullOrEmpty() {
        assertFalse(isNotNullOrEmpty(null));
        assertFalse(isNotNullOrEmpty("     \n"));
        assertFalse(isNotNullOrEmpty(""));
        assertFalse(isNotNullOrEmpty(" "));

    }

    @Test
    public void inputIsNotNullOrEmpty() {
        assertTrue(isNotNullOrEmpty("TEST"));
        assertTrue(isNotNullOrEmpty("     \nTEST"));
        assertTrue(isNotNullOrEmpty("  TEST  "));
        assertTrue(isNotNullOrEmpty(" TEST SPACES "));

    }

    @Test
    public void isValidEmail_validEmailInput() {
        assertTrue(isValidEmail("test123@test.com"));
        assertTrue(isValidEmail("test_123@test.com"));
        assertTrue(isValidEmail("test-123@test.com"));
        assertTrue(isValidEmail("test.test@test.com"));
        assertTrue(isValidEmail("test@hotmail.co.uk"));
        assertTrue(isValidEmail("123456789@test.com"));
        assertTrue(isValidEmail("   123456789@test.com   "));

    }

    @Test
    public void isValidEmail_invalidEmailInput() {
        assertFalse(isValidEmail("email"));
        assertFalse(isValidEmail("emailwith @ spaces.com"));
        assertFalse(isValidEmail("helloemail@test"));
        assertFalse(isValidEmail("hello*email@test.com"));
        assertFalse(isValidEmail("hello#email@test.com"));

    }

    @Test
    public void isValidDobFormat_validDob() {
        assertTrue(isValidDobFormat("11/11/2011"));
        assertTrue(isValidDobFormat("01/11/2011"));
        assertTrue(isValidDobFormat("01/11/2000"));

    }

    @Test
    public void isValidDobFormat_invalidDob() {
        assertFalse(isValidDobFormat("11/11/201"));
        assertFalse(isValidDobFormat("a1/22/1111"));
        assertFalse(isValidDobFormat("01/11/200-"));
        assertFalse(isValidDobFormat("01-11-2000"));
        assertFalse(isValidDobFormat("01/11/99"));

    }

    @Test
    public void isValidCity_validInput() {
        assertTrue(isValidCity("provo"));
        assertTrue(isValidCity("salt lake"));
        assertTrue(isValidCity("NEW YORK CITY"));
    }

    @Test
    public void isValidCountryName_() {
        assertTrue(isValidCountryName("United States"));
        assertFalse(isValidCountryName(null));
    }
}