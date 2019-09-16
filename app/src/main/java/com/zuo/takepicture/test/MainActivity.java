package com.zuo.takepicture.test;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zuo.takepicture.test.R;
import com.zuo.takepicture.IUploadEvent;
import com.zuo.takepicture.TakepictureUtil;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        iv = findViewById(R.id.iv);

        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                onBtnCropClick();
                //Toast.makeText(this,"拍照 不含裁剪",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void onBtnCropClick() {

            TakepictureUtil.getInstance(this)
            .checkPermissions(new IUploadEvent() {
                @Override
                public void takepictureSuccessEvent(String originUri) {
                    Log.e("zuo", "原始路径:" + originUri);
                    Log.e("zuo", "原始.getAbsolutePath<> " + new File(originUri).getAbsolutePath() + " ,大小<> " + new File(originUri).length());

                    Glide.with(MainActivity.this)
                            .load(originUri)
                            .placeholder(R.mipmap.ic_launcher)
                            .centerCrop()
                            .into(iv);
                }

            });
    }
}
