package com.zuo.takephoto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;
import com.zuo.takephoto.util.FileProvider;

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
    private String mFilepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +"/hc" +"/takephoto_"  + System.currentTimeMillis() + ".jpg";

    //private String mFilepathCrop = Environment.getExternalStorageDirectory() + "/hc"+"/takephoto_crop" + System.currentTimeMillis() + ".jpg";
    private String mFilepathCrop = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/hc"+"/takephoto_crop_" + System.currentTimeMillis() + ".jpg";

    private boolean isNeedCrop ;
    private int index ;
    private String photoPath = "";
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
        index = intent.getIntExtra("permissions", 0);
        isNeedCrop = intent.getBooleanExtra("isNeedCrop", false);
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
            mProviderUri = FileProvider.getUriForFile(this, "com.zuo.takephoto.fileprovider", file);//manifest 值一样
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
            Toast.makeText(this,"摄像头未准备好",Toast.LENGTH_SHORT).show();

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
                    if(isNeedCrop){
                        // 拍照
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            // 调用裁剪方法
                            cropRawPhoto(mProviderUri);

                        } else {
                            cropRawPhoto(mUri);
                        }
                    }else {
                        TakephotoUtil.getInstance(this).onRequestPermissionsResult(TakephotoUtil.RESULT_TAKEPHOTO_SUCCESS,  mFilepath);
                        finish();
                    }
                    break;
                case SELECT_FILE:
                    if(isNeedCrop){
                        photoPath = getPhotoPath(data.getData());
                        cropRawPhoto(data.getData());

                    }else {
                            TakephotoUtil.getInstance(this).onRequestPermissionsResult(TakephotoUtil.RESULT_TAKEPHOTO_SUCCESS, getPhotoPath(data.getData()));

                        finish();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    // 成功（返回的是文件地址）
                    Uri resultUri = UCrop.getOutput(data);
                    //Uri inputUri = data.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
                    //Log.e("zuo","origon:" +inputUri.toString());
                    //Log.e("zuo", "crop:" + resultUri.toString());//file:///storage/emulated/0/Pictures/hc/takephoto_crop_1568262848725.jpg

                    //Log.e("zuo", "mFilepath.getAbsolutePath<> " + new File(mFilepath).getAbsolutePath() + " ,大小<> " + new File(mFilepath).length());
                    //Log.e("zuo", "uri.getAbsolutePath<> " + new File(resultUri.getPath()).getAbsolutePath() + " ,大小<> " + new File(resultUri.getPath()).length());

                    if(index == 0){
                        mFilepath = photoPath;
                    }
                    TakephotoUtil.getInstance(this).onRequestPermissionsResult(TakephotoUtil.RESULT_TAKEPHOTO_SUCCESS, mFilepath, resultUri.getPath());

                    finish();
                    break;
                case UCrop.RESULT_ERROR:
                    // 失败
                    TakephotoUtil.getInstance(this).onRequestPermissionsResult(TakephotoUtil.RESULT_TAKEPHOTO_ERROR, UCrop.getError(data) + "");
                    finish();
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {

            finish();
        }
    }

    //相册选择 ，不带后缀名的图片 返回带后后缀名的相册图片
    private String getPhotoPath(Uri uri){
        try {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);  //获取照片路径
            cursor.close();
            //Bitmap bitmap = BitmapFactory.decodeFile(path);
            return path ;
        } catch (Exception e) {
            e.printStackTrace();
            return uri.toString();
        }

    }

    public void cropRawPhoto(Uri uri) {
        Log.e("zuo", "cropRawPhoto: " + uri.toString());
        // 修改配置参数（我这里只是列出了部分配置，并不是全部）
        UCrop.Options options = new UCrop.Options();
        // 修改标题栏颜色
        options.setToolbarColor(getResources().getColor(R.color.black));
        // 修改状态栏颜色
        options.setStatusBarColor(getResources().getColor(R.color.blue));
        // 隐藏底部工具
        options.setHideBottomControls(true);
        // 图片格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        //options.setAllowedGestures(UCropActivity.NONE,UCropActivity.ALL,UCropActivity.ALL);//缩放 旋转 裁剪
        // 设置图片压缩质量
        options.setCompressionQuality(100);
        // 是否让用户调整范围(默认false)，如果开启，可能会造成剪切的图片的长宽比不是设定的
        // 如果不开启，用户不能拖动选框，只能缩放图片
        options.setFreeStyleCropEnabled(true);


        File file = new File(mFilepathCrop);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 设置需要裁剪的图片的Uri和裁剪之后的Uri       System.currentTimeMillis()
        UCrop.of(uri, Uri.fromFile(file))
                // 长宽比
                .withAspectRatio(1, 1)
                // 图片大小
                .withMaxResultSize(500, 500)
                // 配置参数
                .withOptions(options)
                .start(this);
    }

}
