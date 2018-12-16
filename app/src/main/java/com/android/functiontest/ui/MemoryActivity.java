package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.functiontest.R;
import com.android.functiontest.deviceinfo.Memory;

import butterknife.Bind;

/**
 * 资源管理器
 * <p/>
 * Created by yt on 2017/3/24.
 */
public class MemoryActivity extends BaseActivity {
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, MemoryActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings16));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public int initLayout() {
        return R.layout.activity_memory;
    }

    @Override
    public Activity butterKnife() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.content, new Memory()).commit();
    }

}
