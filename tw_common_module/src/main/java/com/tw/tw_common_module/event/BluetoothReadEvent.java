package com.tw.tw_common_module.event;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/30
 * Time:上午9:46
 */

public class BluetoothReadEvent {

    private int device;
    private byte[] bytes;

    public BluetoothReadEvent(int device, byte[] bytes) {
        this.device = device;
        this.bytes = bytes;
    }

    public int getDevice() {
        return device;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
