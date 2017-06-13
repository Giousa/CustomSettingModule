package com.zmm.tw_common_module.utils.okhttp.builder;


import com.zmm.tw_common_module.utils.okhttp.OkHttpUtils;
import com.zmm.tw_common_module.utils.okhttp.request.OtherRequest;
import com.zmm.tw_common_module.utils.okhttp.request.RequestCall;

/**
 * Created by zhy on 16/3/2.
 */
public class HeadBuilder extends GetBuilder
{
    @Override
    public RequestCall build()
    {
        return new OtherRequest(null, null, OkHttpUtils.METHOD.HEAD, url, tag, params, headers,id).build();
    }
}
