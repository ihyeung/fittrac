package com.example.mcresswell.project01.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.mcresswell.project01.R;
import com.example.mcresswell.project01.fragments.CreateAccountFragment;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String LOG = CreateAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        if(isWideDisplay()){
//            //present fragment to display
////            m_fTrans = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fl_create_account_nd, new CreateAccountFragment(), "v_frag_dashboard");
//            fragmentTransaction.commit();
//        } else {
            //present fragment to display
            fragmentTransaction.replace(R.id.fl_create_account_nd, new CreateAccountFragment(), "v_frag_dashboard");
            fragmentTransaction.commit();
//        }
    }

    private boolean isWideDisplay(){
        return getResources().getBoolean(R.bool.isWideDisplay);
    }

    @Override
    public void onBackPressed() {
        Log.d(LOG, "onBackPressed");

        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);
    }

}