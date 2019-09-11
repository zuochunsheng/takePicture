package com.zuo.takephoto.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zuo.takephoto.IUploadEvent;
import com.zuo.takephoto.TakephotoUtil;
import com.zuo.takephoto.test.R;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        Button btnCrop = findViewById(R.id.btnCrop);
        ivCrop = findViewById(R.id.ivCrop);

        btn.setOnClickListener(this);
        btnCrop.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                onBtnCropClick(ivCrop,false);
                //Toast.makeText(this,"拍照 不含裁剪",Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCrop:
                onBtnCropClick(ivCrop,true);
                break;
        }
    }

    private void onBtnCropClick(final ImageView imageView,final boolean isNeedCrop) {

        TakephotoUtil.getInstance(this)
                .setIsNeedCrop(isNeedCrop)
                .checkPermissions(new IUploadEvent() {
                    @Override
                    public void takephotoSuccessEvent(String originUri, String cropUri) {
                        Log.e("zuo", "原始路径:" + originUri);

                        Log.e("zuo", "原始.getAbsolutePath<> " + new File(originUri).getAbsolutePath() + " ,大小<> " + new File(originUri).length());

                        if(isNeedCrop){
                            Log.e("zuo", "裁剪后缓存的路径 :" + cropUri);
                            Log.e("zuo", "裁剪后.getAbsolutePath<> " + new File(cropUri).getAbsolutePath() + " ,大小<> " + new File(cropUri).length());
                        }


                        Glide.with(MainActivity.this)
                                .load(originUri)
                                .placeholder(R.mipmap.ic_launcher)
                                .centerCrop()
                                .into(imageView);
                    }

                    @Override
                    public void takephotoErrorEvent(String error) {
                        Log.e("zuo", "takephotoErrorEvent =" + error);
                    }
                });
    }
}
