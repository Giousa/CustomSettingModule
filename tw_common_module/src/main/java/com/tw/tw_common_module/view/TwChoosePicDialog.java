package com.tw.tw_common_module.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tw.tw_common_module.R;

public class TwChoosePicDialog extends Dialog {

    Context context;
    Dialog dialog;
    Dialogcallback dialogcallback;
    TextView choose_cmera,choose_pic,choose_cancle;

    public TwChoosePicDialog(Context context) {
        super(context);
        this.context = context;
        dialog = new Dialog(context, R.style.Choose_Dialog);
        dialog.setContentView(R.layout.common_dialog_choose_pic);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimBottom);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        choose_cmera = (TextView) dialog.findViewById(R.id.tv_getCamera);
        choose_pic = (TextView) dialog.findViewById(R.id.tv_getPic);
        choose_cancle = (TextView) dialog.findViewById(R.id.tv_cancel);
        choose_cmera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogcallback.doGetCamera();
            }
        });
        choose_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogcallback.doGetPic();
            }
        });
        choose_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogcallback.doCancle();
            }
        });
    }

    public interface Dialogcallback{
        void doGetCamera();
        void doGetPic();
        void doCancle();
    }

    public void setDialogCallback(Dialogcallback dialogcallback) {
        this.dialogcallback = dialogcallback;
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    public void dismiss() {
        dialog.dismiss();
    }

}
