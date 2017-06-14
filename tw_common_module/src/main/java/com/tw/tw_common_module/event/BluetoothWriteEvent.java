package com.tw.tw_common_module.event;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/30
 * Time:上午9:46
 */

public class BluetoothWriteEvent {

    private byte[] bys;

    public BluetoothWriteEvent(byte[] bys) {
        this.bys = bys;
    }

    public byte[] getBys() {
        return bys;
    }

}
