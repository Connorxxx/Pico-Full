package com.connor.picofull.zc;

import java.lang.reflect.Method;

public class Device {

    public static String getPropStr(String key) {
        Class<?> mClassType = null;
        Method mGetMethod = null;
        String value = "";
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties");
                mGetMethod = mClassType.getDeclaredMethod("get", String.class);
                value = (String) mGetMethod.invoke(mClassType, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = "";
        }
        return value;
    }

}
