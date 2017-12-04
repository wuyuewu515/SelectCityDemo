package com.wyw.selectcitydemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 缓存类
 */
public class SharedPreUtils {

    public final static String CACHE = "CACHE";
    //系统枚举版本号
    public final static String APP_ENUM_VERSION = "APP_ENUM_VERSION";
    //系统枚举
    public final static String APP_ENUM_INFO = "APP_ENUM_INFO";
    public final static String APP_PACKAGE_VERSION = "APP_PACKAGE_VERSION";
    public final static String GUIDE_VERSION = "GUIDE_VERSION";
    public final static String USER_LOGINNAME = "USER_LOGINNAME";
    public final static String USER_AUTOLOGIN = "USER_AUTOLOGIN";
    public final static String DEVICENO = "DEVICENO";
    public final static String SESSION_ID = "SEESION_ID";
    public final static String LAST_MSG_ID = "LAST_MSG_ID";
    //下载id
    public final static String DOWNLOADID = "downLoadId";
    private SharedPreferences sp;
    static private SharedPreUtils instance;

    /**
     * 初始化动作
     * @param context
     * @return
     */
    static public SharedPreUtils getInstance(Context context) {
        if (instance == null || instance.sp == null) {
            instance = getInstance().init(context);
        }
        return instance;
    }

    /**
     * 初始化成功之后，可以不需要Context直接使用
     * @return
     */
    static public SharedPreUtils getInstance() {
        if (instance == null) {
            instance = new SharedPreUtils();
        }
        return instance;
    }

    private SharedPreUtils() {
    }

    /**
     * 初始化
     * @param context
     */
    public SharedPreUtils init(Context context){
        sp = context.getApplicationContext().getSharedPreferences(CACHE, Context.MODE_PRIVATE);

        return this;
    }

    /**
     * SharedPreferences通过key取值
     *
     * @param key
     * @param dfValue 默认值
     * @return
     */
    public String getValue(String key, String dfValue) {
        String value = sp.getString(key, dfValue);
        if ("null".equals(value)) {
            value = dfValue;
        }
        return value;
    }

    /**
     * SharedPreferences通过key存value
     *
     * @param key
     * @param value
     */
    public void saveValue(String key, String value) {
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * SharedPreferences通过key取值
     *
     * @param key
     * @param dfValue 默认值
     * @return
     */
    public boolean getValue(String key, boolean dfValue) {
        boolean value = sp.getBoolean(key, dfValue);
        if ("null".equals(value)) {
            value = dfValue;
        }
        return value;
    }

    /**
     * SharedPreferences通过key存value
     *
     * @param key
     * @param value
     */
    public void saveValue(String key, boolean value) {
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 清空所有数据
     */
    public void clearAllData() {
        Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 清空所有数据
     */
    public void clearData(String key) {
        Editor editor = sp.edit();
        if (sp.contains(key)) {
            editor.remove(key);
            editor.commit();
        }
    }
}
