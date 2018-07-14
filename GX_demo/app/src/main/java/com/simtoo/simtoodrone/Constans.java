package com.simtoo.simtoodrone;

/**
 * 创建者     张涛
 * 创建时间   2016/6/21 0021 14:39
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class Constans {

    public class AttributeEvent {

        private static final String PACKAGE_NAME = "com.simtoo.simtoodrone";

        public static final String ATTITUDE_UPDATED = PACKAGE_NAME + ".ATTITUDE_UPDATED";

        public static final String AUTOPILOT_FAILSAFE = PACKAGE_NAME + ".AUTOPILOT_FAILSAFE";

        public static final String CALIBRATION_MAG_STARTED    = PACKAGE_NAME +
                ".CALIBRATION_MAG_STARTED";
        public static final String CALIBRATION_MAG_ESTIMATION = PACKAGE_NAME +
                ".CALIBRATION_MAG_ESTIMATION";
        public static final String CALIBRATION_MAG_COMPLETED  = PACKAGE_NAME +
                ".CALIBRATION_MAG_COMPLETED";

        public static final String CALIBRATION_IMU         = PACKAGE_NAME + ".CALIBRATION_IMU";
        public static final String CALIBRATION_IMU_ERROR   = PACKAGE_NAME + ".CALIBRATION_IMU_ERROR";
        public static final String CALIBRATION_IMU_TIMEOUT = PACKAGE_NAME +
                ".CALIBRATION_IMU_TIMEOUT";

        public static final String FOLLOW_START  = PACKAGE_NAME + ".FOLLOW_START";
        public static final String FOLLOW_STOP   = PACKAGE_NAME + ".FOLLOW_STOP";
        public static final String FOLLOW_UPDATE = PACKAGE_NAME + ".FOLLOW_UPDATE";

        public static final String CAMERA_UPDATED            = PACKAGE_NAME + ".CAMERA_UPDATED";
        public static final String CAMERA_FOOTPRINTS_UPDATED = PACKAGE_NAME + ".CAMERA_FOOTPRINTS_UPDATED";

        public static final String GUIDED_POINT_UPDATED = PACKAGE_NAME + ".GUIDED_POINT_UPDATED";

        public static final String MISSION_UPDATED        = PACKAGE_NAME + ".MISSION_UPDATED";
        public static final String MISSION_DRONIE_CREATED = PACKAGE_NAME + "" +
                ".MISSION_DRONIE_CREATED";
        public static final String MISSION_SENT           = PACKAGE_NAME + ".MISSION_SENT";
        public static final String MISSION_RECEIVED       = PACKAGE_NAME + ".MISSION_RECEIVED";
        public static final String MISSION_ITEM_UPDATED   = PACKAGE_NAME + ".MISSION_ITEM_UPDATED";

        public static final String PARAMETERS_REFRESH_STARTED = PACKAGE_NAME + ".PARAMETERS_REFRESH_STARTED";
        public static final String PARAMETERS_REFRESH_ENDED   = PACKAGE_NAME + ".PARAMETERS_REFRESH_ENDED";
        public static final String PARAMETERS_RECEIVED        = PACKAGE_NAME + ".PARAMETERS_RECEIVED";

        public static final String TYPE_UPDATED = PACKAGE_NAME + ".TYPE_UPDATED";

        public static final String SIGNAL_UPDATED = PACKAGE_NAME + ".SIGNAL_UPDATED";
        public static final String SIGNAL_WEAK    = PACKAGE_NAME + ".SIGNAL_WEAK";

        public static final String SPEED_UPDATED = PACKAGE_NAME + ".SPEED_UPDATED";

        public static final String BATTERY_UPDATED = PACKAGE_NAME + ".BATTERY_UPDATED";

        public static final String STATE_UPDATED      = PACKAGE_NAME + ".STATE_UPDATED";
        public static final String STATE_ARMING       = PACKAGE_NAME + ".STATE_ARMING";
        public static final String STATE_CONNECTED    = PACKAGE_NAME + ".STATE_CONNECTED";
        public static final String STATE_DISCONNECTED = PACKAGE_NAME + ".STATE_DISCONNECTED";
        public static final String STATE_VEHICLE_MODE = PACKAGE_NAME + ".STATE_VEHICLE_MODE";

        public static final String HOME_UPDATED = PACKAGE_NAME + ".HOME_UPDATED";

        public static final String GPS_POSITION   = PACKAGE_NAME + ".GPS_POSITION";
        public static final String GPS_FIX        = PACKAGE_NAME + ".GPS_FIX";
        public static final String GPS_COUNT      = PACKAGE_NAME + ".GPS_COUNT";
        public static final String WARNING_NO_GPS = PACKAGE_NAME + ".WARNING_NO_GPS";

        public static final String HEARTBEAT_FIRST    = PACKAGE_NAME + ".HEARTBEAT_FIRST";
        public static final String HEARTBEAT_RESTORED = PACKAGE_NAME + ".HEARTBEAT_RESTORED";
        public static final String HEARTBEAT_TIMEOUT  = PACKAGE_NAME + ".HEARTBEAT_TIMEOUT";

        public static final String ALTITUDE_400FT_EXCEEDED = PACKAGE_NAME +
                ".ALTITUDE_400FT_EXCEEDED";

        public static final String START_RCOUTPUTS     = PACKAGE_NAME + ".START_RCOUTPUTS";//开始油门
        public static final String STOP_RCOUTPUTS      = PACKAGE_NAME + ".STOP_RCOUTPUTS";
        public static final String POINT_CIRCLE_OPENED = PACKAGE_NAME + ".POINT_CIRCLE_OPENED";//热点环绕开启
        public static final String POINT_CIRCLE_CLOSED = PACKAGE_NAME + ".POINT_CIRCLE_CLOSED";//热点环绕关闭

        public static final String CALI_HORIZONTAL = PACKAGE_NAME + ".CALI_HORIZONTAL";//热点环绕开启
        public static final String CALI_VERTICAL   = PACKAGE_NAME + ".CALI_VERTICAL";//热点环绕关闭
        public static final String CAIL_SUCCEED    = PACKAGE_NAME + ".CAIL_SUCCEED";//热点环绕关闭

    }
    public class AttributeEventExtra {

        private static final String PACKAGE_NAME = "com.simtoo.simtoodrone" +
                ".extra";

        public static final String EXTRA_AUTOPILOT_FAILSAFE_MESSAGE = PACKAGE_NAME + "" +
                ".AUTOPILOT_FAILSAFE_MESSAGE";

        public static final String EXTRA_CALIBRATION_IMU_MESSAGE = PACKAGE_NAME +
                ".CALIBRATION_IMU_MESSAGE";

        public static final String EXTRA_CALIBRATION_MAG_POINTS_X   = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_POINTS_X";
        public static final String EXTRA_CALIBRATION_MAG_POINTS_Y   = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_POINTS_Y";
        public static final String EXTRA_CALIBRATION_MAG_POINTS_Z   = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_POINTS_Z";
        public static final String EXTRA_CALIBRATION_MAG_FITNESS    = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_FITNESS";
        public static final String EXTRA_CALIBRATION_MAG_FIT_CENTER = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_FIT_CENTER";
        public static final String EXTRA_CALIBRATION_MAG_FIT_RADII  = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_FIT_RADII";
        public static final String EXTRA_CALIBRATION_MAG_OFFSETS    = PACKAGE_NAME + "" +
                ".CALIBRATION_MAG_OFFSETS";

        public static final String EXTRA_MAVLINK_VERSION = PACKAGE_NAME + ".MAVLINK_VERSION";

        public static final String EXTRA_MISSION_CURRENT_WAYPOINT = PACKAGE_NAME + "" +
                ".MISSION_CURRENT_WAYPOINT";
        public static final String EXTRA_MISSION_DRONIE_BEARING   = PACKAGE_NAME + "" +
                ".MISSION_DRONIE_BEARING";

        public static final String EXTRA_PARAMETERS_COUNT = PACKAGE_NAME + ".PARAMETERS_COUNT";
        public static final String EXTRA_PARAMETER_INDEX  = PACKAGE_NAME + ".PARAMETER_INDEX";

        public static final String STATE_UPDATED      = PACKAGE_NAME + ".STATE_UPDATED";
        public static final String STATE_ARMING       = PACKAGE_NAME + ".STATE_ARMING";
        public static final String STATE_CONNECTED    = PACKAGE_NAME + ".STATE_CONNECTED";
        public static final String STATE_DISCONNECTED = PACKAGE_NAME + ".STATE_DISCONNECTED";
        public static final String STATE_VEHICLE_MODE = PACKAGE_NAME + ".STATE_VEHICLE_MODE";
    }
}
