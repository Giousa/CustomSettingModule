package com.tw.tw_setting_module.manager;

import com.alibaba.fastjson.JSONObject;

public interface RequestCallback {
    void onRequestComplete(JSONObject result);
}
