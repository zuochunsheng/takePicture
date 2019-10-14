package com.zuo.takepicture;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.zuo.takepicture.util.FileProvider;

import java.io.File;


/**
 * anther: created by zcs on 2019/9/11 18 : 24
 * description :
 * 问题 从相册选择 路径为 /content:/media/external/images/media/1833619 ,大小<> 0
 */
public class ShadowActivity extends Activity {


    private static final int SELECT_FILE = 0;          // 选择照片
    private static final int REQUEST_CAMERA = 1;       // 拍照

    // 7.0 以上的uri
    private Uri mProviderUri;
    // 7.0 以下的uri
    private Uri mUri;
    // 图片路径
    //private String mFilepath = Environment.getExternalStorageDirectory() +"/hc" +"/takephoto"  + ".jpg";
    private String mFilepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/hc" + "/takepicture_" + System.currentTimeMillis() + ".jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        int index = intent.getIntExtra("permissions", 0);
        startTakePhotos(index);
    }


    private void startTakePhotos(int index) {

        if (index == 0) {//相册
            selectImg();
        } else {//拍照
            camera();
        }

    }

    /**
     * 拍照
     * //System.currentTimeMillis() + ".jpg"
     */
    private void camera() {
        File file = new File(mFilepath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            mProviderUri = FileProvider.getUriForFile(this, getFileProviderName(this), file);//manifest 值一样
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mProviderUri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            mUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        }
        try {
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (ActivityNotFoundException anf) {
            Toast.makeText(this, "摄像头未准备好", Toast.LENGTH_SHORT).show();

        }
    }

    //相册选图
    private void selectImg() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickIntent, SELECT_FILE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CAMERA:

                    TakepictureUtil.getInstance(this).onRequestPermissionsResult(TakepictureUtil.RESULT_TAKEPHOTO_SUCCESS, mFilepath);
                    finish();
                    break;
                case SELECT_FILE:
                    TakepictureUtil.getInstance(this).onRequestPermissionsResult(TakepictureUtil.RESULT_TAKEPHOTO_SUCCESS, getPhotoPath(data.getData()));

                    finish();

                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            // TakepictureUtil.getInstance(this).onRequestPermissionsResult(TakepictureUtil.RESULT_TAKEPHOTO_ERROR,  "");
            finish();
        }
    }

    //相册选择 ，不带后缀名的图片 返回带后后缀名的相册图片
    private String getPhotoPath(Uri uri) {
        try {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);  //获取照片路径
            cursor.close();
            //Bitmap bitmap = BitmapFactory.decodeFile(path);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return uri.toString();
        }

    }

    public static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }

}
