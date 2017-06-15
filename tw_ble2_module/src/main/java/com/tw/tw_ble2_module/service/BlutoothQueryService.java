package com.tw.tw_ble2_module.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.tw.tw_ble2_module.ble.BluetoothChatService;
import com.tw.tw_common_module.event.BluetoothQueryEvent;
import com.tw.tw_common_module.event.BluetoothReadEvent;
import com.tw.tw_common_module.event.BluetoothRssiEvent;
import com.tw.tw_common_module.event.BluetoothStatusEvent;
import com.tw.tw_common_module.event.BluetoothWriteEvent;
import com.tw.tw_common_module.utils.CommonConfig;
import com.tw.tw_common_module.utils.LogUtils;
import com.tw.tw_common_module.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Description: 蓝牙模块，在开启服务前，要确保蓝牙已经打开
 * Author:zhangmengmeng
 * Date:2017/3/29
 * Time:下午5:53
 */

public class BlutoothQueryService extends Service {

    private static BluetoothAdapter mBluetoothAdapter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("BlutoothQueryService 开启");
//        EventBus.getDefault().register(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initBluetooth();

        return START_STICKY;
    }

    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            ToastUtils.SimpleToast(getApplicationContext(),"蓝牙不支持");
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            ToastUtils.SimpleToast(getApplicationContext(),"蓝牙未打开");
            return;
        }

        registerBluetooth();

        scanLeDevice();
    }

    private void registerBluetooth() {
        //当发现一个新的蓝牙设备时注册广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        //当搜索完毕后注册广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

    }

    private void scanLeDevice() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("广播开启:");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                String deviceName = device.getName();
                String address = device.getAddress();
                LogUtils.d("device 设备名称 = " + deviceName + ",设备地址 = " + device.getAddress());

                if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(address)) {
                    EventBus.getDefault().post(new BluetoothQueryEvent(deviceName,address));
                }


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtils.d("广播结束:");
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }
}
