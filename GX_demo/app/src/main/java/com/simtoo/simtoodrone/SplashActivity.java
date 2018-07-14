package com.simtoo.simtoodrone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zt.mylibrary.connect.MAVLink.MavLinkStreamRates;
import com.zt.mylibrary.connect.MAVLink.enums.MAV_DATA_STREAM;
import com.zt.mylibrary.connect.STDroidPlannerApp;
import com.zt.mylibrary.connect.STDrone;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {
    private Button            connect;
    private STDrone           mDrone;
    private STDroidPlannerApp mApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        connect = (Button) findViewById(R.id.connect);
        connect.setOnClickListener(this);
        mApp = (STDroidPlannerApp) getApplication();
        mDrone = mApp.getSTDrone();
        mDrone = STDroidPlannerApp.getInstance().getSTDrone();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect:

                new AlertDialog(SplashActivity.this).builder().setTitle("提示")
                        .setMsg(R.string.logos)
                        .setPositiveButton(R.string.confirm_exit, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Constants.MOMO=Constants.PHONE;
                                mDrone.getConnection().connect();
                                //请求数据
                                MavLinkStreamRates.requestMavlinkDataStream(mDrone, (byte) 1, (byte) 1, MAV_DATA_STREAM
                                        .MAV_DATA_STREAM_ALL, 5);
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);


                            }
                        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Constants.MOMO=Constants.ROCKER;
                        mDrone.getConnection().connect();
                        //请求数据
                        MavLinkStreamRates.requestMavlinkDataStream(mDrone, (byte) 1, (byte) 1, MAV_DATA_STREAM
                                .MAV_DATA_STREAM_ALL, 50);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                }).show();








                break;
    }
}
}
