package cn.krvision.mynavidemo;

import android.util.Log;

/**
 * Created by Administrator on 2017/5/25.
 */

public class LogUtils {
    public static boolean isDebug = true;
    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }
}