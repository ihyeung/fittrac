package com.utahmsd.cs6018.instyle.util;

import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;

import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertTrue;

public class FitnessProfileUtilsUnitTest {

    @Test
    public void calculateHealthyWeightBmi() {
        double bmi = FitnessProfileUtils.calculateBmi(65, 150);
        assertTrue(FitnessProfileUtils.getBmiClassification(bmi).equals(FitnessProfileUtils.BodyMassIndex.HEALTHY_WEIGHT));
    }

    @Test
    public void calculateUnderWeightBmi() {
        double bmi = FitnessProfileUtils.calculateBmi(65, 105);
        assertTrue(FitnessProfileUtils.getBmiClassification(bmi).equals(FitnessProfileUtils.BodyMassIndex.UNDERWEIGHT));
    }

    @Test
    public void calculateObeseBmi() {
        double bmi = FitnessProfileUtils.calculateBmi(65, 195);
        assertTrue(FitnessProfileUtils.getBmiClassification(bmi).equals(FitnessProfileUtils.BodyMassIndex.OBESE));
    }

    @Test
    public void calculateClinicallyObeseBmi() {
        double bmi = FitnessProfileUtils.calculateBmi(65, 235);
        assertTrue(FitnessProfileUtils.getBmiClassification(bmi).equals(FitnessProfileUtils.BodyMassIndex.CLINICALLY_OBESE));
    }

    @Test
    public void getBmi() {
        assertTrue(FitnessProfileUtils.getBmiClassification(18.49999).equals(FitnessProfileUtils.BodyMassIndex.UNDERWEIGHT));
        assertTrue(FitnessProfileUtils.getBmiClassification(18.5).equals(FitnessProfileUtils.BodyMassIndex.HEALTHY_WEIGHT));
        assertTrue(FitnessProfileUtils.getBmiClassification(24.9).equals(FitnessProfileUtils.BodyMassIndex.HEALTHY_WEIGHT));
        assertTrue(FitnessProfileUtils.getBmiClassification(25).equals(FitnessProfileUtils.BodyMassIndex.OVERWEIGHT));
        assertTrue(FitnessProfileUtils.getBmiClassification(30).equals(FitnessProfileUtils.BodyMassIndex.OBESE));
        assertTrue(FitnessProfileUtils.getBmiClassification(34.9).equals(FitnessProfileUtils.BodyMassIndex.OBESE));
        assertTrue(FitnessProfileUtils.getBmiClassification(35).equals(FitnessProfileUtils.BodyMassIndex.CLINICALLY_OBESE));
        assertTrue(FitnessProfileUtils.getBmiClassification(40).equals(FitnessProfileUtils.BodyMassIndex.CLINICALLY_OBESE));

    }

    @Test
    public void calculateDailyCaloricIntake() {
        FitnessProfile fitnessProfile = new FitnessProfile();
        fitnessProfile.setM_dob("03/15/1993");
        fitnessProfile.setM_heightInches(10);
        fitnessProfile.setM_sex("M");
        fitnessProfile.setM_heightFeet(5);
        fitnessProfile.setM_weightInPounds(200);
        fitnessProfile.setM_lifestyleSelection("SEDENTARY");
        fitnessProfile.setM_lbsPerWeek(0);
        System.out.println(FitnessProfileUtils.calculateDailyCaloricIntake(fitnessProfile));
    }
}