package com.tw.tw_common_module.event;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/4/27
 * Time:上午9:43
 */

public class BluetoothRssiEvent {

    private short rssi;
    private int device;

    public BluetoothRssiEvent(short rssi, int device) {
        this.rssi = rssi;
        this.device = device;
    }

    public short getRssi() {
        return rssi;
    }

    public int getDevice() {
        return device;
    }
}
