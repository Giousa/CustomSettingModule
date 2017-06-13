package com.tw.tw_setting_module.act;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.tw.tw_setting_module.R;
import com.tw.tw_setting_module.base.TwBaseActivity;
import com.tw.tw_setting_module.manager.IAccountManager;
import com.tw.tw_setting_module.manager.RequestCallback;
import com.tw.tw_setting_module.manager.impl.AccountManagerImpl;
import com.zmm.tw_common_module.utils.CommonConfig;
import com.zmm.tw_common_module.utils.SharedPreferencesUtil;
import com.zmm.tw_common_module.utils.ToastUtils;
import com.zmm.tw_common_module.view.TwChoosePicDialog;
import com.zmm.tw_common_module.view.TwSettingCircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TwUserEditActivity extends TwBaseActivity {

    private TextView mTitleTvContent,mTitleTvName,mUsername;
    private ImageView mTitleIvIcon;
    private TwSettingCircleImageView userIcon;
    private AlertDialog mAlertDialog;
    private EditText renameEdit;
    private String imgName = "";
    private File tempFile;
    private String userId;

    private static final int PHOTO_WITH_DATA = 100;
    private static final int PHOTO_WITH_CAMERA = 101;
    private static final int PHOTO_REQUEST_CUT = 102;
    //声明常量权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private RelativeLayout mUserIconRel;
    private RelativeLayout mUserNameRel;

    private Context mContext;

    @Override
    protected int getLayoutId() {
        return R.layout.tw_activity_setting_user;
    }

    @Override
    protected void initView() {
        mContext = getApplicationContext();
        mTitleTvContent = (TextView) findViewById(R.id.title_tv_content);
        mTitleTvContent.setText(R.string.setting_user);
        mTitleTvName = (TextView) findViewById(R.id.title_tv_name);
        mUsername = (TextView) findViewById(R.id.tv_username);
        mTitleIvIcon = (ImageView) findViewById(R.id.title_iv_icon);
        userIcon = (TwSettingCircleImageView) findViewById(R.id.user_icon);
        mUserIconRel = (RelativeLayout) findViewById(R.id.rl_user_icon);
        mUserNameRel = (RelativeLayout) findViewById(R.id.rl_username);

        userId = SharedPreferencesUtil.getString(mContext, CommonConfig.USER_ID, "");

        if(TextUtils.isEmpty(SharedPreferencesUtil.getString(mContext, userId+"head","")))
        {
            Glide.with(mContext).load(R.drawable.tw_set_home_pic_icon_n).into(this.userIcon);
        }
        else
        {
            File file = new File(SharedPreferencesUtil.getString(mContext,userId+"head",""));
            Glide.with(mContext).load(file).into(this.userIcon);
        }

        mUsername.setText(SharedPreferencesUtil.getString(mContext,userId+"name",""));
    }

    @Override
    protected void initData() {
        mUserIconRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPersimisson();
            }
        });

        mUserNameRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rename();
            }
        });
    }

    private void getPersimisson() {
        // 是否添加权限
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return;
        }
        openDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            getPersimisson();
        }
    }

    private void openDialog() {
        final TwChoosePicDialog chooseDialog = new TwChoosePicDialog(this);
        chooseDialog.show();
        chooseDialog.setDialogCallback(new TwChoosePicDialog.Dialogcallback() {
            @Override
            public void doGetCamera() {
                doTakePhoto();
                chooseDialog.dismiss();
            }

            @Override
            public void doGetPic() {
                doPickPhotoFromGallery();
                chooseDialog.dismiss();
            }

            @Override
            public void doCancle() {
                chooseDialog.dismiss();
            }
        });
    }

    /**
     * 从相册获取图片
     **/
    private void doPickPhotoFromGallery() {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        Uri imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_WITH_DATA);
    }

    /**
     * 拍照获取相片
     **/
    private void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机

        tempFile = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        Uri imageUri = Uri.fromFile(tempFile);
        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //直接使用，没有缩小
        startActivityForResult(intent, PHOTO_WITH_CAMERA);  //用户点击了从相机获取
    }

    /**
     * 创建图片不同的文件名
     **/
    private String createPhotoFileName() {
        String fileName = "";
        Date date = new Date(System.currentTimeMillis());  //系统当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + ".jpg";
        return fileName;
    }

    /**
     * 保存图片到本应用下
     **/
    public void saveMyBitmap(Bitmap mBitmap) {
        File f = new File(Environment.getExternalStorageDirectory().getPath(),
                imgName);
        try {
            f.createNewFile();
        } catch (IOException e) {
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }

    /**
     * 获取SD卡上的图片路径
     *
     * @return
     */
    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 回收图片资源
     *
     * @param bitmap
     */
    public void recycleResource(Bitmap bitmap) {
        if (bitmap != null) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        }
    }

    /**
     * You will receive this call immediately before onResume() when your activity is re-starting.
     **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {  //返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA: {//拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) { //是否有SD卡
                        crop(Uri.fromFile(tempFile));
                    } else {
                        showDialog("没有SD卡");
                    }
                    break;
                }
                case PHOTO_WITH_DATA: {//从图库中选择图片
                    if (data != null) {
                        crop(data.getData());
                    }
                    break;
                }
                case PHOTO_REQUEST_CUT: {
                    final Bitmap bitmap = data.getParcelableExtra("data");
                    imgName = createPhotoFileName();
                    saveMyBitmap(bitmap);
                    recycleResource(bitmap);
                    File file = new File(getSDPath() + "/" + imgName);
                    Glide.with(this).load(file).into(userIcon);
                    // uplodHeadPortrait(file,imgName);
                    SharedPreferencesUtil.saveString(getApplicationContext(),userId+"head",getSDPath() + "/" + imgName);
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }



    private void rename() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        View view = View.inflate(this, R.layout.tw_item_device_dialog, null);
        renameEdit = (EditText) view.findViewById(R.id.et_device_rename);
        TextView title = (TextView) view.findViewById(R.id.pop_title);
        title.setText("修改昵称");
        Button cancel = (Button) view.findViewById(R.id.btn_device_cancel);
        Button confirm = (Button) view.findViewById(R.id.btn_device_confirm);

        String oldName = SharedPreferencesUtil.getString(getApplicationContext(),userId + "name", "");
        if(TextUtils.isEmpty(oldName)){
            renameEdit.setHint("请输入昵称");
        }else {
            renameEdit.setText(oldName);
            renameEdit.setSelection(oldName.length());

        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserName();
            }
        });

        renameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                saveUserName();

                return true;
            }
        });

        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.show();
        WindowManager.LayoutParams params = mAlertDialog.getWindow().getAttributes();
        params.width = 800;
        params.height = 400;
        mAlertDialog.getWindow().setBackgroundDrawableResource(R.drawable.tw_shape_corners_grey);
        mAlertDialog.getWindow().setAttributes(params);
    }

    private void saveUserName(){
        if (TextUtils.isEmpty(renameEdit.getText())) {
            ToastUtils.SimpleToast(mContext,"昵称不能为空!");
        } else {
            String newName = renameEdit.getText().toString().trim();
            // updateUsername(newName);
            mUsername.setText(newName);
            SharedPreferencesUtil.saveString(getApplicationContext(),userId+"name",newName);
            mAlertDialog.dismiss();
        }
    }

    private void updateUsername(final String name) {
        IAccountManager mIAccountManager = new AccountManagerImpl();
        showDialog("修改昵称");
        mIAccountManager.updateDoctorInfo(SharedPreferencesUtil.getString(mContext,CommonConfig.USER_ID, " "), name, new RequestCallback() {
            @Override
            public void onRequestComplete(JSONObject result) {
                dismissDialog();
                if(((String)result.get("code")).equals("2000"))
                {
                    ToastUtils.SimpleToast(mContext,(String) result.get("修改成功"));
                    mUsername.setText(name);
                    SharedPreferencesUtil.saveString(mContext,CommonConfig.USER_NAME,name);
                }
                else
                {
                    ToastUtils.SimpleToast(mContext,(String) result.get("message"));
                }
            }
        });
    }

    /**
     * 上传头像
     */
    private void uplodHeadPortrait(File file, String filename) {
        IAccountManager mIAccountManager = new AccountManagerImpl();
        showDialog("上传头像中");
        mIAccountManager.uploadUserAvatar(SharedPreferencesUtil.getString(mContext,CommonConfig.USER_ID, " "), file, filename, new RequestCallback() {
            @Override
            public void onRequestComplete(JSONObject result) {
                dismissDialog();
                if(((String)result.get("code")).equals("2000"))
                {
                    ToastUtils.SimpleToast(mContext,(String) result.get("上传头像成功"));
                    Glide.with(mContext).load(result.getString("data")).into(userIcon);
                    SharedPreferencesUtil.saveString(mContext,CommonConfig.USER_ICON,result.getString("data"));
                }
                else
                {
                    ToastUtils.SimpleToast(mContext,(String) result.get("message"));
                }
            }
        });
    }
}
