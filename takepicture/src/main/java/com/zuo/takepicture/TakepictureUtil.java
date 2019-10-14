package com.zuo.takepicture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chunsheng.permission.IPermission;
import com.chunsheng.permission.PermissionUtil;
import java.util.List;
import com.zuo.takepicture.R;


/**
 * anther: created by zcs on 2019/9/11 17 : 30
 * description :BottomSheetDialog :design:28.0.0
 */
public class TakepictureUtil {


    public static final int RESULT_TAKEPHOTO_SUCCESS = 0;
    //public static final int RESULT_TAKEPHOTO_ERROR = 1;

    private static IUploadEvent mUploadListener;

    private Context context;
    private static TakepictureUtil instance;

    // 单例模式中获取唯一的TakephotoUtil实例  synchronized
    public static TakepictureUtil getInstance(Context context) {
        if (instance == null) {
            instance = new TakepictureUtil(context);//.getApplicationContext()  bottomSheet 弹不出
        }
        return instance;
    }

    public TakepictureUtil(Context context) {
        this.context = context;
    }

    private void setUploadListener(IUploadEvent nUploadListener) {
        mUploadListener = nUploadListener;
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

        TextView tvBottomsheetPhotolist = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_photolist_com_takepicture);
        TextView tvBottomsheetTakephoto = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_takephoto_com_takepicture);
        TextView tvBottomsheetCancel = (TextView) bottomSheetDialog.findViewById(R.id.tv_bottomsheet_cancel_com_takepicture);

        //从相册选择一张
        tvBottomsheetPhotolist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShadowActivity(0);
                bottomSheetDialog.cancel();

            }
        });
        // 拍照 一张
        tvBottomsheetTakephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startShadowActivity(1);
                bottomSheetDialog.cancel();
            }
        });

        tvBottomsheetCancel.setOnClickListener(new View.OnClickListener() {
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    //回调 ---十分巧妙
    void onRequestPermissionsResult(int resultCode, String result) {
        switch (resultCode) {
            case RESULT_TAKEPHOTO_SUCCESS:
                mUploadListener.takepictureSuccessEvent(result);

                break;

        }
    }

}
