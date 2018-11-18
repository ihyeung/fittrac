package com.utahmsd.cs6018.instyle.util;

import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FitnessProfileUtils {

    private static final String LOG_TAG = FitnessProfileUtils.class.getSimpleName();
    private static final int CALORIES_PER_POUND = 3500;
    private static final double POUND_PER_KG = 2.2;
    private static final double CM_PER_INCH = 2.54;


    public enum BodyMassIndex {
        UNDERWEIGHT,
        HEALTHY_WEIGHT,
        OVERWEIGHT,
        OBESE,
        CLINICALLY_OBESE
    }

    public static double calculateBmi(int heightInInches, int weightInPounds) {
        final double BMI_METRIC_TO_IMPERIAL_SCALE_FACTOR = 703;
        return weightInPounds/Math.pow(heightInInches, 2) * BMI_METRIC_TO_IMPERIAL_SCALE_FACTOR;
    }

    public static BodyMassIndex getBmiClassification(double bmiValue) {
        final double UNDERWEIGHT_THRESHOLD = 18.5;
        final double AVERAGE_THRESHOLD = 25.0;
        final double OVERWEIGHT_THRESHOLD = 30.0;
        final double OBESE_THRESHOLD = 35.0;

        return bmiValue < UNDERWEIGHT_THRESHOLD ? BodyMassIndex.UNDERWEIGHT :
                bmiValue < AVERAGE_THRESHOLD ? BodyMassIndex.HEALTHY_WEIGHT :
                        bmiValue < OVERWEIGHT_THRESHOLD ? BodyMassIndex.OVERWEIGHT :
                                bmiValue < OBESE_THRESHOLD ? BodyMassIndex.OBESE :
                                        BodyMassIndex.CLINICALLY_OBESE;
    }

    public static double calculateBMR(int heightFeet, int heightInches,
                                      String sex, int weightInPounds, int age) {

        int totalHeightInInches = calculateHeightInInches(heightFeet, heightInches);

        if (sex.equalsIgnoreCase("F")){

            return calculateFemaleBmr(weightInPounds, totalHeightInInches, age);
        }

        return calculateMaleBmr(weightInPounds, totalHeightInInches, age);
    }

    private static double calculateFemaleBmr(int weightLbs, int heightInches, int age) {
        return 655 + (4.35 * weightLbs) + (4.7 * heightInches) - (4.7 * age);
    }

    private static double calculateMaleBmr(int weightLbs, int heightInches, int age) {
        return 66 + (6.23 * weightLbs) + (12.7 * heightInches) - (6.8 * age);
    }

    public static double calculateDailyCaloricIntake(FitnessProfile fp) {

        final double BMR_FACTOR_SEDENTRY = 1.2;
        final double BMR_FACTOR_MODERATE = 1.55;
        final double BMR_FACTOR_ACTIVE = 1.725;

        final double CALORIC_DIFFERENCE_DAILY = fp.getM_lbsPerWeek() * CALORIES_PER_POUND / 7;

        double calculatedBmr = calculateBMR(fp.getM_heightFeet(),
                                            fp.getM_heightInches(),
                                            fp.getM_sex(),
                                            fp.getM_weightInPounds(),
                                            calculateAge(fp.getM_dob()));

        double baselineCalories = fp.getM_lifestyleSelection().equalsIgnoreCase("SEDENTARY") ?
                        calculatedBmr * BMR_FACTOR_SEDENTRY :
                        fp.getM_lifestyleSelection().equalsIgnoreCase("MODERATE") ?
                                calculatedBmr * BMR_FACTOR_MODERATE :
                        calculatedBmr * BMR_FACTOR_ACTIVE;

        double total = baselineCalories + CALORIC_DIFFERENCE_DAILY;

        return total < 0 ? 0 : total;

    }


    //http://www.bmrcalculator.org
    //You exercise moderately (3-5 days per week)	Calories Burned a Day = BMR x 1.55
    //Low	You get little to no exercise	Calories Burned a Day = BMR x 1.2
    //In order to lose 1 pound of fat each week, you must have a deficit of 3,500 calories over the course of a week.[5]
    // https://www.wikihow.com/Calculate-How-Many-Calories-You-Need-to-Eat-to-Lose-Weight
    public static double calculateCalories(FitnessProfile profile) {
        double numOfCalories;
        double BMR = 1500;

        if (profile.getM_lifestyleSelection().equalsIgnoreCase("Sedentary")) {
            numOfCalories = BMR * 1.2;

            if(profile.getM_weightGoal().equalsIgnoreCase("Gain")) {
                numOfCalories += profile.getM_lbsPerWeek() + 3500;
            } else if(profile.getM_weightGoal().equalsIgnoreCase("Lose")){
                numOfCalories += profile.getM_lbsPerWeek() - 3500;
            }

        }
        else {
            //active
            numOfCalories = BMR * 1.55;

            if(profile.getM_weightGoal().equalsIgnoreCase("Gain")) {
                numOfCalories += BMR + profile.getM_lbsPerWeek() + 3500;
            } else if(profile.getM_weightGoal().equalsIgnoreCase("Lose")){
                numOfCalories += BMR + profile.getM_lbsPerWeek() - 3500;
            }
        }

        return numOfCalories;
    }

    public static int calculateHeightInInches(int feet, int inches) {
        return feet * 12 + inches;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(String m_dob) {
        DateTimeFormatter dob_format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        dob_format = dob_format.withLocale(Locale.US);

        LocalDate dob = LocalDate.parse(m_dob, dob_format);
        LocalDate today = LocalDate.now();

        return Period.between(dob, today).getYears();
    }
}
