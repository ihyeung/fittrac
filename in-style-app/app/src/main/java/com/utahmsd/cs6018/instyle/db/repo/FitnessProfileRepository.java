package com.utahmsd.cs6018.instyle.db.repo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import com.utahmsd.cs6018.instyle.db.InStyleDatabase;
import com.utahmsd.cs6018.instyle.db.entity.FitnessProfile;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FitnessProfileRepository {
    private LiveData<FitnessProfile> fitnessProfileData;
    private InStyleDatabase m_db;
    private Executor m_executor = Executors.newSingleThreadExecutor();

    //The following three items make this a singleton
    //make the only call that applies to all accesses of the repository.
    private static FitnessProfileRepository FPR_instance;
    //the only way to access the instance.
    public static FitnessProfileRepository getInstance(Context context) {
        if (FPR_instance == null){
             FPR_instance = new FitnessProfileRepository(context);
        }
        return FPR_instance;}
    //made private to ensure that nothing can create an instance besides itself
    private FitnessProfileRepository(Context context) {
//      m_fitnessProfile = //TODO: Get data from database.
        //TODO: This is just a sample data until the database is established.
        m_db = InStyleDatabase.getDatabaseInstance(context);
//        fitnessProfileData = getFitnessProfileData();
    }

    //This is where the Repository gets the data.
    public LiveData<FitnessProfile> getFitnessProfileData(int userID){
        //get from database when ready. Number value currently hard coded.
       return m_db.fitnessProfileDao().findByuserID(userID);
    }

    public void updateFitnessProfile(FitnessProfile fitnessProfile) {
        //This is only used to updated the fitness profile data for the sample data
        //This is not correctly using the pub sub model of the observer and live data
        m_executor.execute(
                () -> m_db.fitnessProfileDao().updateExistingFitnessProfileData(fitnessProfile)
        );
    }

    @SuppressLint("StaticFieldLeak")
    public void insertNewFitnessProfile(FitnessProfile fitnessProfile) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                m_db.fitnessProfileDao().insertNewFitnessProfile(fitnessProfile);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }.execute();

//        m_executor.execute(
//                () -> m_db.fitnessProfileDao().insertNewFitnessProfile(fitnessProfile)
//        );
    }
}
