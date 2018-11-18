package com.utahmsd.cs6018.instyle.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.LoginFragment;
import com.utahmsd.cs6018.instyle.ui.RV_Adapter;

import static com.utahmsd.cs6018.instyle.util.Constants.CREATE;

public class LoginActivity extends AppCompatActivity
        implements RV_Adapter.OnAdapterDataChannel {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    private FragmentTransaction m_fTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getResources().getBoolean(R.bool.isWideDisplay)){
            m_fTrans = getSupportFragmentManager().beginTransaction();
            m_fTrans.replace(R.id.fl_detail_wd, new LoginFragment(), "v_frag_dashboard");
            m_fTrans.commit();
        } else {
            m_fTrans = getSupportFragmentManager().beginTransaction();
            m_fTrans.replace(R.id.fl_login_nd, new LoginFragment(), "v_frag_dashboard");
            m_fTrans.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed");
        //Do nothing when back button is pressed on the Login screen

    }

    @Override
    public void onAdapterDataPass(int position) {

    }
}
