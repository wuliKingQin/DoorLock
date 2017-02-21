package cn.saiyi.doorlock.http;

import com.alibaba.fastjson.JSONObject;

import cn.finalteam.okhttpfinal.RequestParams;

/**
 * 描述：json请求参数
 * 创建作者：黎丝军
 * 创建时间：2016/10/12 18:28
 */

public class JSONParam {

    //存放JSON参数
    private JSONObject mJsonParam;
    //存放请求数据
    private RequestParams mParam;

    public JSONParam(){
        mJsonParam = new JSONObject();
        mParam = new RequestParams();
    }

    /**
     * 添加json参数
     * @param key 键值
     * @param value 值
     */
    public void putJSONParam(String key,Object value) {
        mJsonParam.put(key,value);
    }

    /**
     * 获取json请求的参数
     * @return JSON参数实例
     */
    public RequestParams getJSONParam() {
        mParam.applicationJson(mJsonParam);
        return mParam;
    }
}
