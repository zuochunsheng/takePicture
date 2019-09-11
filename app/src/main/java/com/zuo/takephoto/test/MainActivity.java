package com.zuo.takephoto.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zuo.takephoto.test.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = findViewById(R.id.btn);
        Button btnCrop = findViewById(R.id.btnCrop);
        btn.setOnClickListener(this);
        btnCrop.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                Toast.makeText(this,"拍照 不含裁剪",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCrop:

                break;
        }
    }
}
