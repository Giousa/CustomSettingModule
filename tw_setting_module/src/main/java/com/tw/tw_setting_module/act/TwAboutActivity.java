package com.tw.tw_setting_module.act;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;


public class TwAboutActivity extends TwBaseActivity {


    @Override
    protected int getLayoutId() {
        return R.layout.tw_activity_setting_about;
    }

    @Override
    protected void initView() {
        TextView title = (TextView) findViewById(R.id.title_tv_content);
        title.setText(R.string.setting_about);

        Button quit = (Button) findViewById(R.id.btn_quit);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwSettingActivity.class,true);
            }
        });
    }

    @Override
    protected void initData() {

    }
}
