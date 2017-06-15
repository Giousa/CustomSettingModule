package com.zmm.customproject;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.tw.tw_ble2_module.dao.TwBle2Manager;
import com.tw.tw_ble2_module.dao.TwBle2Service;
import com.tw.tw_common_module.event.BluetoothReadEvent;
import com.tw.tw_common_module.event.BluetoothStatusEvent;
import com.tw.tw_common_module.utils.LogUtils;
import com.tw.tw_common_module.utils.ToastUtils;
import com.tw.tw_setting_module.dao.TwSettingModule;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {


    private Context mContext;
    private final int mOffsetCount = 32768;
    private final int paramInteger = 1000;
    private final int paramByte = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        mContext = getApplicationContext();

        Button setting = (Button) findViewById(R.id.btn_setting);
        Button start = (Button) findViewById(R.id.btn_start);
        Button stop = (Button) findViewById(R.id.btn_stop);
        Button startCollecting = (Button) findViewById(R.id.btn_start_collecting);
        Button stopCollecting = (Button) findViewById(R.id.btn_stop_collecting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(MainActivity.this, TwSettingActivity.class);
//                startActivity(intent);
                TwSettingModule.execute(MainActivity.this);


            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwBle2Service.start(getApplicationContext(),"RG41L170614001","00:0C:BF:14:AA:E0","RG41R170614001","00:0C:BF:14:AA:64");
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwBle2Service.stop(getApplicationContext());
            }
        });

        startCollecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 确保左右蓝牙已经连接的情况下，发送指令开始采集数据
                TwBle2Manager.startCollecting();
            }
        });


        stopCollecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwBle2Manager.stopCollecting();
            }
        });

    }

    @Subscribe
    public void onEventMainThread(BluetoothStatusEvent event) {
        LogUtils.d("onEventMainThread:state = " + event.getState() + "    device = " + event.getDevice());


        if (event.getState() == 3) {
            if (event.getDevice() == 0) {
                LogUtils.d("步态评估 左边蓝牙 连接成功");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.SimpleToast(mContext,"左侧设备 连接成功!!");
                        TwBle2Manager.queryBattery();
                    }
                });
            } else if (event.getDevice() == 1) {
                LogUtils.d("步态评估 右边蓝牙 连接成功");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.SimpleToast(mContext,"右侧设备 连接成功!!");
                        TwBle2Manager.queryBattery();
                    }
                });

            }
        }
    }

    @Subscribe
    public void onEventMainThread(final BluetoothReadEvent event) {

        byte[] bytes = event.getBytes();

        if (event.getDevice() == 0) {
            if ((bytes[0] & 0xFF) == 0x84) {
                parseBattery(0,bytes);
            }else if ((bytes[0] & 0xFF) == 0xDC) {
                parseAngleData(0, bytes);
            }else if ((bytes[0] & 0xFF) == 0xDB) {
                parseVelocityData(0, bytes);
            }

        } else {
            if ((bytes[0] & 0xFF) == 0x84) {
                parseBattery(1,bytes);
            }else if ((bytes[0] & 0xFF) == 0xDC) {
                parseAngleData(1, bytes);
            }else if ((bytes[0] & 0xFF) == 0xDB) {
                parseVelocityData(1, bytes);
            }
        }

    }

    private void parseBattery(int i, byte[] bytes) {
        if(i == 0){
            int amountLeft = dataBattery((bytes[1] & 0xFF), (bytes[2] & 0xFF));
            LogUtils.d("左侧蓝牙电量 : amountLeft = "+amountLeft);

        }else {
            int amountRight = dataBattery((bytes[1] & 0xFF), (bytes[2] & 0xFF));
            LogUtils.d("右侧蓝牙电量 : amountRight = "+amountRight);
        }
    }

    private int dataBattery(int i, int y){
        return i*256+y;
    }

    private void parseAngleData(final int i, final byte[] bytes) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float i0 =  dataQuaternion((bytes[1] & 0xFF), (bytes[2] & 0xFF), (bytes[3] & 0xFF), (bytes[4] & 0xFF));
                float i1 =  dataQuaternion((bytes[5] & 0xFF), (bytes[6] & 0xFF), (bytes[7] & 0xFF), (bytes[8] & 0xFF));
                float i2 =  dataQuaternion((bytes[9] & 0xFF), (bytes[10] & 0xFF), (bytes[11] & 0xFF), (bytes[12] & 0xFF));
                float i3 =  dataQuaternion((bytes[13] & 0xFF), (bytes[14] & 0xFF), (bytes[15] & 0xFF), (bytes[16] & 0xFF));

                LogUtils.d("unity angle i0 = "+i0+",i1 = "+i1+",i2 = "+i2+",i3 = "+i3);
            }
        });

    }

    private float dataQuaternion(int a, int b, int c, int d) {

        float e = (float)a / 64  + (float)b /16384 + (float)c / 4194304 + (float)d/(1 << 30);

        if (e > 2) {
            e -= 4;
        }

        return e;

    }

    private void parseVelocityData(int i, byte[] bytes) {

        double i0 = (bytes[1] & 0xFF) * 16777.216 + (bytes[2] & 0xFF) * 65.536 + (bytes[3] & 0xFF) * 0.256 + (bytes[4] & 0xFF) * 0.001;
        float i1 = (dataCol((bytes[5] & 0xFF), (bytes[6] & 0xFF)) * paramInteger / mOffsetCount);
        float i2 = (dataCol((bytes[7] & 0xFF), (bytes[8] & 0xFF)) * paramInteger / mOffsetCount);
        float i3 = (dataCol((bytes[9] & 0xFF), (bytes[10] & 0xFF)) * paramInteger / mOffsetCount);
        float i4 = (float) (dataCol((bytes[11] & 0xFF), (bytes[12] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
        float i5 = (float) (dataCol((bytes[13] & 0xFF), (bytes[14] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
        float i6 = (float) (dataCol((bytes[15] & 0xFF), (bytes[16] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
        double i7 = (bytes[17] & 0xFF) * 16777.216 + (bytes[18] & 0xFF) * 65.536 + (bytes[19] & 0xFF) * 0.256 + (bytes[20] & 0xFF) * 0.001;
//        double i7 = (double) (((bytes[17] & 0xFF) * 256 * 256 * 256 + (bytes[18] & 0xFF) * 256 * 256 + (bytes[19] & 0xFF) * 256 + (bytes[20] & 0xFF)) / 1000);
        float i8 = (dataCol((bytes[21] & 0xFF), (bytes[22] & 0xFF)) * paramInteger / mOffsetCount);
        float i9 = (dataCol((bytes[23] & 0xFF), (bytes[24] & 0xFF)) * paramInteger / mOffsetCount);
        float i10 = (dataCol((bytes[25] & 0xFF), (bytes[26] & 0xFF)) * paramInteger / mOffsetCount);
        float i11 = (float) (dataCol((bytes[27] & 0xFF), (bytes[28] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
        float i12 = (float) (dataCol((bytes[29] & 0xFF), (bytes[30] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
        float i13 = (float) (dataCol((bytes[31] & 0xFF), (bytes[32] & 0xFF)) * paramByte * 9.8 / mOffsetCount);
//        float i14 = (float) ((bytes[33] & 0xFF) * 256 * 256 * 256 + (bytes[34] & 0xFF) * 256 * 256 + (bytes[35] & 0xFF) * 256 + (bytes[36] & 0xFF));

        LogUtils.d("i0 = "+i0+",i1 = "+i1+",i2 = "+i2+",i3 = "+i3+",i4 = "+i4+",i5 = "+i5+",i6 = "+i6);
        LogUtils.d("i8 = "+i8+",i9 = "+i9+",i10 = "+i10+",i11 = "+i11+",i12 = "+i12+",i13 = "+i13);
    }

    private float dataCol(int i, int y) {

        return (i * 256 + y) > 32767 ? (i * 256 + y - 65536) : (i * 256 + y);
    }
}
