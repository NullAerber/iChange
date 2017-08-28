package com.carporange.ichange.util;

import android.content.Context;

import cn.bmob.imdemo.iChangeApplication;


/**
 * Created by Liyuchen on 2016/6/14.
 * email:987424501@qq.com
 * phone:18298376275
 */
public class SpUtil {
    public static void put(String key, String value) {
        iChangeApplication.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }
    public static void put(String key, boolean value) {
        iChangeApplication.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return iChangeApplication.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE).getBoolean(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return iChangeApplication.getInstance().getSharedPreferences("data", Context.MODE_PRIVATE).getString(key, defValue);
    }


}
