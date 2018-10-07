package com.example.mcresswell.project01;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mcresswell.project01.db.entity.UserProfile;
import com.example.mcresswell.project01.fragments.ProfileEntryFragment;
import com.example.mcresswell.project01.fragments.ProfileSummaryFragment;
import com.example.mcresswell.project01.util.Constants;

public class ProfileSummaryActivity extends AppCompatActivity
    implements ProfileSummaryFragment.OnProfileSummaryInteractionListener,
        ProfileEntryFragment.OnProfileEntryFragmentListener {

    private final String LOG = getClass().getSimpleName();

    private FragmentTransaction m_fTrans;
    private UserProfile m_userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG, Constants.CREATE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_summary);

        m_fTrans = getSupportFragmentManager().beginTransaction();

        if (savedInstanceState != null) {
            m_userProfile = getIntent().getParcelableExtra("profile");
//            Bitmap photo = getIntent().getParcelableExtra("M_IMG_DATA");
        }
        m_fTrans.replace(R.id.fl_activity_profile_details, ProfileSummaryFragment.newInstance(m_userProfile), "v_frag_profile");
        m_fTrans.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onProfileSummaryEditButton(UserProfile profile) {
        Log.d(LOG, "onProfileSummaryEditButton listener");
        Intent intent = new Intent(this, ProfileEntryActivity.class);

        if (profile != null){ //Existing profile data, transfer data
            intent.putExtra("profile", profile);
        }
        startActivity(intent);

    }

    @Override
    public void onProfileEntryDataEntered_DoneButtonOnClick(UserProfile profile) {
        Log.d(LOG, "onProfileEntryDoneButton listener, user finished adding data");
        m_fTrans = getSupportFragmentManager().beginTransaction();

        m_fTrans.replace(R.id.fl_activity_profile_details, ProfileSummaryFragment.newInstance(profile));
        m_fTrans.commit();
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, "onBackPressed");
        // code here to show dialog
        Intent intent = new Intent(this, DashboardActivity.class);

        if (m_userProfile != null){ //Existing profile data, transfer data
            intent.putExtra("profile", m_userProfile);
        }
        startActivity(intent);
    }

}