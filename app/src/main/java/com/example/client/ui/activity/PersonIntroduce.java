package com.example.client.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.client.R;
import com.example.client.base.BaseActivity;
import com.example.client.model.event.Event;
import com.example.client.utils.ImageUtils;
import com.example.client.utils.SharedPrefUtil;
import com.example.client.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;

public class PersonIntroduce extends BaseActivity implements View.OnClickListener {

    //动态改变文字
    @BindView(R.id.text_edit_Status)
    TextView textEditStatus;
    @BindView(R.id.text_edit_Status_honour)
    TextView textEditStatusHonour;
    //输入框
    @BindView(R.id.edit_person_introduce)
    EditText edit_person_introduce;
    @BindView(R.id.edit_person_honour)
    EditText edit_person_honour;

    private LinearLayout mine_person_photo;

    private Dialog mCameraDialog;

    protected static Uri tempUri=null;

    protected static final int CHOOSE_FILES=0;
    protected static final int TAKE_PICTURES=1;
    protected static final int CROP_SMALL_PICTURES=2;

    @Override
    protected int getLayoutId() {
        return R.layout.mine_person_introduce;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mine_person_photo=(LinearLayout)findViewById(R.id.mine_person_photo);

        edit_person_introduce.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textEditStatus.setText(String.valueOf(s.length())+"/200");
                if (s.length()>=200){
                    ToastUtils.show(mContext,R.string.show_person_introduce_honour);
                }
            }
        });

        edit_person_honour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textEditStatusHonour.setText(String.valueOf(s.length())+"/200");
                if (s.length()>=200){
                    ToastUtils.show(mContext,R.string.show_person_introduce_honour);
                }
            }
        });

    }

    @Override
    protected void initClick() {
        super.initClick();
        mine_person_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_person_photo:
                showBottomDialog_Person();
                break;

            case R.id.mine_achievedata_dialog_photo:
                takePicture();
//                ToastUtils.show(mContext,R.string.mine_btn_achieve_data_dialog_back);
                mCameraDialog.dismiss();
                break;

            case R.id.mine_achievedata_dialog_file:
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_FILES);
                mCameraDialog.dismiss();
                break;

            case R.id.btn_achievedata_cancel:
                mCameraDialog.dismiss();
                break;

            default:
                break;
        }

    }

    private void showBottomDialog_Person() {
        mCameraDialog = new Dialog(this, R.style.Find_BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.mine_achievedata_dialog, null);

        //初始化视图
        root.findViewById(R.id.mine_achievedata_dialog_photo).setOnClickListener(this);
        root.findViewById(R.id.mine_achievedata_dialog_file).setOnClickListener(this);
        root.findViewById(R.id.btn_achievedata_cancel).setOnClickListener(this);

        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画

        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    private void takePicture() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            // 需要申请动态权限
            int check = ContextCompat.checkSelfPermission(mContext, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= 24) {
            openCameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            tempUri = FileProvider.getUriForFile(mContext, "com.example.client.fileProvider", file);
        } else {
            tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "image.jpg"));
        }
        // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURES);
    }

    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent, CROP_SMALL_PICTURES);

    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            photo = ImageUtils.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            //将Bitmap转换成Drawable
            Drawable drawable = new BitmapDrawable(photo);
            mine_person_photo.setBackground(drawable);

            //SharePrefUtil存照片
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            String headimg = new String(Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT));
            SharedPrefUtil.putString(mContext,"icon",headimg);

            Bitmap bitmap = null;
            savePhoto(bitmap);
        }
    }

    private void savePhoto(Bitmap bitmap) {
        String imagename = "personIntroducePhoto";
        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), imagename + ".png");
        if(imagePath!=null){
            Log.i(TAG,"Success");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURES:
                    startPhotoZoom(tempUri); // 开始对图片进行裁剪处理
                    break;

                case CHOOSE_FILES:
                    startPhotoZoom(data.getData()); // 开始对图片进行裁剪处理
                    break;

                case CROP_SMALL_PICTURES:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
