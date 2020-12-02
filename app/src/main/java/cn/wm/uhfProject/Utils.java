package cn.wm.uhfProject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class Utils {

    // Build.BOARD + "_" + Build.VERSION.SDK_INT
    public static final String DEVICE_O_W530 = "k65v1_64_bsp_27";

    public static void handleRfidPower(Context context, boolean powerOn) {
        boolean result = false;
        // i11 && 8.1
        if (DEVICE_O_W530.equals(getDeviceKey())) {
            Intent pintent = new Intent("android.intent.action.SETTINGS_BJ");
            pintent.putExtra( "enable" , powerOn);
            context.sendBroadcast(pintent);
//            result = true;
        } else {
//            result = handleLedTest(powerOn? "51" : "50");
        }
        log("handleRfidPower: " + powerOn + " -> " + result);
    }

//    private static boolean handleLedTest(String params) {
//        boolean result = false;
//        try {
//            Class clazz = MainActivity.class.getClassLoader().loadClass("android.os.ServiceManager");
//
//            Method method = clazz.getDeclaredMethod("getService", String.class);
//            IBinder ibinder = (IBinder) method.invoke(null, "aw9523_led");
//            ILedManager service = ILedManager.Stub.asInterface(ibinder);
//            if (null != service) {
//                result = service.setLed(params);
//            }
//        } catch (Exception e) {
//            log("handleLedTest fail for exception: " + e);
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String getCOMPort() {
        // W530 && 8.1
        if (DEVICE_O_W530.equals(getDeviceKey())) {
            return "/dev/ttyS1";
        }
        return "/dev/ttyMT1";
    }

    public static String getDeviceKey() {
        return Build.BOARD + "_" + Build.VERSION.SDK_INT;
    }

    public static boolean isNetworkActive(Context context) {
        if (null == context) {
            log("isNetworkActive fail for context null");
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            log("isNetworkActive fail for cm null");
            return false;
        }
        NetworkInfo nwInfo = cm.getActiveNetworkInfo();
        return null != nwInfo && nwInfo.isConnected();
    }

    private static void log(String msg) {
//        Wlog.d(Utils.class, msg);
    }
}
