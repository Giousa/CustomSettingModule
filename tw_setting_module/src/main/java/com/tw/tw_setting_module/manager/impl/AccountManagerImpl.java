package com.tw.tw_setting_module.manager.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tw.tw_setting_module.manager.IAccountManager;
import com.tw.tw_setting_module.manager.RequestCallback;
import com.zmm.tw_common_module.utils.okhttp.OkHttpUtils;
import com.zmm.tw_common_module.utils.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class AccountManagerImpl implements IAccountManager {


    private String TAG = IAccountManager.class.getSimpleName();

    /**
     * 登陆
     */
    @Override
    public void login(String name, String pwd, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/user/loginByDoctor";
        Map<String, String> map = new HashMap<>();
        try {
            map.put("loginId", name);
            map.put("password", pwd);
            map.put("user_type", "doctor");
            //map.put("token", "53e7a883a4e14f569860343036709ikm");
        } catch (Exception e) {
            Log.e(TAG, "login error.", e);
        }


        OkHttpUtils.post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                      JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "login.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        } catch (Exception e) {
                            Log.e(TAG, "login.onResponse.", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });

    }

    /**
     * 重置密码
     */
    @Override
    public void resetUserPwd(String userId, String oldPassword, String newPassword, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/user/updatePassword";
        Map<String, String> map = new HashMap<>();
        try {

            map.put("userId", userId);
            map.put("oldPassword", oldPassword);
            map.put("newPassword", newPassword);
        } catch (Exception e) {
            Log.e(TAG, " resetUserPwd error.", e);
        }

        OkHttpUtils.post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, " resetUserPwd.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        } catch (Exception e) {
                            Log.e(TAG, "resetUserPwd.onResponse.", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });
    }

    /**
     * 上传头像
     */
    @Override
    public void uploadUserAvatar(final String useId, final File file, final String filename, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/user/uploadUserAvatar";

        Map<String, String> map = new HashMap<>();
        try {
            map.put("userId", useId);
        } catch (Exception e) {
            Log.e(TAG, "upload headPortrait error.", e);
        }

        OkHttpUtils.post().
                url(url).
                addFile("file", filename, file).
                params(map).
                build().
                execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "upload headPortrait.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        }catch (Exception e) {
                            Log.e(TAG, "upload headPortrait.onResponse", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });
    }

    /**
     * 更新医生昵称
     */
    @Override
    public void updateDoctorInfo(String doctor_id, String doctorName, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/user/updateDoctorInfo";
     //   String url = "http://172.28.6.25:8080/api-webapp/api/user/updateDoctorInfo";
        Map<String, String> map = new HashMap<>();
        try {
            map.put("doctor_id", doctor_id);
            map.put("doctorName", doctorName);
            //map.put("token", "53e7a883a4e14f569860343036709ikm");
        } catch (Exception e) {
            Log.e(TAG, "login error.", e);
        }


        OkHttpUtils.post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "updateDoctorInfo.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        } catch (Exception e) {
                            Log.e(TAG, "updateDoctorInfo.onResponse.", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });

    }

    /**
     * 退出登录
     */
    public void logout(String token, String userId, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/user/logout";

        OkHttpUtils.post()
                .url(url)
                .addParams("userId", userId)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, " logout.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        }catch (Exception e) {
                            Log.e(TAG, "logout.onResponse.", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });
    }


    /**
     * 左右脚原始数据上传
     */
    @Override
    public void addDataFile(final String doctor_id, final String patient_id,final String leftName, final File left_file,final String rightName,  final File right_file, final RequestCallback callBack) {
        String url = "http://test.ricamed.com/api/smartshoes/addDataFile";
        //String url = "http://172.28.6.25:8080/api-webapp/api/smartshoes/addDataFile";
        Map<String, String> map = new HashMap<>();
        try {
            map.put("doctor_id", doctor_id);
            map.put("patient_id", patient_id);

        } catch (Exception e) {
            Log.e(TAG, "addDataFile error.", e);
        }

        OkHttpUtils.post().
                url(url).
                addFile("left_file",leftName, left_file).
                addFile("right_file",rightName, right_file).
                params(map).
                build().
                execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "addDataFile.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        }catch (Exception e) {
                            Log.e(TAG, "addDataFile.onResponse", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });
    }

    /**
     * 报告数据上传
     */
    @Override
    public void addReport( final String data_id, final String doctor_id, final String patient_id, final String result, final RequestCallback callBack)
    {
        String url = "http://test.ricamed.com/api/smartshoes/addReport";
       // String url = "http://172.28.6.25:8080/api-webapp/api/smartshoes/addReport";
        JSONObject body = new JSONObject();
        try {
            body.put("data_id", data_id);
            body.put("doctor_id", doctor_id);
            body.put("patient_id", patient_id);
            body.put("result", result);

        } catch (Exception e) {
            Log.e(TAG, " addReport error.", e);
        }

        OkHttpUtils.post()
                .url(url)
                .addParams("requestData", body.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        JSONObject result = new JSONObject();
                        result.put("code","-1");
                        result.put("message","无法连接到服务器");
                        callBack.onRequestComplete(result);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, " addReport.onResponse: " + response.toString());

                        JSONObject result = null;
                        try {
                            result = JSON.parseObject(response.toString());

                        } catch (Exception e) {
                            Log.e(TAG, "addReport.onResponse.", e);
                        }
                        callBack.onRequestComplete(result);
                    }
                });
    }

}
