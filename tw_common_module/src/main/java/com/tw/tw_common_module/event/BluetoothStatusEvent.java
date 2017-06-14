package com.tw.tw_common_module.event;


/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/30
 * Time:上午9:46
 */

public class BluetoothStatusEvent {

    private int state;
    private int device;

    public BluetoothStatusEvent(int state, int device) {
        this.state = state;
        this.device = device;
    }

    public int getState() {
        return state;
    }

    public int getDevice() {
        return device;
    }
}
