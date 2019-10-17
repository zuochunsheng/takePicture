package com.zuo.takepicture.test;



import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zuo.takepicture.IUploadEvent;
import com.zuo.takepicture.TakepictureUtil;


public class TakePhoteTestActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv;
    private static TakePhoteTestActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_phote_test);

        Button btn = findViewById(R.id.btn);
        iv = findViewById(R.id.iv);
        context = this;
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                onBtnCropClick();
                break;
        }
    }

    private void onBtnCropClick() {

        //Activity parent = getParent(); error
        //context ok
        TakepictureUtil.getInstance(this)
                .checkPermissions(new IUploadEvent() {
                    @Override
                    public void takepictureSuccessEvent(String originUri) {
                        //Log.e("zuo", "原始路径:" + originUri);
                       // Log.e("zuo", "原始.getAbsolutePath<> " + new File(originUri).getAbsolutePath() + " ,大小<> " + new File(originUri).length());

                        Glide.with(TakePhoteTestActivity.this)
                                .load(originUri)
                                .placeholder(R.mipmap.ic_launcher)
                                .centerCrop()
                                .into(iv);
                    }

                });
    }

    @Override
    public boolean releaseInstance() {
        return super.releaseInstance();
    }

    @Override
    protected void onDestroy() {
        TakepictureUtil.getInstance(this).releaseInstance();
        super.onDestroy();
    }
}
