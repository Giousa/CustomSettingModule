package com.tw.tw_ble2_module.dao;

import android.content.Context;
import android.content.Intent;

import com.tw.tw_ble2_module.service.BlutoothService;
import com.tw.tw_common_module.utils.CommonConfig;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/14
 * Time:下午4:43
 */

public class StartBle2Module {

    public static void start(Context context,String nameLeft, String addressLeft, String nameRight, String addressRight){
        Intent startIntent = new Intent(context, BlutoothService.class);
        startIntent.putExtra(CommonConfig.NAME_LEFT,nameLeft);
        startIntent.putExtra(CommonConfig.NAME_RIGHT,nameRight);
        startIntent.putExtra(CommonConfig.ADDRESS_LEFT,addressLeft);
        startIntent.putExtra(CommonConfig.ADDRESS_RIGHT,addressRight);
        context.startService(startIntent);
    }

    public static void stop(Context context){
        Intent stopIntent = new Intent(context, BlutoothService.class);
        context.stopService(stopIntent);
    }
}
