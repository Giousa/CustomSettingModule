package com.tw.tw_setting_module.dao;

import android.content.Context;
import android.content.Intent;

import com.tw.tw_setting_module.act.TwSettingActivity;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/14
 * Time:下午1:44
 */

public class StartSettingModule {

    public static void execute(Context context){
        Intent intent = new Intent(context, TwSettingActivity.class);
        context.startActivity(intent);
    }
}
