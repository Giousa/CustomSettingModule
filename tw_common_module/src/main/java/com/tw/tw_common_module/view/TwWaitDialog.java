package com.tw.tw_common_module.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tw.tw_common_module.R;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/20
 * Time:上午10:36
 */

public class TwWaitDialog
{
    private static final String TAG = TwWaitDialog.class.getSimpleName();

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private Dialog mDialog;

    private TextView mDialogText;

    private ViewGroup mParentView;

    private TwWaitLayout mLayout;

    public TwWaitDialog(Context context)
    {
        init(context);
    }

    public TwWaitDialog(Context context, ViewGroup parent)
    {
        init(context);
        mParentView = parent;
    }

    public void setParentView(ViewGroup parent)
    {
        mParentView = parent;
    }

    public void show()
    {
        show(null);
    }

    public void show(String msg)
    {
        show(mParentView, msg);
    }

    public void show(ViewGroup parent, String msg)
    {
        mParentView = parent;
        if (mParentView != null)
        {
            addDialog(mParentView);
        }
        else
        {
            if (mDialog == null)
            {
                mDialog = initDialog();
            }
            if (mDialog != null && !mDialog.isShowing())
            {
                mDialog.show();
            }
        }
        mDialogText.setText(msg);
    }

    public void dismiss()
    {
        if (mParentView != null)
        {
            if (mLayout != null && mLayout.isShown())
            {
                mParentView.removeView(mLayout);
            }
        }
        else
        {
            if (mDialog != null && mDialog.isShowing())
            {
                try
                {
                    mDialog.dismiss();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
    }

    public boolean isShown()
    {
        if (mParentView != null)
        {
            if (mLayout != null)
            {
                return mLayout.isShown();
            }
        }
        else
        {
            if (mDialog != null)
            {
                return mDialog.isShowing();
            }
        }
        return false;
    }

    private void init(Context context)
    {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private View initView(ViewGroup parent)
    {
        View layout = (ViewGroup) mLayoutInflater.inflate(R.layout.common_dialog_wait,
                parent);
        layout.setBackgroundResource(R.drawable.common_single_color);
        mDialogText = (TextView) layout.findViewById(R.id.waitName);
        return layout;
    }

    private Dialog initDialog()
    {
        View view = initView(null);
        final Dialog alertDialog = new Dialog(mContext,
                R.style.translucent_dialog);
        alertDialog.setContentView(view);
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }

    //tangzepeng modify on 2014-06-05 20:50
    private void addDialog(ViewGroup parent)
    {
        mLayout = new TwWaitLayout(mContext);
        mLayout.setBackgroundColor(Color.parseColor("#40000000"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300,
                300);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        View view = initView(null);
        mLayout.addView(view, params);
        parent.addView(mLayout,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
    }

    private boolean check()
    {
        if (null == mDialog)
        {
            return false;
        }

        return true;
    }

    /**
     * Set a listener to be invoked when the dialog is canceled.
     *
     * <p>This will only be invoked when the dialog is canceled.
     * Cancel events alone will not capture all ways that
     * the dialog might be dismissed. </p>
     * @param listener The {@link DialogInterface.OnCancelListener} to use.
     */
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {

        if (!check())
        {
            return;
        }

        mDialog.setOnCancelListener(listener);
    }

    /**
     * Sets whether this dialog is cancelable with the
     * {@link KeyEvent#KEYCODE_BACK BACK} key.
     */
    public void setCancelable(boolean flag) {
        if (!check())
        {
            return;
        }
        mDialog.setCancelable(flag);
    }

    /**
     * Sets whether this dialog is canceled when touched outside the window's
     * bounds. If setting to true, the dialog is set to be cancelable if not
     * already set.
     *
     * @param cancel Whether the dialog should be canceled when touched outside
     *            the window.
     */
    public void setCanceledOnTouchOutside(boolean cancel) {
        if (!check())
        {
            return;
        }

        mDialog.setCanceledOnTouchOutside(cancel);
    }
}
