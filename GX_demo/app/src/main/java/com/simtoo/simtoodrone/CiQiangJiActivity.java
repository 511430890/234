package com.simtoo.simtoodrone;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

public class CiQiangJiActivity extends AppCompatActivity {
    private FrameLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ci_qiang_ji);
        //        content= (FrameLayout) findViewById(R.id.content);
        SensorSetupFragment girlFragment = new SensorSetupFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content,girlFragment);
        fragmentTransaction.commit();
    }
}
