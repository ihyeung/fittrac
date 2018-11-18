package com.utahmsd.cs6018.instyle.util;

public class ValidationUtils {

    public static boolean isValidName(String name) {
        return isValidAlphaCharsWithSpaces(name);
    }

    public static boolean isValidCity(String city) {
        return isValidAlphaCharsWithSpaces(city);
    }

    public static boolean isValidCountryName(String countryName) {
        return ValidationUtils.isNotNullOrEmpty(countryName) && isValidAlphaCharsWithSpaces(countryName);
    }

    public static boolean isValidEmail(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    public static boolean isValidAlphaChars(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("[A-Za-z]+");
    }


    public static boolean isValidDobFormat(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}");
    }

    /**
     * Validates that weight entered by user is an integer value greater than 0.
     * @param input
     * @return boolean
     */
    public static boolean isValidWeight(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("[0-9]{1,3}")
                && Integer.parseInt(input.trim()) > 0;
    }

    /**
     * Validates that height measurements entered by user are valid feet/inches values.
     * Returns true if the user enters a feet value between 4 and 7 feet, and an inches value
     * between 0 and 12. If the inches field is left blank, this function returns true as long
     * as the feet measurement is valid.
     * @param feet, inches
     * @return boolean
     */
    public static boolean isValidHeight(String feet, String inches) {
        if (!isNotNullOrEmpty(inches)) {
            return isNotNullOrEmpty(feet) &&
                    feet.trim().matches("[0-9]{1,2}") &&
                    inches.trim().matches("[0-9]{0,2}") &&
                    Integer.parseInt(feet.trim()) > 4 &&
                    Integer.parseInt(feet.trim()) <= 7;
        }
        return isNotNullOrEmpty(feet) &&
                feet.trim().matches("[0-9]{1,2}") &&
                inches.trim().matches("[0-9]{0,2}") &&
                Integer.parseInt(feet.trim()) > 4 &&
                Integer.parseInt(feet.trim()) <= 7 &&
                Integer.parseInt(inches.trim()) >= 0 &&
                Integer.parseInt(inches.trim()) <= 12;
    }

    public static boolean isValidAlphaCharsWithSpaces(String input) {
        return isNotNullOrEmpty(input) && input.trim().matches("[a-zA-Z\\s]+");
    }

    public static boolean isValidAlphaNumericChars(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("[A-Za-z0-9]+");
    }

    public static boolean isValidNumericChars(String input) {
        return isNotNullOrEmpty(input) &&
                input.trim().matches("[0-9]+");
    }

    public static boolean isNotNullOrEmpty(String input) {
        return input != null && !input.isEmpty() && !input.matches("^\\s+$");
    }

    public static boolean isValidSex(String input) {
        return input.equalsIgnoreCase("M") ||
                input.equalsIgnoreCase("F");
    }

    public static boolean isValidWeightPlan(String lbsPerWeek){
        return isValidNumericChars(lbsPerWeek) &&
                Integer.parseInt(lbsPerWeek) >= -5 &&
                Integer.parseInt(lbsPerWeek) <= 5;
    }

    public static boolean isValidEmailAndPassword(String email, String password) {
        return isValidEmail(email) && isNotNullOrEmpty(password);
    }
}
