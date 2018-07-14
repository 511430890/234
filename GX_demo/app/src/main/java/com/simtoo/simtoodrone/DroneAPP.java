package com.simtoo.simtoodrone;

import com.zt.mylibrary.connect.STDroidPlannerApp;

/**
 * 创建者     张涛
 * 创建时间   2018/1/30 19:32
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class DroneAPP extends STDroidPlannerApp {
    @Override
    public void onCreate() {
        super.onCreate();
        STDroidPlannerApp.getInstance().connectMavlink("192.168.201.1", 50000);
    }
}
