package com.tw.tw_ble2_module.dao;

import com.tw.tw_common_module.event.BluetoothWriteEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/15
 * Time:上午9:36
 */

public class TwBle2Manager {

    private static byte[] mWrite = new byte[5];
    private static byte[] mStart = new byte[6];


    public static void startCollecting(){
        mStart[0] = (byte) 0xA5;
        mStart[1] = 0x20;
        mStart[2] = 0x21;
        mStart[3] = 0x02;
        mStart[4] = (byte) 0x80;
        mStart[5] = 0x01;
        EventBus.getDefault().post(new BluetoothWriteEvent(mStart));
    }

    public static void stopCollecting(){
        mWrite[0] = (byte) 0xA5;
        mWrite[1] = 0x20;
        mWrite[2] = 0x21;
        mWrite[3] = 0x01;
        mWrite[4] = (byte) 0x81;
        EventBus.getDefault().post(new BluetoothWriteEvent(mWrite));
    }

    public static void queryBattery(){
        mWrite[0] = (byte) 0xA5;
        mWrite[1] = 0x20;
        mWrite[2] = 0x21;
        mWrite[3] = 0x01;
        mWrite[4] = (byte) 0x84;
        EventBus.getDefault().post(new BluetoothWriteEvent(mWrite));
    }
}
