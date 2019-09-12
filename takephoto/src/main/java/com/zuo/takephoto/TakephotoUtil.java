package com.zuo.takephoto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chunsheng.permission.IPermission;
import com.chunsheng.permission.PermissionUtil;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


/**
 * anther: created by zcs on 2019/9/11 17 : 30
 * description :BottomSheetDialog :design:28.0.0
 */
public class TakephotoUtil {


    public static final int RESULT_TAKEPHOTO_SUCCESS = 0;
    public static final int RESULT_TAKEPHOTO_ERROR = 1;

    private static IUploadEvent mUploadListener;

    private Context context;
    private static TakephotoUtil instance;
    private boolean isNeedCrop = false;

    // 单例模式中获取唯一的TakephotoUtil实例  synchronized
    public static TakephotoUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TakephotoUtil(context);//.getApplicationContext()  bottomSheet 弹不出
        }
        return instance;
    }

    public TakephotoUtil(Context context) {
        this.context = context;
    }

    private void setUploadListener(IUploadEvent nUploadListener) {
        mUploadListener = nUploadListener;
    }

    public TakephotoUtil setIsNeedCrop(boolean isNeedCrop) {
        this.isNeedCrop = isNeedCrop;
        return instance ;
    }


    //  6.0 检查 权限
    public void checkPermissions(final IUploadEvent nUploadListener) {
        //checkPermission
        PermissionUtil.getInstance(context)
                .requestRunTimePermission(new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new IPermission() {
                            @Override
                            public void onGranted() {
                                setUploadListener(nUploadListener);
                                initBottomSheetDialog();
                            }

                            @Override
                            public void onDenied(List<String> deniedPermission) {
                                Toast.makeText(context, "授权未通过", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
    }

    // 底部弹框
    private void initBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogStyle);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView tv_bottomsheet_photolist = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_photolist);
        TextView tv_bottomsheet_takephoto = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_takephoto);
        TextView tv_bottomsheet_cancel = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_cancel);

        //从相册选择一张
        tv_bottomsheet_photolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShadowActivity(0);
                bottomSheetDialog.cancel();

            }
        });
        // 拍照 一张
        tv_bottomsheet_takephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startShadowActivity(1);
                bottomSheetDialog.cancel();
            }
        });

        tv_bottomsheet_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();

    }

    //跳转
    private void startShadowActivity(int index) {

        Intent intent = new Intent(context, ShadowActivity.class);
        intent.putExtra("permissions", index);
        intent.putExtra("isNeedCrop", isNeedCrop);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    //回调 ---十分巧妙
    public void onRequestPermissionsResult(int resultCode, String result, String... args) {

        Log.d("zuo", "mineFragment onActivityResult");

        switch (resultCode) {
            case RESULT_TAKEPHOTO_SUCCESS:
                //第一个参数 原始uri
                // 第二个参数 裁剪后的 uri (isNeedCrop = true 是有值)
                if(isNeedCrop){
                    mUploadListener.takephotoSuccessEvent(result, args[0]);
                }else {
                    mUploadListener.takephotoSuccessEvent(result, null);
                }
                break;
            case RESULT_TAKEPHOTO_ERROR:
                // 失败
                mUploadListener.takephotoErrorEvent(result);
                break;
        }

    }

}
