package com.utahmsd.cs6018.instyle.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.ProfileEntryFragment;
import com.utahmsd.cs6018.instyle.util.Constants;

public class ProfileEntryActivity extends AppCompatActivity {

    private final String LOG = getClass().getSimpleName();

    private FragmentTransaction m_fTrans;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, Constants.CREATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_entry);

        loadProfileEntryFragment();
    }

    private void loadProfileEntryFragment() {
        m_fTrans = getSupportFragmentManager().beginTransaction();
        m_fTrans.replace(R.id.fl_activity_profile_entry, new ProfileEntryFragment());
        m_fTrans.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, "onBackPressed");
        if (getResources().getBoolean(R.bool.isWideDisplay)) {
            loadProfileEntryFragment();
        } else {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        }
    }
}
