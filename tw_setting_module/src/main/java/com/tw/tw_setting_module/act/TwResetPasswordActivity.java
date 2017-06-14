package com.tw.tw_setting_module.act;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;
import com.tw.tw_setting_module.manager.IAccountManager;
import com.tw.tw_setting_module.manager.RequestCallback;
import com.tw.tw_setting_module.manager.impl.AccountManagerImpl;
import com.tw.tw_common_module.utils.CommonConfig;
import com.tw.tw_common_module.utils.DigestUtils;
import com.tw.tw_common_module.utils.LogUtils;
import com.tw.tw_common_module.utils.SharedPreferencesUtil;
import com.tw.tw_common_module.utils.ToastUtils;


public class TwResetPasswordActivity extends TwBaseActivity {

    private ImageView mTitleIvIcon;
    private EditText etOldPassword,etNewPassword,etConfirmPassword;
    private Button mConfirm;
    private Context mContext;

    @Override
    protected int getLayoutId() {
        return R.layout.tw_activity_setting_password;
    }

    @Override
    protected void initView() {
        mContext = getApplicationContext();
        TextView title = (TextView) findViewById(R.id.title_tv_content);
        title.setText(R.string.setting_password);

        mTitleIvIcon = (ImageView) findViewById(R.id.title_iv_icon);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        mConfirm = (Button) findViewById(R.id.bt_confirm);

        etConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    reset(etOldPassword.getText().toString().trim(),etNewPassword.getText().toString().trim(),etConfirmPassword.getText().toString().trim());
                }
                return true;
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset(etOldPassword.getText().toString().trim(),etNewPassword.getText().toString().trim(),etConfirmPassword.getText().toString().trim());
            }
        });

    }

    private void reset(String oldpwd,String newpwd,String pwd) {
        if(checkEdit(oldpwd, newpwd, pwd))
        {
            IAccountManager mIAccountManager = new AccountManagerImpl();
            showDialog("修改密码中");
            mIAccountManager.resetUserPwd(SharedPreferencesUtil.getString(mContext,CommonConfig.USER_ID,""), DigestUtils.encryptPassword(oldpwd, "gongjinkey20170106123020123"), DigestUtils.encryptPassword(newpwd, "gongjinkey20170106123020123"), new RequestCallback() {
                @Override
                public void onRequestComplete(JSONObject result) {
                    LogUtils.d("onRequestComplete:"+result.toString());
                    dismissDialog();
                    if((result.get("code")).equals("2000"))
                    {
                        finish();
                    }
                    else
                    {
                        ToastUtils.SimpleToast(mContext,(String) result.get("message"));
                    }
                }
            });
        }

    }

    public boolean checkEdit(String oldpwd, String newpwd, String pwd) {
        if (TextUtils.isEmpty(oldpwd))
        {
            ToastUtils.SimpleToast(mContext,"请输入旧密码");
            return false;
        }

        if (TextUtils.isEmpty(newpwd))
        {
            ToastUtils.SimpleToast(mContext,"请输入新密码");
            return false;
        }
        if (TextUtils.isEmpty(pwd))
        {
            ToastUtils.SimpleToast(mContext,"请再次输入新密码");

            return false;
        }

        if(!newpwd.equals(pwd))
        {
            ToastUtils.SimpleToast(mContext,"两次输入密码不一致");
            etConfirmPassword.setText("");
            etNewPassword.setText("");
            return false;
        }
        if (newpwd.length() >= 6 && newpwd.length() <= 16) {
            return true;
        } else {
            ToastUtils.SimpleToast(mContext,"请输入6-16位数字或者字母");
            return false;
        }
    }


    @Override
    protected void initData() {

    }
}
