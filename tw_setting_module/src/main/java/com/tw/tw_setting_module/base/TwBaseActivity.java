package com.tw.tw_setting_module.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.tw.tw_setting_module.R;
import com.zmm.tw_common_module.view.TwWaitDialog;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/8
 * Time:上午10:42
 */

public abstract class TwBaseActivity extends AppCompatActivity {

    private ImageView mQuit;
    private TwWaitDialog mWaitDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mWaitDialog = new TwWaitDialog(this);
        initView();
        initData();
        dealtCommonBtn();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected void startActivity(Class activity, boolean finish) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        if (finish) {
            finish();
        }
    }

    private void dealtCommonBtn() {
        mQuit = (ImageView) findViewById(R.id.title_iv_quit);
        if(mQuit != null){
            mQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            });
        }
    }

    /**
     * 开启进度条
     */
    protected void showDialog(String title) {
        if (mWaitDialog.isShown()) {
        } else {
            mWaitDialog.show(title);
        }
    }

    /**
     * 隐藏进度条
     */
    protected void dismissDialog() {
        if (mWaitDialog.isShown()){
            mWaitDialog.dismiss();
        }
    }

}
