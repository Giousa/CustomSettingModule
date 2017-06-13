package com.tw.tw_setting_module.act;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/6/8
 * Time:上午10:42
 */

public class TwSettingActivity extends TwBaseActivity {

    private final String TAG = TwSettingActivity.class.getSimpleName();
    private RelativeLayout mUserEdit,mResetPassword,mReportTitle,mAbout,mLogout;
    private ImageView mTitleQuit;


    @Override
    protected int getLayoutId() {
        return R.layout.tw_activity_setting;
    }

    @Override
    protected void initView() {
        mUserEdit = (RelativeLayout) findViewById(R.id.rl_user_edit);
        mResetPassword = (RelativeLayout) findViewById(R.id.rl_reset_password);
        mReportTitle = (RelativeLayout) findViewById(R.id.rl_report_title);
        mAbout = (RelativeLayout) findViewById(R.id.rl_about);
        mLogout = (RelativeLayout) findViewById(R.id.rl_logout);
        mTitleQuit = (ImageView) findViewById(R.id.title_iv_quit);

//        mUserEdit.setOnClickListener(this);
//        mResetPassword.setOnClickListener(this);
//        mReportTitle.setOnClickListener(this);
//        mAbout.setOnClickListener(this);
//        mLogout.setOnClickListener(this);

        mUserEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwUserEditActivity.class,false);
            }
        });
        mResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwResetPasswordActivity.class,false);
            }
        });
        mReportTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwLogoActivity.class,false);
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwAboutActivity.class,false);
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(TwLogOutActivity.class,false);
            }
        });

        mTitleQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.rl_user_edit:
//                Log.d(TAG,"rl_user_edit");
//                startActivity(TwUserEditActivity.class,false);
//                break;
//
//            case R.id.rl_reset_password:
//                Log.d(TAG,"rl_reset_password");
//                startActivity(TwResetPasswordActivity.class,false);
//
//                break;
//
//            case R.id.rl_report_title:
//                Log.d(TAG,"rl_report_title");
//                startActivity(TwLogoActivity.class,false);
//
//                break;
//
//            case R.id.rl_about:
//                Log.d(TAG,"rl_about");
//                startActivity(TwAboutActivity.class,false);
//
//                break;
//
//            case R.id.rl_logout:
//                Log.d(TAG,"rl_logout");
//                startActivity(TwLogOutActivity.class,false);
//
//                break;
//
//        }
//    }
}
