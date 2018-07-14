package com.simtoo.simtoodrone;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zt.mylibrary.connect.MAVLink.MavLinkArm;
import com.zt.mylibrary.connect.MAVLink.MavLinkCalibration;
import com.zt.mylibrary.connect.MAVLink.MavLinkModes;
import com.zt.mylibrary.connect.MAVLink.MavLinkRC;
import com.zt.mylibrary.connect.MAVLink.MavLinkStreamRates;
import com.zt.mylibrary.connect.MAVLink.Messages.ApmModes;
import com.zt.mylibrary.connect.MAVLink.enums.MAV_DATA_STREAM;
import com.zt.mylibrary.connect.STDroidPlannerApp;
import com.zt.mylibrary.connect.STDrone;
import com.zt.mylibrary.connect.SimToo_MAVLink.Coord2D;
import com.zt.mylibrary.connect.SuperUI;
import com.zt.mylibrary.connect.flycontrol.DecimalFormatUtil;

import java.text.DecimalFormat;
import java.util.Timer;

/**
 * //注意事项，飞机飞行中，不允许让客户点击罗盘和加速计校准按键。
 * //校准信息     罗盘校准操作，到时候，给你们发个视频。
 * 飞机失联，是根据获取飞机数据来判断，假如，飞机6秒没有拿到数据，（电池，高度）判断为飞机失联了。需要调用重新连接方法和请求数据
 * mDrone.getConnection().connect();
 * //请求数据
 * MavLinkStreamRates.requestMavlinkDataStream(mDrone, (byte) 1, (byte) 1, MAV_DATA_STREAM
 * .MAV_DATA_STREAM_ALL, 3);
 */
public class MainActivity extends SuperUI implements View.OnClickListener,
        View.OnTouchListener {


    private static final int MSG_CHANGE_MODE_GUANGLIU_LOITER = 0;
    private static final int MSG_UPDATE_WIFI                 = 1;

    private boolean isStarted = false;

    private        View              mControlLayout;
    private        Button            takeoff;
    private        Button            land;
    private        Button            imgbtn_up;//上升
    private        Button            imgbtn_down;//下降
    private        Button            imgbtn_left;//左转向
    private        Button            imgbtn_right;//右转向
    private        Button            imgbtn_ups;//前进
    private        Button            btn_flip;//翻滚
    private        Button            btn_qianflip;//前翻滚
    private        Button            btn_guangliu;//前翻滚
    private        Button            imgbtn_downs;//后退
    private        Button            imgbtn_lefts;//左偏移
    private        Button            imgbtn_rights;//右偏移
    //云台调节
    private        Button            mib_adjust_up;
    private        Button            mib_adjust_down;
    private        TextView          tv_gpsNumber;
    private        TextView          mTv_altitude;
    private        TextView          mTv_distance;
    //2.13
    private        STDrone           mDrone;
    private        STDroidPlannerApp mApp;
    private        float             alt;
    private static TextView          tv_battery;
    private        TextView          tv_mode;

    private int[]   rcOutputs = new int[8];
    private boolean isRocker  = true;


    private Coord2D mHome;
    private Coord2D mNow;//信号强度值

    //电池
    //电池
    int batt_fulls = 0;
    int batt_index = 0;
    private static int[]   batts  = new int[5];
    private        boolean isBatt = true;

    private boolean isUpTouch     = false;
    private boolean isDownTouch   = false;
    private boolean isLeftTouch   = false;
    private boolean isRightTouch  = false;
    private boolean isUpsTouch    = false;
    private boolean isDownsTouch  = false;
    private boolean isLeftsTouch  = false;
    private boolean isRightsTouch = false;
    private boolean upYun         = false;
    private boolean downYun       = false;

    public boolean isOnLongClick   = false;
    public boolean isOnLongClickss = false;

    int sendTemo = 1000;
    private Button        btn_compass;//罗盘校准
    private Button        btn_accelerometer;//加速计校准
    private PlusYunThread plusYunThread;
    private MiusYunThread miusYunThread;

    private boolean running = true;

    private final IntentFilter superIntentFilter = new IntentFilter();

    private Button   mFollowBtn;
    private TextView mFollowValueTV;
    private Timer    mFollowTimer;

    private DecimalFormat mDecimalFormat = new DecimalFormat("0.000");
    private static Handler mHandler;
    private boolean  isTakeOff = false;
    private Runnable runnable  = new Runnable() {
        @Override
        public void run() {
            try {

                if (Constants.MOMO == Constants.PHONE) {
                    if (isRocker) {
                        mHandler.postDelayed(this, 20);
                        if (alt >= 13 && rcOutputs[2] > 1500) {
                            rcOutputs[2] = 1500;
                        }
                        MavLinkRC.sendRcOverrideMsg(mDrone, rcOutputs);
                        Log.d("油门", "=========");
                    }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    final static Handler mHandlers = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv_battery.setText(String.valueOf(100) + "%");//95
                    break;
                case 2:
                    tv_battery.setText(String.valueOf(0) + "%");//95
                    break;
                case 3:
                    tv_battery.setText(String.valueOf(battery) + "%");

                    break;
            }

            super.handleMessage(msg);
        }
    };
    private static int battery;
    private double batt_voltnew = 0.0;
    private double batt_volt;
    private boolean ISFOLLOW = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDroneControl();
    }

    private void initView() {
        Constants.CURRENTROCK = Constants.ROCKCLOSE;

        mControlLayout = findViewById(R.id.control_layout);
        imgbtn_up = (Button) findViewById(R.id.Up);
        imgbtn_down = (Button) findViewById(R.id.down);
        imgbtn_left = (Button) findViewById(R.id.left);
        imgbtn_right = (Button) findViewById(R.id.right);
        imgbtn_ups = (Button) findViewById(R.id.ups);
        imgbtn_downs = (Button) findViewById(R.id.downs);
        imgbtn_lefts = (Button) findViewById(R.id.lefts);
        imgbtn_rights = (Button) findViewById(R.id.rights);
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        mib_adjust_up = (Button) findViewById(R.id.ib_adjust_up);
        mib_adjust_down = (Button) findViewById(R.id.ib_adjust_down);
        land = (Button) findViewById(R.id.land);
        tv_gpsNumber = (TextView) findViewById(R.id.tv_gpsNumber);
        mTv_altitude = (TextView) findViewById(R.id.tv_altitude);//高度
        mTv_distance = (TextView) findViewById(R.id.tv_distance);//距离
        tv_mode = (TextView) findViewById(R.id.tv_mode);
        takeoff = (Button) findViewById(R.id.takeoff);
        btn_accelerometer = (Button) findViewById(R.id.btn_accelerometer);
        btn_compass = (Button) findViewById(R.id.btn_compass);
        btn_flip = (Button) findViewById(R.id.btn_flip);
        btn_qianflip = (Button) findViewById(R.id.btn_qianflip);
        btn_guangliu = (Button) findViewById(R.id.btn_guangliu);


        land.setOnClickListener(this);
        imgbtn_up.setOnTouchListener(this);
        imgbtn_down.setOnTouchListener(this);
        imgbtn_left.setOnTouchListener(this);
        imgbtn_right.setOnTouchListener(this);
        imgbtn_ups.setOnTouchListener(this);
        imgbtn_downs.setOnTouchListener(this);
        imgbtn_lefts.setOnTouchListener(this);
        imgbtn_rights.setOnTouchListener(this);
        mib_adjust_up.setOnTouchListener(this);
        mib_adjust_down.setOnTouchListener(this);
        takeoff.setOnClickListener(this);
        btn_compass.setOnClickListener(this);
        btn_accelerometer.setOnClickListener(this);
        btn_flip.setOnClickListener(this);
        btn_qianflip.setOnClickListener(this);
        btn_guangliu.setOnClickListener(this);

    }


    private void initDroneControl() {
        mApp = (STDroidPlannerApp) getApplication();
        mDrone = mApp.getSTDrone();
        mDrone.getConnection().connect();//连接
        MavLinkStreamRates.requestMavlinkDataStream(mDrone, (byte) 1, (byte) 1, MAV_DATA_STREAM
                .MAV_DATA_STREAM_ALL, 50);

        initRcOutputs();
        mHandler = STDroidPlannerApp.getInstance().getMainHandler();
        startRcOutputs();


        /*加速计校准广播*/
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_IMU_ERROR);
        superIntentFilter.addAction(Constans.AttributeEvent.STATE_CONNECTED);
        superIntentFilter.addAction(Constans.AttributeEvent.STATE_DISCONNECTED);
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_MAG_STARTED);
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_MAG_ESTIMATION);
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_MAG_COMPLETED);
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_IMU);
        superIntentFilter.addAction(Constans.AttributeEvent.HEARTBEAT_TIMEOUT);
        superIntentFilter.addAction(Constans.AttributeEvent.CALIBRATION_IMU_TIMEOUT);
                /*加速计校准广播*/


        //////////////////////////////////////////////////////////////////////
        superIntentFilter.addAction(Constans.AttributeEvent.GPS_COUNT);//DroneEventsType.GPS_COUNT
        superIntentFilter.addAction(Constans.AttributeEvent.GPS_POSITION);
        superIntentFilter.addAction(Constans.AttributeEvent.GPS_FIX);//DroneEventsType.GPS_FIX

        superIntentFilter.addAction(Constans.AttributeEvent.STATE_CONNECTED);
        superIntentFilter.addAction(Constans.AttributeEvent.STATE_DISCONNECTED);

        //HOME
        superIntentFilter.addAction(Constans.AttributeEvent.HOME_UPDATED);
        //电池
        superIntentFilter.addAction(Constans.AttributeEvent.BATTERY_UPDATED);
        superIntentFilter.addAction(Constans.AttributeEvent.SIGNAL_UPDATED);

        //GPS

        superIntentFilter.addAction(Constans.AttributeEvent.STATE_UPDATED);

        //FlightModes
        superIntentFilter.addAction(Constans.AttributeEvent.STATE_VEHICLE_MODE);//DroneEventsType.MODE
        //        eventFilter.addAction(Constans.AttributeEvent.TYPE_UPDATED);//?

        superIntentFilter.addAction(Constans.AttributeEvent.SPEED_UPDATED);//点了连接 未确定是否已收到数据
        superIntentFilter.addAction(Constans.AttributeEvent.STATE_ARMING);//getState().setArmed

        STDroidPlannerApp.getInstance().registerLocalReceiver(superReceiver, superIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStarted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrone.getConnection().disconnect();
        STDroidPlannerApp.getInstance().unregisterLocalReceiver(superReceiver);
        isRocker = false;
        stopRcOutputs();//是否可以去掉这个
    }


    private void upHodp() {
        mode = mDrone.getState().getMode().getName();
        if (mode.contains("Stabilize")) {
            tv_mode.setText("自稳模式");
        } else if (mode.contains("Alt Hold")) {
            tv_mode.setText("定高模式");
        } else if (mode.contains("Loiter")) {
            tv_mode.setText("悬停模式");
        } else if (mode.contains("RTL")) {
            tv_mode.setText("返航模式");
        } else if (mode.contains("Land")) {
            tv_mode.setText("降落模式");
        } else if (mode.contains("PosHold")) {
            tv_mode.setText("GPS");
        } else if (mode.contains("Guang")) {
            tv_mode.setText("光流模式");
        } else if (mode.contains("Guided")) {
            tv_mode.setText("引导模式");
        } else if (mode.contains("Flip")) {
            tv_mode.setText("翻滚模式");
        }

        //        tvhodp.setText("飞机模式=" + mDrone.getState().getMode().getName());
        //        tvhodps.setText("Hopd=" + mDrone.getGps().getGpsEPH());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_flip:
                Log.d("翻滚", "===2222==");
                //                rcOutputs[6] = 1900;
                //                new Handler().postDelayed(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        rcOutputs[6] = 1000;
                //                    }
                //                }, 500);
                MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_FLIP);
                Log.d("翻滚", "===11==");
                break;

            case R.id.btn_guangliu://跟随功能

                if (mDrone.getGps().getGpsEPH() <= 2 && mDrone.getGps().getGpsEPH() > 0) {
                    if (ISFOLLOW) {
                        ISFOLLOW = false;
                        MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LOITER);
                        mDrone.getFollow().toggleFollowMeState(this);
                    } else {
                        ISFOLLOW = true;
                        if (mDrone.getGps().getGpsEPH() <= 2 && mDrone.getGps().getGpsEPH() > 0) {
                            MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LOITER);
                        } else {
                            MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "当前卫星精度不够", Toast.LENGTH_LONG).show();
                }


                //                MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
                break;
            case R.id.btn_qianflip:
                MavLinkCalibration.sendStartAccelerometerMessages(mDrone);
                break;
            case R.id.land:
                new AlertDialog.Builder(MainActivity.this).setTitle("SimTooXT200")
                        .setMessage("确定要降落吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //                                takeoff.setVisibility(View.VISIBLE);
                                //                                mControlLayout.setVisibility(View.GONE);
                                isTakeOff = false;
                                MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LAND);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;
            case R.id.takeoff:
                initRcOutputstakeoff();
                MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_STABILIZE);
                new AlertDialog.Builder(MainActivity.this).setTitle("SimTooXT200")
                        .setMessage("确定要起飞吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MavLinkArm.sendArmMessage(mDrone, true);
                                MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        rcOutputs[2] = 1200;
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                rcOutputs[2] = 1500;
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        rcOutputs[2] = 1500;
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                rcOutputs[2] = 1670;
                                                                MavLinkModes.changeFlightMode(mDrone, ApmModes
                                                                        .GUANGLIU_LOITER);
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        rcOutputs[2] = 1500;
                                                                        MavLinkModes.changeFlightMode(mDrone,
                                                                                ApmModes.GUANGLIU_LOITER);
//                                                                        new Handler().postDelayed(new Runnable() {
//                                                                            @Override
//                                                                            public void run() {
//                                                                                rcOutputs[2] = 1750;
//                                                                                MavLinkModes.changeFlightMode(mDrone,
//                                                                                        ApmModes.GUANGLIU_LOITER);
//                                                                                new Handler().postDelayed(new Runnable() {
//                                                                                    @Override
//                                                                                    public void run() {
//                                                                                        rcOutputs[2] = 1500;
//                                                                                        MavLinkModes.changeFlightMode
//                                                                                                (mDrone, ApmModes
//                                                                                                        .GUANGLIU_LOITER);
//                                                                                    }
//                                                                                }, 400);
//                                                                            }
//                                                                        }, 200);
                                                                    }
                                                                }, 1700);
                                                            }
                                                        }, 500);
                                                    }
                                                }, 300);
                                            }
                                        }, 200);
                                    }
                                }, 200);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                break;
            case R.id.btn_compass://罗盘校准按键
                //                rcOutputs[0] = 1750;
                //                new Handler().postDelayed(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        rcOutputs[0] = 1500;
                //                    }
                //                }, 1500);
                startActivity(new Intent(this, CiQiangJiActivity.class));
                //                                    MavLinkCalibration.sendStartCalibrationMessage(mDrone);
                //                                    Toast.makeText(this,"水平顺时针旋转进行校准...",Toast.LENGTH_LONG).show();

                break;
            //            case R.id.btn_accelerometer://加速计校准按键
            //                    MavLinkCalibration.sendStartAccelerometerMessages(mDrone);
            //                    Toast.makeText(this,"水平放置时光机，正在校准中...",Toast.LENGTH_LONG).show();
            //                break;
        }
    }


    private boolean isAscensionTakeOff = true;
    private String mode;

    private void AscensionTakeOff() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isAscensionTakeOff) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //                            Log.d("起飞","111111122"+alt);
                    if (alt < 0.2 || alt > 2) {
                        //                Log.d("起飞","1111111");
                        //                Toast.makeText(MainActivity.this,"高度值异常",Toast.LENGTH_LONG).show();
                        rcOutputs[2] = 1500;
                        MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
                        isAscensionTakeOff = false;
                        return;
                    }

                    if (rcOutputs[2] == 1690) {
                        if (alt == 0.3) {
                            //                Log.d("起飞","22222");
                            //                    Toast.makeText(MainActivity.this,"高度值异常0.35",Toast.LENGTH_LONG)
                            // .show();
                            rcOutputs[2] = 1500;
                            MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
                            isAscensionTakeOff = false;
                            return;
                        }

                    }
                    //                    if(mode.equals("Land")|| rcOutputs[2]<1500){
                    //                        //                Log.d("起飞","3333");
                    //                        if(rcOutputs[2]>1500){
                    //                            rcOutputs[2]=1500;
                    //                            MavLinkModes.changeFlightMode(mDrone,ApmModes.GUANGLIU_LOITER);
                    //                        }
                    //                        isAscensionTakeOff=false;
                    //                        return;
                    //                    }
                    if (alt >= 1.2) {
                        Log.d("起飞", "44444");
                        rcOutputs[2] = 1500;
                        isAscensionTakeOff = false;
                        MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rcOutputs[2] = 1630;
                                Log.d("起飞", "5555");
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("起飞", "66666");
                                        rcOutputs[2] = 1500;
                                        //                                Constants.LINA =Constants.TTAA;
                                    }
                                }, 200);
                            }
                        }, 1000);


                    } else {
                        Log.d("起飞", "888=rcOutputs[2]=1600===");
                        rcOutputs[2] = 1600;
                        //                isAscensionTakeOff=true;
                    }

                }


            }
        }).start();
    }


    public void initRcOutputstakeoff() {
        Log.d("initRcOutputs", "initRcOutputs===");
        for (int i = 0; i < rcOutputs.length; i++) {
            if (i == 6) {
                rcOutputs[i] = 1000;
            } else if (i == 2) {
                rcOutputs[i] = 1100;
            } else {
                rcOutputs[i] = 1500;
            }

        }

    }


    public void initRcOutputs() {
        for (int i = 0; i < rcOutputs.length; i++) {
            if (i == 6) {
                rcOutputs[i] = 1000;
            } else {
                rcOutputs[i] = 1500;
            }
        }
    }

    public void startRcOutputs() {
        mHandler.postDelayed(runnable, 20);
        isRocker = true;
    }

    public void stopRcOutputs() {
        mHandler.removeCallbacks(runnable);
    }

    /**
     * 连接成功
     */
    @Override
    protected void a() {
        Toast.makeText(this, "飞控连接成功", Toast.LENGTH_SHORT).show();
        //点了连接
        initRcOutputs();
        startRcOutputs();
        isRocker = true;
    }

    @Override
    protected void a(String warning) {//飞控警告信息
        //        if (warning.contains("Alt disparity")) {
        //            MavLinkArm.sendArmMessage(mDrone, false);//上锁指令
        //            if (isTakeOff) {
        //                takeOff(true);
        //            }
        //        }
        //        if (warning.contains("Bad Velocity") || warning.contains("Arm: Leaning")) {
        //            if (isTakeOff) {
        //                //加速计校准
        //                MavLinkCalibration.sendStartCalibrationMessages(mDrone);
        //            }
        //        }
    }

    @Override
    protected void b() {//飞机退出，停止油门
        //        invalidateOptionsMenu();
        stopRcOutputs();

    }


    //    private Handler mMainHandler = new Handler() {
    //
    //        @Override
    //        public void handleMessage(Message msg) {
    //            switch (msg.what) {
    //                case MSG_CHANGE_MODE_GUANGLIU_LOITER:
    //                    MavLinkModes.changeFlightMode(mDrone, ApmModes.GUANGLIU_LOITER);
    //                    break;
    //                case MSG_UPDATE_WIFI:
    //                    tvWifi.setText("WIFI信号强度：" + msg.arg1);
    //                    break;
    //            }
    //        }
    //    };

    private final BroadcastReceiver superReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Constans.AttributeEvent.GPS_COUNT.equals(action)) {

                updateGPSView();

            } else if (Constans.AttributeEvent.SPEED_UPDATED.equals(action)) {

                updateSpeedMsg();

            } else if (Constans.AttributeEvent.GPS_POSITION.equals(action)) {

                mNow = mDrone.getGps().getPosition();
                if (mDrone.getGps().getGpsEPH() > 0 && mDrone.getGps().getGpsEPH() <= 1.9) {
                    if (mDrone.getState().isArmed()) {
                        mHome = mDrone.getGps().getPosition();
                    } else {
                        mHome = null;
                    }
                    updateDistanceMsgSimToo();
                }

            } else if (Constans.AttributeEvent.BATTERY_UPDATED.equals(action) || Constans.AttributeEvent
                    .SIGNAL_UPDATED.equals(action)) {

                updateBattery();

                upHodp();
                if (Constants.GPSMODECHOOSE == Constants.NOGPSMODETAKEOFF || Constants.GPSMODECHOOSE == Constants
                        .GPSMODELAND || Constants.GPSMODECHOOSE == Constants.NOGPSMODERTL) {


                } else if (mDrone.getGps().getGpsEPH() > 0 && mDrone.getGps().getGpsEPH() <= 1.9 && mDrone.getGps()
                        .getSatCount() >= 10) {
//                    if (mDrone.getState().isArmed()) {
//                        MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LOITER);
//                    }
                }

            } else if (Constans.AttributeEvent.STATE_ARMING.equals(action)) {

                //                if (mDrone.getState().isArmed()) {
                //                    mHome = mDrone.getGps().getPosition();
                //                } else {
                //                    mHome = null;
                //                }
            } else if (Constans.AttributeEvent.CALIBRATION_MAG_STARTED.equals(action) || Constans.AttributeEvent
                    .CALIBRATION_MAG_ESTIMATION.equals(action) || Constans.AttributeEvent.CALIBRATION_MAG_COMPLETED
                    .equals(action) || Constans.AttributeEvent.CALIBRATION_IMU
                    .equals(action) || Constans.AttributeEvent.HEARTBEAT_TIMEOUT
                    .equals(action) || Constans.AttributeEvent.CALIBRATION_IMU_TIMEOUT
                    .equals(action)) {
                updateCalibration();
            }
            //TODO 失联处理
        }
    };

    private void updateGPSView() {
        tv_gpsNumber.setText(mDrone.getGps().getFixType() + "\n" + mDrone.getGps().getSatCount());
    }

    private void updateSpeedMsg() {
        if (mDrone.getState().isArmed()) {
            alt = DecimalFormatUtil.more2OneDecimal(mDrone.getSpeed().getAlt()/1000);
        } else {
            alt = DecimalFormatUtil.more2OneDecimal(mDrone.getSpeed().getAlt()/1000);
        }
        Log.d("高度", "updateSpeedMsg===" + mDrone.getSpeed().getAlt()/1000);
        mTv_altitude.setText(alt + " M");
        //               if (mDrone.getState().isArmed()) {
        //                   float climb = DecimalFormatUtil.more2OneDecimal(mDrone.getSpeed().getClimb());
        //                   alt = DecimalFormatUtil.more2OneDecimal(mDrone.getSpeed().getGroundSpeed());
        //                   float airSpeed = DecimalFormatUtil.more2OneDecimal(mDrone.getSpeed().getAirSpeed());
        //                   mTv_altitude.setText("高度：" + alt + " M");
        //               } else {
        //                   mTv_altitude.setText("高度：" + 0 + " M");
        //               }
    }

    private void updateDistanceMsgSimToo() {
        if (mHome != null) {
            float[] results = new float[1];
            Location.distanceBetween(mHome.getLatitude(), mHome.getLongitude(), mNow.getLatitude(), mNow.getLongitude
                    (), results);
            float diatance = DecimalFormatUtil.more2OneDecimal(results[0]);
            mTv_distance.setText("距离" + diatance + " M");
        } else {
            mTv_distance.setText(0 + " M");
        }
    }

    double[] powerValue = new double[]{
            8.21, 8.07, 8.02, 7.98, 7.94, 7.92, 7.9, 7.88,
            7.86, 7.85, 7.82, 7.81, 7.8, 7.78, 7.76, 7.75,
            7.73, 7.71, 7.7, 7.68, 7.67, 7.66, 7.65, 7.64,
            7.63, 7.61, 7.6, 7.57, 7.54, 7.51, 7.49, 7.46
            , 7.43, 7.41, 7.4, 7.39, 7.38, 7.37, 7.36,
            7.35, 7.34, 7.33, 7.32, 7.31, 7.3, 7.29, 7.28,
            7.27, 7.26, 7.25, 7.24, 7.23, 7.22, 7.21, 7.2,
            7.19, 7.18, 7.17, 7.16, 7.15, 7.14, 7.13, 7.12,
            7.11, 7.1, 7.09, 7.08, 7.07, 7.06, 7.05, 7.04,
            7.03, 7.02, 7.01, 7, 6.99, 6.98, 6.97, 6.96,
            6.95, 6.94, 6.93, 6.92, 6.91, 6.9, 6.89, 6.88,
            6.87, 6.86, 6.85, 6.84, 6.83, 6.82, 6.81, 6.8, 6.79, 6.78,
            6.77, 6.76, 6.75, 6.74
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Constants.CURRENTROCK = Constants.ROCKCLOSE;
            MainActivity.this.finish();
            //            }
            //            lastTime = currentTime;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateBattery() {
        //        if (batt_fulls == 0) {
        //            tv_battery.setText("--%");
        //        }

        int batt = mDrone.getBattery().getBattRemain();
        //        Log.d("电池", "电池百分比：:" + batt);

        //        Log.d("电池", "电池电压：:" + batt_volt);
        double batt_current = mDrone.getBattery().getBattCurrent();
        //        Log.d("电池", "电池电流：:" + batt_current);
        //        采用滤波形式过滤无效值，从而得到百分比


        new Thread(new Runnable() {
            @Override
            public void run() {
                batt_volt = mDrone.getBattery().getBattVolt();
                Log.d("电池", "===batt_volt===" + batt_volt);
                if (batt_volt > 8.21) {
                    mHandlers.sendEmptyMessage(1);
                    //                    tv_battery.setText(String.valueOf(100) + "%");//95
                }

                if (6.89 > batt_volt) {
                    mHandlers.sendEmptyMessage(2);
                    //                    tv_battery.setText(String.valueOf(0) + "%");//95
                }
                if (batt_volt > batt_voltnew && Constants.CURRENTROCK == Constants.ROCKOPEN) {

                    Log.d("电量显示", "===batt_volt==9999=");
                } else {
                    for (int i = 0; i < 99; i++) {
                        if (powerValue[i] >= batt_volt && batt_volt >= powerValue[i + 1]) {
                            Log.d("电池", "======" + String.valueOf(100 - i) + "%");
                            //                tv_battery.setText(String.valueOf(100 - i) + "%");
                            battery = 100 - i;
                            if (battery <= 15) {
                                MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LAND);
                            }
                            mHandlers.sendEmptyMessage(3);
                            batt_voltnew = batt_volt;
                            Constants.CURRENTROCK = Constants.ROCKOPEN;
                        }
                    }
                }

            }
        }).start();
        //        Log.d("电量显示","======"+batt_volt);
        //        if (batt_volt >= 8.21) {
        //            tv_battery.setText(String.valueOf(100) + "%");
        //        }
        //        if (8.20 >= batt_volt && batt_volt >= 8.07) {
        //            tv_battery.setText(String.valueOf(99) + "%");
        //        }
        //        if (8.06 >= batt_volt && batt_volt >= 8.02) {
        //            tv_battery.setText(String.valueOf(98) + "%");
        //        }
        //        if (8.01 >= batt_volt && batt_volt >= 7.98) {
        //            tv_battery.setText(String.valueOf(97) + "%");
        //        }
        //        if (7.97 >= batt_volt && batt_volt >= 7.94) {
        //            tv_battery.setText(String.valueOf(96) + "%");
        //        }
        //        if (7.93 >= batt_volt && batt_volt >= 7.92) {
        //            tv_battery.setText(String.valueOf(95) + "%");
        //        }
        //        if (7.91 >= batt_volt && batt_volt >= 7.9) {
        //            tv_battery.setText(String.valueOf(94) + "%");
        //        }
        //        if (7.89 >= batt_volt && batt_volt >= 7.88) {
        //            tv_battery.setText(String.valueOf(93) + "%");
        //        }
        //        if (7.87 >= batt_volt && batt_volt >= 7.86) {
        //            tv_battery.setText(String.valueOf(92) + "%");
        //        }
        //        if (7.85 >= batt_volt && batt_volt >= 7.85) {
        //            tv_battery.setText(String.valueOf(91) + "%");
        //        }
        //        if (7.84 >= batt_volt && batt_volt >= 7.82) {
        //            tv_battery.setText(String.valueOf(90) + "%");
        //        }
        //        if (7.81 >= batt_volt && batt_volt >= 7.81) {
        //            tv_battery.setText(String.valueOf(89) + "%");
        //        }
        //        if (7.8 >= batt_volt && batt_volt >= 7.8) {
        //            tv_battery.setText(String.valueOf(88) + "%");
        //        }
        //        if (7.79 >= batt_volt && batt_volt >= 7.78) {
        //            tv_battery.setText(String.valueOf(87) + "%");
        //        }
        //        if (7.77 >= batt_volt && batt_volt >= 7.76) {
        //            tv_battery.setText(String.valueOf(86) + "%");
        //        }
        //        if (7.75 >= batt_volt && batt_volt >= 7.75) {
        //            tv_battery.setText(String.valueOf(85) + "%");
        //        }
        //        if (7.74 >= batt_volt && batt_volt >= 7.73) {
        //            tv_battery.setText(String.valueOf(84) + "%");
        //        }
        //        if (7.72 >= batt_volt && batt_volt >= 7.71) {
        //            tv_battery.setText(String.valueOf(83) + "%");
        //        }
        //        if (7.7 >= batt_volt && batt_volt >= 7.7) {
        //            tv_battery.setText(String.valueOf(82) + "%");
        //        }
        //        if (7.69 >= batt_volt && batt_volt >= 7.68) {
        //            tv_battery.setText(String.valueOf(81) + "%");
        //        }
        //        if (7.67 >= batt_volt && batt_volt >= 7.67) {
        //            tv_battery.setText(String.valueOf(80) + "%");
        //        }
        //        if (7.66 >= batt_volt && batt_volt >= 7.66) {
        //            tv_battery.setText(String.valueOf(79) + "%");
        //        }
        //        if (7.65 >= batt_volt && batt_volt >= 7.65) {
        //            tv_battery.setText(String.valueOf(78) + "%");
        //        }
        //        if (7.64 >= batt_volt && batt_volt >= 7.64) {
        //            tv_battery.setText(String.valueOf(77) + "%");
        //        }
        //        if (7.63 >= batt_volt && batt_volt >= 7.63) {
        //            tv_battery.setText(String.valueOf(76) + "%");
        //        }
        //        if (7.62 >= batt_volt && batt_volt >= 7.61) {
        //            tv_battery.setText(String.valueOf(75) + "%");
        //        }
        //        if (7.60 >= batt_volt && batt_volt >= 7.60) {
        //            tv_battery.setText(String.valueOf(74) + "%");
        //        }
        //        if (7.59 >= batt_volt && batt_volt >= 7.57) {
        //            tv_battery.setText(String.valueOf(73) + "%");
        //        }
        //        if (7.56 >= batt_volt && batt_volt >= 7.54) {
        //            tv_battery.setText(String.valueOf(72) + "%");
        //        }
        //        if (7.53 >= batt_volt && batt_volt >= 7.51) {
        //            tv_battery.setText(String.valueOf(71) + "%");
        //        }
        //        if (7.50 >= batt_volt && batt_volt >= 7.49) {
        //            tv_battery.setText(String.valueOf(70) + "%");
        //        }
        //        if (7.48 >= batt_volt && batt_volt >= 7.46) {
        //            tv_battery.setText(String.valueOf(69) + "%");
        //        }
        //        if (7.45 >= batt_volt && batt_volt >= 7.43) {
        //            tv_battery.setText(String.valueOf(68) + "%");
        //        }
        //        if (7.42 >= batt_volt && batt_volt >= 7.41) {
        //            tv_battery.setText(String.valueOf(67) + "%");
        //        }
        //        if (7.40 >= batt_volt && batt_volt >= 7.40) {
        //            tv_battery.setText(String.valueOf(66) + "%");
        //        }
        //        if (7.39 >= batt_volt && batt_volt >= 7.39) {
        //            tv_battery.setText(String.valueOf(65) + "%");
        //        }
        //        if (7.38 >= batt_volt && batt_volt >= 7.38) {
        //            tv_battery.setText(String.valueOf(64) + "%");
        //        }
        //        if (7.37 >= batt_volt && batt_volt >= 7.37) {
        //            tv_battery.setText(String.valueOf(63) + "%");
        //        }
        //        if (7.36 >= batt_volt && batt_volt >= 7.36) {
        //            tv_battery.setText(String.valueOf(62) + "%");
        //        }
        //        if (7.35 >= batt_volt && batt_volt >= 7.35) {
        //            tv_battery.setText(String.valueOf(61) + "%");
        //        }
        //        if (7.34 >= batt_volt && batt_volt >= 7.34) {
        //            tv_battery.setText(String.valueOf(60) + "%");
        //        }
        //        if (7.33 >= batt_volt && batt_volt >= 7.33) {
        //            tv_battery.setText(String.valueOf(59) + "%");
        //        }
        //        if (7.32 >= batt_volt && batt_volt >= 7.32) {
        //            tv_battery.setText(String.valueOf(58) + "%");
        //        }
        //        if (7.31 >= batt_volt && batt_volt >= 7.31) {
        //            tv_battery.setText(String.valueOf(57) + "%");
        //        }
        //        if (7.30 >= batt_volt && batt_volt >= 7.30) {
        //            tv_battery.setText(String.valueOf(56) + "%");
        //        }
        //        if (7.29 >= batt_volt && batt_volt >= 7.29) {
        //            tv_battery.setText(String.valueOf(55) + "%");
        //        }
        //        if (7.28 >= batt_volt && batt_volt >= 7.28) {
        //            tv_battery.setText(String.valueOf(54) + "%");
        //        }
        //        if (7.27 >= batt_volt && batt_volt >= 7.27) {
        //            tv_battery.setText(String.valueOf(53) + "%");
        //        }
        //        if (7.26 >= batt_volt && batt_volt >= 7.26) {
        //            tv_battery.setText(String.valueOf(52) + "%");
        //        }
        //        if (7.25 >= batt_volt && batt_volt >= 7.25) {
        //            tv_battery.setText(String.valueOf(51) + "%");
        //        }
        //        if (7.24 >= batt_volt && batt_volt >= 7.24) {
        //            tv_battery.setText(String.valueOf(50) + "%");
        //        }
        //        if (7.23 >= batt_volt && batt_volt >= 7.23) {
        //            tv_battery.setText(String.valueOf(49) + "%");
        //        }
        //        if (7.22 >= batt_volt && batt_volt >= 7.22) {
        //            tv_battery.setText(String.valueOf(48) + "%");
        //        }
        //        if (7.21 >= batt_volt && batt_volt >= 7.21) {
        //            tv_battery.setText(String.valueOf(47) + "%");
        //        }
        //        if (7.20 >= batt_volt && batt_volt >= 7.20) {
        //            tv_battery.setText(String.valueOf(46) + "%");
        //        }
        //        if (7.19 >= batt_volt && batt_volt >= 7.19) {
        //            tv_battery.setText(String.valueOf(45) + "%");
        //        }
        //        if (7.18 >= batt_volt && batt_volt >= 7.18) {
        //            tv_battery.setText(String.valueOf(44) + "%");
        //        }
        //        if (7.17 >= batt_volt && batt_volt >= 7.17) {
        //            tv_battery.setText(String.valueOf(43) + "%");
        //        }
        //        if (7.16 >= batt_volt && batt_volt >= 7.16) {
        //            tv_battery.setText(String.valueOf(42) + "%");
        //        }
        //        if (7.15 >= batt_volt && batt_volt >= 7.15) {
        //            tv_battery.setText(String.valueOf(41) + "%");
        //        }
        //        if (7.14 >= batt_volt && batt_volt >= 7.14) {
        //            tv_battery.setText(String.valueOf(40) + "%");
        //        }
        //        if (7.13 >= batt_volt && batt_volt >= 7.13) {
        //            tv_battery.setText(String.valueOf(39) + "%");
        //        }
        //        if (7.12 >= batt_volt && batt_volt >= 7.12) {
        //            tv_battery.setText(String.valueOf(38) + "%");
        //        }
        //        if (7.11 >= batt_volt && batt_volt >= 7.11) {
        //            tv_battery.setText(String.valueOf(37) + "%");
        //        }
        //        if (7.10 >= batt_volt && batt_volt >= 7.10) {
        //            tv_battery.setText(String.valueOf(36) + "%");
        //        }
        //        if (7.09 >= batt_volt && batt_volt >= 7.09) {
        //            tv_battery.setText(String.valueOf(35) + "%");
        //        }
        //        if (7.08 >= batt_volt && batt_volt >= 7.08) {
        //            tv_battery.setText(String.valueOf(34) + "%");
        //        }
        //        if (7.07 >= batt_volt && batt_volt >= 7.07) {
        //            tv_battery.setText(String.valueOf(33) + "%");
        //        }
        //        if (7.06 >= batt_volt && batt_volt >= 7.06) {
        //            tv_battery.setText(String.valueOf(32) + "%");
        //        }
        //        if (7.05 >= batt_volt && batt_volt >= 7.05) {
        //            tv_battery.setText(String.valueOf(31) + "%");
        //        }
        //        if (7.04 >= batt_volt && batt_volt >= 7.04) {
        //            tv_battery.setText(String.valueOf(30) + "%");
        //        }
        //        if (7.03 >= batt_volt && batt_volt >= 7.03) {
        //            tv_battery.setText(String.valueOf(29) + "%");
        //        }
        //        if (7.02 >= batt_volt && batt_volt >= 7.02) {
        //            tv_battery.setText(String.valueOf(28) + "%");
        //        }
        //        if (7.01 >= batt_volt && batt_volt >= 7.01) {
        //            tv_battery.setText(String.valueOf(27) + "%");
        //        }
        //        if (7.0 >= batt_volt && batt_volt >= 7.0) {
        //            tv_battery.setText(String.valueOf(26) + "%");
        //        }
        //        if (6.99 >= batt_volt && batt_volt >= 6.99) {
        //            tv_battery.setText(String.valueOf(25) + "%");
        //        }
        //        if (6.98 >= batt_volt && batt_volt >= 6.98) {
        //            tv_battery.setText(String.valueOf(24) + "%");
        //        }
        //        if (6.97 >= batt_volt && batt_volt >= 6.97) {
        //            tv_battery.setText(String.valueOf(23) + "%");
        //        }
        //        if (6.96 >= batt_volt && batt_volt >= 6.96) {
        //            tv_battery.setText(String.valueOf(22) + "%");
        //        }
        //        if (6.95 >= batt_volt && batt_volt >= 6.95) {
        //            tv_battery.setText(String.valueOf(21) + "%");
        //        }
        //        if (6.94 >= batt_volt && batt_volt >= 6.94) {
        //            tv_battery.setText(String.valueOf(20) + "%");
        //        }
        //        if (6.93 >= batt_volt && batt_volt >= 6.93) {
        //            tv_battery.setText(String.valueOf(19) + "%");
        //        }
        //        if (6.92 >= batt_volt && batt_volt >= 6.92) {
        //            tv_battery.setText(String.valueOf(18) + "%");
        //        }
        //        if (6.91 >= batt_volt && batt_volt >= 6.91) {
        //            tv_battery.setText(String.valueOf(17) + "%");
        //        }
        //        if (6.9 >= batt_volt && batt_volt >= 6.9) {
        //            tv_battery.setText(String.valueOf(16) + "%");
        //        }
        //        if (6.89 >= batt_volt && batt_volt >= 6.89) {
        //            tv_battery.setText(String.valueOf(15) + "%");
        //        }
        //        if (6.88 >= batt_volt && batt_volt >= 6.88) {
        //            tv_battery.setText(String.valueOf(14) + "%");
        //        }
        //        if (6.87 >= batt_volt && batt_volt >= 6.87) {
        //            tv_battery.setText(String.valueOf(13) + "%");
        //        }

        //        if (batt_volt <= 13.5 && batt_volt >= 6 && batt_current >= 0.5 && batt_current <= 25 && batt >= 0
        // && batt <= 100
        //                ) {
        //
        //            if (isBatt) {
        //                if (batt_index < 5) {
        //                    batts[batt_index] = batt;
        //                    batt_index++;
        //                } else {
        //                    isBatt = false;
        //                    int index = 0;
        //                    int maxIndex = 0;
        //                    for (int i = 0; i < batts.length; i++) {
        //                        index = 0;
        //                        for (int j = i + 1; j < batts.length; j++) {
        //                            if (batts[i] == batts[j]) {
        //                                index++;
        //                            }
        //                        }
        //                        if (index >= maxIndex) {
        //                            batt_fulls = batts[i];
        //                            maxIndex = index;
        //                        }
        //                    }
        //                }
        //            } else {
        //                if (batt_fulls + 2 >= batt && batt_fulls - 2 <= batt) {
        //                    batt_fulls = batt;
        //                    //                    Log.d("dianchi","111111=="+batt_fulls);
        //                    if (batt_fulls == 5) {
        //                        Constants.D = Constants.C;
        //                        //                        imgbtn_follow.setBackgroundResource(R.mipmap
        // .moment_follow_w);
        //                        //震动的方式
        //                        if (sendTemo > 1000) {
        //                            sendTemo = 1000;
        //                            rcOutputs[7] = sendTemo;
        //                        }
        //                        MavLinkModes.changeFlightMode(mDrone, ApmModes.ROTOR_LAND);
        //                    }
        //
        //                    if (batt_fulls >= 80) {
        //
        //                    } else if (batt_fulls < 80 && batt_fulls >= 40) {
        //
        //                    } else if (batt_fulls < 40 && batt_fulls > 15) {
        //
        //                    } else if (batt_fulls < 15) {
        //                        tv_battery.setTextColor(Color.RED);
        //                        //                        vibrator.vibrate(1000);//振动十秒
        //                        //            tv_low_batt_land.setVisibility(View.GONE);
        //                        //                               mLow_batt.setVisibility(View.VISIBLE);
        //                        //                               //            mTv_low_batt.setVisibility(View
        // .VISIBLE);
        //                        //                               setFlickerAnimations(mTv_low_batt);
        //                        //                               imgbtn_battery.setBackgroundResource(R.mipmap
        // .battery_low);
        //                    } else if (batt_fulls <= 15) {
        //                        Log.d("电池", "111" + batt_fulls);
        //                        tv_battery.setTextColor(Color.RED);
        //
        //                        //                        vibrator.vibrate(1000);//振动十秒
        //                        //            tv_low_batt_land.setVisibility(View.GONE);
        //                        //                               mLow_batt.setVisibility(View.VISIBLE);
        //                        //                               //            mTv_low_batt.setVisibility(View
        // .VISIBLE);
        //                        //                               setFlickerAnimations(mTv_low_batt);
        //                        //                               imgbtn_battery.setBackgroundResource(R.mipmap
        // .battery_low);
        //                        //                        setFlickerAnimation(imgbtn_battery);
        //                    }
        //                    if (batt_fulls == 15) {
        //                        Log.d("电池", "222");
        //                        //                               vibrator.vibrate(1000);//振动十秒
        //                        tv_battery.setTextColor(Color.RED);
        //                    } else if (batt_fulls < 15) {
        //                        tv_battery.setTextColor(Color.RED);
        //                    } else {
        //                        tv_battery.setTextColor(Color.GRAY);
        //                    }
        //
        //                    tv_battery.setText(String.valueOf(batt_fulls) + "%");
        //
        //
        //                }
        //            }
        //        }
        //
        //        return;
    }


    //注意事项，飞机飞行中，不允许让客户点击罗盘和加速计校准按键。
    //校准信息     罗盘校准操作，到时候，给你们发个视频。
    private void updateCalibration() {
        String calIMUMessage = mDrone.getCalibrationSetup()
                .getMessage();
        if (calIMUMessage.contains("finish level calibration")) {
            //加速计校准，需要把飞机水平放置才能开始进行校准。
            Toast.makeText(this, "加速计校准成功", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            //上升
            case R.id.Up:
                float up_w = imgbtn_up.getMeasuredWidth();
                float up_h = imgbtn_up.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpTouch && !isDownTouch && !isLeftTouch && !isRightTouch) {
                            rcOutputs[2] = 1750;
                            imgbtn_up.setBackgroundResource(R.drawable.control_btn_pressed);
                            isUpTouch = true;
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float up_x = motionEvent.getX();
                        float up_y = motionEvent.getY();
                        if (up_x > up_w || up_x < 0 || up_y > up_h || up_y < 0) {
                            if (isUpTouch) {
                                rcOutputs[2] = 1500;
                                imgbtn_up.setBackgroundResource(R.drawable.control_btn_normal);
                                isUpTouch = false;
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (isUpTouch) {
                            rcOutputs[2] = 1500;
                            imgbtn_up.setBackgroundResource(R.drawable.control_btn_normal);
                            //                            isOnLongClick = false;
                            isUpTouch = false;
                        }
                        break;
                }
                break;
            //下降
            case R.id.down:
                float down_w = imgbtn_down.getMeasuredWidth();
                float down_h = imgbtn_down.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpTouch && !isDownTouch && !isLeftTouch && !isRightTouch) {
                            rcOutputs[2] = 1250;
                            imgbtn_down.setBackgroundResource(R.drawable.control_btn_pressed);
                            isDownTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float down_x = motionEvent.getX();
                        float down_y = motionEvent.getY();
                        if (down_x > down_w || down_x < 0 || down_y > down_h || down_y < 0) {
                            if (isDownTouch) {
                                rcOutputs[2] = 1500;
                                imgbtn_down.setBackgroundResource(R.drawable.control_btn_normal);
                                isDownTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isDownTouch) {
                            rcOutputs[2] = 1500;
                            imgbtn_down.setBackgroundResource(R.drawable.control_btn_normal);
                            isDownTouch = false;
                        }
                        break;
                }
                break;

            //左转向
            case R.id.left:
                float left_w = imgbtn_left.getMeasuredWidth();
                float left_h = imgbtn_left.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpTouch && !isDownTouch && !isLeftTouch && !isRightTouch) {
                            rcOutputs[3] = 1350;
                            imgbtn_left.setBackgroundResource(R.drawable.control_btn_pressed);
                            isLeftTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float left_x = motionEvent.getX();
                        float left_y = motionEvent.getY();
                        if (left_x > left_w || left_x < 0 || left_y > left_h || left_y < 0) {
                            if (isLeftTouch) {
                                rcOutputs[3] = 1500;
                                imgbtn_left.setBackgroundResource(R.drawable.control_btn_normal);
                                isLeftTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isLeftTouch) {
                            rcOutputs[3] = 1500;
                            imgbtn_left.setBackgroundResource(R.drawable.control_btn_normal);
                            isLeftTouch = false;
                        }
                        break;
                }
                break;
            //右转向
            case R.id.right:
                float right_w = imgbtn_right.getMeasuredWidth();
                float right_h = imgbtn_right.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpTouch && !isDownTouch && !isLeftTouch && !isRightTouch) {
                            rcOutputs[3] = 1650;
                            imgbtn_right.setBackgroundResource(R.drawable.control_btn_pressed);
                            isRightTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float right_x = motionEvent.getX();
                        float right_y = motionEvent.getY();
                        if (right_x > right_w || right_x < 0 || right_y > right_h || right_y < 0) {
                            if (isRightTouch) {
                                rcOutputs[3] = 1500;
                                imgbtn_right.setBackgroundResource(R.drawable.control_btn_normal);
                                isRightTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isRightTouch) {
                            rcOutputs[3] = 1500;
                            imgbtn_right.setBackgroundResource(R.drawable.control_btn_normal);
                            isRightTouch = false;
                        }
                        break;
                }
                break;
            //前进
            case R.id.ups:
                float ups_w = imgbtn_ups.getMeasuredWidth();
                float ups_h = imgbtn_ups.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpsTouch && !isDownsTouch && !isLeftsTouch && !isRightsTouch) {
                            rcOutputs[1] = 1350;
                            imgbtn_ups.setBackgroundResource(R.drawable.control_btn_pressed);
                            isUpsTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float ups_x = motionEvent.getX();
                        float ups_y = motionEvent.getY();
                        if (ups_x > ups_w || ups_x < 0 || ups_y > ups_h || ups_y < 0) {
                            if (isUpsTouch) {
                                rcOutputs[1] = 1500;
                                imgbtn_ups.setBackgroundResource(R.drawable.control_btn_normal);
                                isUpsTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isUpsTouch) {
                            rcOutputs[1] = 1500;
                            imgbtn_ups.setBackgroundResource(R.drawable.control_btn_normal);
                            isUpsTouch = false;
                        }
                        break;
                }
                break;
            //后退
            case R.id.downs:
                float downs_w = imgbtn_downs.getMeasuredWidth();
                float downs_h = imgbtn_downs.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpsTouch && !isDownsTouch && !isLeftsTouch && !isRightsTouch) {
                            rcOutputs[1] = 1650;
                            imgbtn_downs.setBackgroundResource(R.drawable.control_btn_pressed);
                            isDownsTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float downs_x = motionEvent.getX();
                        float downs_y = motionEvent.getY();
                        if (downs_x > downs_w || downs_x < 0 || downs_y > downs_h || downs_y < 0) {
                            if (isDownsTouch) {
                                rcOutputs[1] = 1500;
                                imgbtn_downs.setBackgroundResource(R.drawable.control_btn_normal);
                                isDownsTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isDownsTouch) {
                            rcOutputs[1] = 1500;
                            imgbtn_downs.setBackgroundResource(R.drawable.control_btn_normal);
                            isDownsTouch = false;
                        }
                        break;
                }
                break;
            //左偏移
            case R.id.lefts:
                float lefts_w = imgbtn_lefts.getMeasuredWidth();
                float lefts_h = imgbtn_lefts.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpsTouch && !isDownsTouch && !isLeftsTouch && !isRightsTouch) {
                            rcOutputs[0] = 1350;
                            imgbtn_lefts.setBackgroundResource(R.drawable.control_btn_pressed);
                            isLeftsTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float lefts_x = motionEvent.getX();
                        float lefts_y = motionEvent.getY();
                        if (lefts_x > lefts_w || lefts_x < 0 || lefts_y > lefts_h || lefts_y < 0) {

                            if (isLeftsTouch) {
                                rcOutputs[0] = 1500;
                                imgbtn_lefts.setBackgroundResource(R.drawable.control_btn_normal);
                                isLeftsTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isLeftsTouch) {
                            rcOutputs[0] = 1500;
                            imgbtn_lefts.setBackgroundResource(R.drawable.control_btn_normal);
                            isLeftsTouch = false;
                        }
                        break;
                }
                break;
            //右偏移
            case R.id.rights:
                float rights_w = imgbtn_rights.getMeasuredWidth();
                float rights_h = imgbtn_rights.getMeasuredHeight();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isUpsTouch && !isDownsTouch && !isLeftsTouch && !isRightsTouch) {
                            rcOutputs[0] = 1650;
                            imgbtn_rights.setBackgroundResource(R.drawable.control_btn_pressed);
                            isRightsTouch = true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float rights_x = motionEvent.getX();
                        float rights_y = motionEvent.getY();
                        if (rights_x > rights_w || rights_x < 0 || rights_y > rights_h || rights_y < 0) {
                            if (isRightsTouch) {
                                rcOutputs[0] = 1500;
                                imgbtn_rights.setBackgroundResource(R.drawable.control_btn_normal);
                                isRightsTouch = false;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isRightsTouch) {
                            rcOutputs[0] = 1500;
                            imgbtn_rights.setBackgroundResource(R.drawable.control_btn_normal);
                            isRightsTouch = false;
                        }
                        break;
                }
                break;

            case R.id.ib_adjust_up://ib_adjust_up
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        isOnLongClickss = true;

                        miusYunThread = new MiusYunThread();
                        miusYunThread.start();

                        mib_adjust_up.setBackgroundResource(R.drawable.control_btn_pressed);
                        upYun = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (upYun) {


                            if (miusYunThread != null) {
                                isOnLongClick = false;
                            }
                            mib_adjust_up.setBackgroundResource(R.drawable.control_btn_normal);
                            isOnLongClickss = false;

                            upYun = false;

                        }
                        break;
                }
                break;


            case R.id.ib_adjust_down://ib_adjust_down
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        isOnLongClickss = true;
                        mib_adjust_down.setBackgroundResource(R.drawable.control_btn_pressed);
                        plusYunThread = new PlusYunThread();
                        plusYunThread.start();
                        downYun = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (downYun) {

                            if (plusYunThread != null) {
                                isOnLongClickss = false;
                            }
                            mib_adjust_down.setBackgroundResource(R.drawable.control_btn_normal);
                            isOnLongClickss = false;
                            downYun = false;

                        }
                        break;
                }
                break;
        }
        return true;
    }


    // 加操作左右通道 0
    class PlusYunThread extends Thread {
        @Override
        public void run() {
            try {
                while (isOnLongClickss) {
                    Thread.sleep(30);
                    if (sendTemo < 1900) {
                        sendTemo += 10;
                    }
                    rcOutputs[6] = sendTemo;//油门sendTemY+=4;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 减操作左右通道 0
    class MiusYunThread extends Thread {
        @Override
        public void run() {
            try {
                while (isOnLongClickss) {
                    Thread.sleep(30);
                    if (sendTemo > 1000) {
                        sendTemo -= 10;

                    }
                    rcOutputs[6] = sendTemo;//油门sendTemY+=4;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
