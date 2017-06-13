package com.tw.tw_setting_module.manager;

import java.io.File;

public interface IAccountManager {

    /**
     * 登录接口
     *
     * @param name     帐号
     * @param pwd      密码
     * @param callBack 请求结果回调
     */
    void login(String name, String pwd, RequestCallback callBack);

    /**
     * 用户修改密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     */
    void resetUserPwd(String userId, String oldPassword, String newPassword, RequestCallback callBack);

    /**
     * 用户头像上传请求
     *
     * @param useId
     * @param file
     * @param filename
     */
    void uploadUserAvatar(final String useId, final File file, final String filename, RequestCallback callBack);

    /**
     * 用户信息修改
     *
     * @param doctor_id
     * @param doctorName
     */

     void updateDoctorInfo(String doctor_id, String doctorName, RequestCallback callBack);

    /**
     * 退出登录
     *
     * @param token
     * @param userId
     */
    void logout(String token, String userId, RequestCallback callBack);

    /**
     * 左右脚原始数据上传
     *
     * @param doctor_id
     * @param patient_id
     * @param left_file
     * @param right_file
     */
    void addDataFile(final String doctor_id, final String patient_id, final String leftName, final File left_file, final String rightName, final File right_file, RequestCallback callBack);

    /**
     * 报告数据上传
     *
     * @param data_id
     * @param doctor_id
     * @param patient_id
     * @param result
     */
    void addReport(final String data_id, final String doctor_id, final String patient_id, final String result, RequestCallback callBack);

}
