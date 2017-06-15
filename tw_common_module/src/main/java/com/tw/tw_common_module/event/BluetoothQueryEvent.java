package com.tw.tw_common_module.event;


/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/30
 * Time:上午9:46
 */

public class BluetoothQueryEvent {

    private String name;
    private String address;

    public BluetoothQueryEvent(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
