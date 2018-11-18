package com.utahmsd.cs6018.instyle.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import com.utahmsd.cs6018.instyle.db.repo.FitnessProfileRepository;

public class FitnessProfileViewModel extends AndroidViewModel {

    private LiveData<FitnessProfile> m_fitnessProfile;
    private FitnessProfileRepository m_fitnessProfileRepository;

    public FitnessProfileViewModel(@NonNull Application application) {
        super(application);
        m_fitnessProfileRepository = FitnessProfileRepository.getInstance(application.getApplicationContext());

    }

    public LiveData<FitnessProfile> getFitnessProfile(int userID) {
        m_fitnessProfile = m_fitnessProfileRepository.getFitnessProfileData(userID);
        return m_fitnessProfile;
    }

    public void updateFitnessProfile(FitnessProfile fitnessProfile){
        m_fitnessProfileRepository.updateFitnessProfile(fitnessProfile);
    }

    public void updateFitnessProfileDailyStepCount(float numSteps) {
        //TODO: Fix fitness profile view model database calls for updating step count data
//        m_fitnessProfile.getValue().setM_stepCount(m_numberOfSteps);
//        m_fitnessProfile.getValue().setM_dateLastUpdated(new Date(2018, 10, 31));
        insertNewFitnessProfile(m_fitnessProfile.getValue());
    }

    public void insertNewFitnessProfile(FitnessProfile fitnessProfile) {
        m_fitnessProfileRepository.insertNewFitnessProfile(fitnessProfile);
    }

//    public LiveData<FitnessProfile> getFitnessProfile() {
//        return m_fitnessProfile;
//    }

}
