package com.zmm.tw_common_module.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/17
 * Time:下午1:28
 */

public class ToastUtils {

    private static Toast toast;

    public static void SimpleToast(Context context,String string){
        if (toast==null){
            toast=Toast.makeText(context, string, Toast.LENGTH_SHORT);
        }else{
            toast.setText(string);
        }
        toast.show();
    }
}
