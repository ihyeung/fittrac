package com.utahmsd.cs6018.instyle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.ProfileSummaryFragment;
import com.utahmsd.cs6018.instyle.util.Constants;

import static com.utahmsd.cs6018.instyle.util.Constants.BACK_PRESSED;

public class ProfileSummaryActivity extends AppCompatActivity {

    private final String LOG = getClass().getSimpleName();

    private FragmentTransaction m_fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, Constants.CREATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_summary);
        loadProfileSummaryFragment();
    }

    private void loadProfileSummaryFragment() {
        m_fTrans = getSupportFragmentManager().beginTransaction();
        m_fTrans.replace(R.id.fl_activity_profile_summary, new ProfileSummaryFragment(), "v_frag_profile");
        m_fTrans.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, BACK_PRESSED);
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

}