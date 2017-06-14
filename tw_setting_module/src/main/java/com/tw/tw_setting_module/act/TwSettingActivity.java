package com.tw.tw_setting_module.act;

import android.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;
import com.zmm.tw_common_module.utils.CommonConfig;
import com.zmm.tw_common_module.utils.SharedPreferencesUtil;

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
    private AlertDialog mQuitDialog;


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
                startActivity(TwTitleActivity.class,false);
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
//                startActivity(TwLogOutActivity.class,false);
                quitDialog();
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

    private void quitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View viewDialog = View.inflate(this, R.layout.item_quit_dialog, null);
        TextView content = (TextView) viewDialog.findViewById(R.id.dialog_content);
        content.setText("是否退出登陆?");
        Button cancel = (Button) viewDialog.findViewById(R.id.dialog_cancel);
        Button confirm = (Button) viewDialog.findViewById(R.id.dialog_confirm);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuitDialog.dismiss();

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuitDialog.dismiss();
                SharedPreferencesUtil.saveBoolean(getApplicationContext(),CommonConfig.LOGIN, false);
                //TODO 退出操作
//                startActivity(LoginActivity.class, true);

            }
        });

        builder.setView(viewDialog);
        mQuitDialog = builder.create();
        mQuitDialog.setCanceledOnTouchOutside(false);
        mQuitDialog.show();
        WindowManager.LayoutParams params = mQuitDialog.getWindow().getAttributes();
        params.width = 700;
        params.height = 300;
        mQuitDialog.getWindow().setBackgroundDrawableResource(R.drawable.tw_shape_corners_grey);
        mQuitDialog.getWindow().setAttributes(params);

    }
}
