package com.utahmsd.cs6018.instyle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.utahmsd.cs6018.instyle.R;
import com.utahmsd.cs6018.instyle.fragment.CreateAccountFragment;
import com.utahmsd.cs6018.instyle.ui.RV_Adapter;

import static com.utahmsd.cs6018.instyle.util.Constants.BACK_PRESSED;
import static com.utahmsd.cs6018.instyle.util.Constants.CREATE;

public class CreateAccountActivity extends AppCompatActivity
    implements RV_Adapter.OnAdapterDataChannel {

    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, CREATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(getResources().getBoolean(R.bool.isWideDisplay)){
            fragmentTransaction.replace(R.id.fl_detail_wd, new CreateAccountFragment());
            fragmentTransaction.commit();
        } else {
            fragmentTransaction.replace(R.id.fl_create_account_nd, new CreateAccountFragment());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, BACK_PRESSED);

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onAdapterDataPass(int position) {

    }
}
