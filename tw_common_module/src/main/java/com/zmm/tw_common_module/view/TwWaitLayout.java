package com.zmm.tw_common_module.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Description:
 * Author:zhangmengmeng
 * Date:2017/3/20
 * Time:上午10:44
 */

public class TwWaitLayout extends RelativeLayout {

    public TwWaitLayout(Context context) {
        super(context);
    }

    public TwWaitLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        super.onInterceptTouchEvent(e);
        return false;
    }

}