package com.android.functiontest.ui;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.functiontest.MainPreFragment;
import com.android.functiontest.MyApplication;
import com.android.functiontest.R;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;

    @Override
    public Activity butterKnife() {
        return this;
    }

    @Override
    public int initLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.app_name)+ MyApplication.getInstance().getVersionName());
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getFragmentManager().beginTransaction().replace(R.id.content, new MainPreFragment()).commit();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {  //Android 6.0
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }
}
