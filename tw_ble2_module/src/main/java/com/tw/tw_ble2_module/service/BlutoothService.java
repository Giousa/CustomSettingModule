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
import android.support.annotation.Nullable;

import com.tw.tw_ble2_module.ble.BluetoothChatService;
import com.tw.tw_common_module.event.BluetoothReadEvent;
import com.tw.tw_common_module.event.BluetoothRssiEvent;
import com.tw.tw_common_module.event.BluetoothStatusEvent;
import com.tw.tw_common_module.event.BluetoothWriteEvent;
import com.tw.tw_common_module.utils.CommonConfig;
import com.tw.tw_common_module.utils.LogUtils;

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

public class BlutoothService extends Service implements BluetoothChatService.OnReadBluetoothListener, BluetoothChatService.OnBluetoothStatusMsgListener, BluetoothChatService.OnConnectedListener {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatServiceLeft = null;
    private BluetoothChatService mChatServiceRight = null;
    private boolean isLeft = false;
    private boolean isRight = false;
    private long mStartTime;//开始时间
    private boolean isDestrory = false;
    private String mFileName;
    private BluetoothSocket mBluetoothSocketLeft;
    private BluetoothSocket mBluetoothSocketRight;
    private int count = 0;
    private boolean isFirst = false;
    private String mBleNameLeft;
    private String mBleNameRight;
    private String mBleAddressLeft;
    private String mBleAddressRight;
    private CountDownTimer mCountDownTimer;

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            LogUtils.d("count = "+count++);

            if(!isDestrory){
                if(!isLeft || !isRight){
                    reConnectDevice();
                }
            }

            handler.postDelayed(this, 5000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("BlutoothService onCreate");
        EventBus.getDefault().register(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!isFirst && intent != null){

            mBleNameLeft = intent.getStringExtra(CommonConfig.NAME_LEFT);
            mBleNameRight = intent.getStringExtra(CommonConfig.NAME_RIGHT);
            mBleAddressLeft = intent.getStringExtra(CommonConfig.ADDRESS_LEFT);
            mBleAddressRight = intent.getStringExtra(CommonConfig.ADDRESS_RIGHT);

            LogUtils.d("mBleNameLeft = "+mBleNameLeft+",mBleNameRight = "+mBleNameRight);
            LogUtils.d("mBleAddressLeft = "+mBleAddressLeft+",mBleAddressRight = "+mBleAddressRight);

            mCountDownTimer = new CountDownTimer(1000000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long l = millisUntilFinished / 1000;
                    LogUtils.d("剩余时间 time = " + l);
                }

                @Override
                public void onFinish() {

                }
            }.start();

            if(mBleNameLeft != null && mBleNameRight != null){
                initBluetoothData();
                handler.postDelayed(runnable, 2000);
            }

            isFirst = true;
        }


        return START_STICKY;
    }

    private void initBluetoothData() {

        startDate();

        initBluetooth();

        reConnectDevice();

    }

    private void startDate() {
        mStartTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(mStartTime);
        String startDate = format.format(date);
        LogUtils.d("开始测试日期: startDate = "+startDate);

        SimpleDateFormat format2 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date2 = new Date(mStartTime);
        mFileName = format2.format(date2);
    }

    private void initBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mChatServiceLeft == null) {
            mChatServiceLeft = new BluetoothChatService();
            mChatServiceLeft.start();
            mChatServiceLeft.setOnReadBluetoothListener(this);
            mChatServiceLeft.setOnBluetoothStatusMsgListener(this);
            mChatServiceLeft.setOnConnectedListener(this);
        }

        if (mChatServiceRight == null) {
            mChatServiceRight = new BluetoothChatService();
            mChatServiceRight.start();
            mChatServiceRight.setOnReadBluetoothListener(this);
            mChatServiceRight.setOnBluetoothStatusMsgListener(this);
            mChatServiceRight.setOnConnectedListener(this);
        }

        registerBluetooth();
    }



    private void reConnectDevice(){

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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("广播开启:");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                LogUtils.d("device 设备名称 = " + device.getName() + ",设备地址 = " + device.getAddress()+
                        ",信号强度 = "+rssi);

                if (mBleNameLeft.equals(device.getName())) {
                    if(!isLeft){
                        mChatServiceLeft.connect(device);
                        LogUtils.d("左边蓝牙连接中...");
                        EventBus.getDefault().post(new BluetoothRssiEvent(rssi,0));
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }

                }

                if(mBleNameRight.equals(device.getName())){
                    if(!isRight){
                        mChatServiceRight.connect(device);
                        LogUtils.d("右边蓝牙连接中...");
                        EventBus.getDefault().post(new BluetoothRssiEvent(rssi,1));
                        if (mBluetoothAdapter.isDiscovering()) {
                            mBluetoothAdapter.cancelDiscovery();
                        }
                    }

                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                LogUtils.d("广播结束:");
            }
        }
    };


    @Override
    public void onConnected(BluetoothSocket socket, BluetoothDevice device) {
        LogUtils.d("回调接口 name = "+device.getName()+",address = "+device.getAddress()+",socket = "+socket);
        if(mBleAddressLeft.equals(device.getAddress())){
            mBluetoothSocketLeft = socket;
        }else if(mBleAddressRight.equals(device.getAddress())){
            mBluetoothSocketRight = socket;
        }
    }

    @Override
    public void onBluetoothStatusMsg(int state, String msg) {
        LogUtils.d("蓝牙状态:state = " + state + "    msg = " + msg);

        if(isDestrory){
            return;
        }

        switch (state) {
            case 0:
                LogUtils.d("蓝牙状态 空  " + msg);
                break;

            case 1:
                LogUtils.d("蓝牙状态 开始  " + msg);
                break;

            case 2:
                LogUtils.d("蓝牙状态 连接中  " + msg);
                if(mBleAddressLeft.equals(msg)){
                    LogUtils.d("左边蓝牙 正在连接...");
                }else if(mBleAddressRight.equals(msg)){
                    LogUtils.d("右边蓝牙 正在连接...");
                }
                break;

            case 3:
                LogUtils.d("蓝牙状态 已连接  " + msg);
                if(mBleAddressLeft.equals(msg)){
                    isLeft = true;
                    LogUtils.d("左边蓝牙 连接成功");
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,0));
                }else if(mBleAddressRight.equals(msg)){
                    isRight = true;
                    LogUtils.d("右边蓝牙 连接成功");
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,1));

                }

                break;

            case 4:
                LogUtils.d("蓝牙状态 连接失败  " + msg);
                if(mBleAddressLeft.equals(msg)){
                    isLeft = false;
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,0));
                }else if(mBleAddressRight.equals(msg)){
                    isRight = false;
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,1));
                }
                if(!isDestrory){
                    reConnectDevice();
                }
                break;

            case 5:
                LogUtils.d("蓝牙状态 设备离线  " + msg);

                if(mBleAddressLeft.equals(msg)){
                    isLeft = false;
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,0));
                }else if(mBleAddressRight.equals(msg)){
                    isRight = false;
                    EventBus.getDefault().post(new BluetoothStatusEvent(state,1));
                }
                if(!isDestrory){
                    reConnectDevice();
                }
                break;
        }
    }

    @Override
    public void onReadBluetooth(BluetoothSocket socket, byte[] bytes) {
        if(socket.getRemoteDevice().getAddress().equals(mBleAddressLeft)){
            EventBus.getDefault().post(new BluetoothReadEvent(0,bytes));
        }else if(socket.getRemoteDevice().getAddress().equals(mBleAddressRight)){
            EventBus.getDefault().post(new BluetoothReadEvent(1,bytes));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestrory = true;

        if(mCountDownTimer != null){
            mCountDownTimer.cancel();
        }
        // Unregister broadcast listeners
        try {
            if(mReceiver != null){
                unregisterReceiver(mReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        handler.removeCallbacks(runnable);
        if (mChatServiceLeft != null) mChatServiceLeft.stop();
        if (mChatServiceRight != null) mChatServiceRight.stop();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }

        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void onEventMainThread(BluetoothWriteEvent event) {

        if(isDestrory){
            return;
        }

        byte[] bys = event.getBys();
        String bytes = "Service onEventMainThread收到了消息：" + Arrays.toString(bys);
        LogUtils.d(bytes);
        if((bys[4] & 0xFF) == 0x80){
            start(bys);
        }else if((bys[4] & 0xFF) == 0x81){
            stop(bys);
        }else if((bys[4] & 0xFF) == 0x82){

        }else if((bys[4] & 0xFF) == 0x83){

        }else if((bys[4] & 0xFF) == 0x84){
            amount(bys);
        }else if((bys[4] & 0xFF) == 0x87){
        }

    }

    private void amount(byte[] bys) {
        isDestrory = false;
        sendDataToBLE(bys);
    }

    private void stop(byte[] bys) {
        isDestrory = true;
        sendDataToBLE(bys);
    }

    private void start(byte[] bys) {
        isDestrory = false;
        sendDataToBLE(bys);
    }

    private void sendDataToBLE(byte[] data){
        if(mBluetoothSocketLeft != null){
            OutputStream os = null;
            try {
                os = mBluetoothSocketLeft.getOutputStream();
                os.write(data);
                os.flush();
            } catch (IOException e) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        }

        if(mBluetoothSocketRight != null){
            OutputStream os = null;
            try {
                os = mBluetoothSocketRight.getOutputStream();
                os.write(data);
                os.flush();
            } catch (IOException e) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
