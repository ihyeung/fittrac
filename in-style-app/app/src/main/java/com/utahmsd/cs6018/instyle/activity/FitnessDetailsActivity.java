package com.utahmsd.cs6018.instyle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.FitnessDetailsFragment;

public class FitnessDetailsActivity extends AppCompatActivity {

    private static final String LOG = FitnessDetailsActivity.class.getSimpleName();
    private FragmentTransaction m_fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness_details);

        loadFitnessDetailsFragment();
    }

    private void loadFitnessDetailsFragment() {
        m_fTrans = getSupportFragmentManager().beginTransaction();
        m_fTrans.replace(R.id.fl_master_nd_activity_fitness_details, new FitnessDetailsFragment());
        m_fTrans.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, "onBackPressed");
        if (getResources().getBoolean(R.bool.isWideDisplay)) {
            //Do nothing, fitness details page is default detail fragment in tablet view

        } else {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }
}