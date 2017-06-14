package com.tw.tw_setting_module.act;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;
import com.tw.tw_common_module.utils.ToastUtils;


public class TwAboutActivity extends TwBaseActivity {


    private RelativeLayout mRelCheckUpdate;

    @Override
    protected int getLayoutId() {
        return R.layout.tw_activity_setting_about;
    }

    @Override
    protected void initView() {
        TextView title = (TextView) findViewById(R.id.title_tv_content);
        title.setText(R.string.setting_about);

        mRelCheckUpdate = (RelativeLayout) findViewById(R.id.rl_check_update);
        mRelCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.SimpleToast(getApplicationContext(),"当前已是最新版本");
            }
        });
    }

    @Override
    protected void initData() {
    }
}
