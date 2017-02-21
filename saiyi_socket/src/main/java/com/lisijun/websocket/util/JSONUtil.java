package com.lisijun.websocket.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 描述：json工具
 * 创建作者：黎丝军
 * 创建时间：2016/11/12 9:43
 */

public class JSONUtil {

    //保存创建的实例
    private static JSONObject mJSONObject = null;

    /**
     * 由json字符串创建JSON对象
     * @param jsonStr json字符串
     * @return JSONObject实例，如果创建失败，返回null
     */
    public static JSONObject initJson(String jsonStr) {
        try {
            mJSONObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Log.d("LiSiJun",e.getMessage());
            Log.d("LiSiJun",e.getMessage());
        }
        return mJSONObject;
    }

    /**
     * 获取字符串
     * @param key 键值
     * @return String
     */
    public static String getString(String key) {
        if(mJSONObject != null) {
            return mJSONObject.optString(key);
        }
        return "";
    }

    /**
     * 获取字符串
     * @param key 键值
     * @return String
     */
    public static String getString(String key,String fallback) {
        if(mJSONObject != null) {
            return mJSONObject.optString(key,fallback);
        }
        return fallback;
    }
}
