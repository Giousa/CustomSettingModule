package com.zmm.customproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tw.tw_ble2_module.dao.StartBle2Module;
import com.tw.tw_setting_module.dao.StartSettingModule;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button setting = (Button) findViewById(R.id.btn_setting);
        Button start = (Button) findViewById(R.id.btn_start);
        Button stop = (Button) findViewById(R.id.btn_stop);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent =new Intent(MainActivity.this, TwSettingActivity.class);
//                startActivity(intent);
                StartSettingModule.execute(MainActivity.this);


            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartBle2Module.start(getApplicationContext(),"left01","leftaddress01","right99","rightaddress999");
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartBle2Module.stop(getApplicationContext());
            }
        });

    }
}
