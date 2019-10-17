package com.zuo.takepicture.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author： zcs
 * @time：2019/10/17 on 9:47
 * @description：
 */
public class BaseActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Context getContext(){
        return this;
    }

}
