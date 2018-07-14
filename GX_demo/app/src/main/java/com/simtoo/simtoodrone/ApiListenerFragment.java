package com.simtoo.simtoodrone;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.zt.mylibrary.connect.STDroidPlannerApp;
import com.zt.mylibrary.connect.STDrone;

/**
 * 创建者     zhangtoa
 * 创建时间   2017/12/28 11:35
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public abstract class ApiListenerFragment extends Fragment {

    private STDroidPlannerApp     dpApp;
    private LocalBroadcastManager broadcastManager;

    //    protected MissionProxy getMissionProxy() { return dpApp.getMissionProxy(); }
    protected STDrone getDrone() {
        return dpApp.getSTDrone();
    }

    protected LocalBroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        dpApp = (STDroidPlannerApp) activity.getApplication();
        broadcastManager = LocalBroadcastManager.getInstance(activity.getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        //        dpApp.addApiListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //        dpApp.removeApiListener(this);
    }
}
