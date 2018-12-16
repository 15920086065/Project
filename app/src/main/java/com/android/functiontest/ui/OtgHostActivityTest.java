package com.android.functiontest.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;
import com.android.functiontest.utils.StorageUtils;

import java.io.File;

import butterknife.Bind;

/**
 * Created by yt on 2017/2/14.
 * OTG，HOST 测试
 */
public class OtgHostActivityTest extends SUOBaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.otgfileList)
    ListView otgfileList;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.content)
    LinearLayout content;
    @Bind(R.id.otgusbStatsTxt)
    TextView otgusbStatsTxt;
    private FileAdpater fileAdpater;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, OtgHostActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileAdpater = new FileAdpater(OtgHostActivityTest.this, null);
        Logger.i("getExternalStorageDirectory :: " + Environment.getExternalStorageDirectory().getPath());

        //判断SD卡是否插入
        if (!StorageUtils.isMountUSB(OtgHostActivityTest.this)) {
            otgusbStatsTxt.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
            return;
        }

        if (scanFile()){
            otgusbStatsTxt.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
        }else {
            otgusbStatsTxt.setVisibility(View.VISIBLE);
            content.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings14));
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LampActivityTest.openActivity(OtgHostActivityTest.this);
                finish();
            }
        });

    }

    /**
     * 初始化布局文件
     *
     * @return
     */
    @Override
    public int initLayout() {
        return R.layout.activity_otg;
    }

    /**
     * 初始化butter
     *
     * @return
     */
    @Override
    public Activity butterKnife() {
        return this;
    }

    /**
     * 注册USBOTG，SD卡广播事件
     *
     * @return
     */
    @Override
    public BroadcastReceiver getmStorageBroadcast() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Logger.i(intent.getAction()+" , "+intent.getScheme());
                String s = intent.getAction();
                if (s.equals(Intent.ACTION_MEDIA_EJECT) || s.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                    otgusbStatsTxt.setVisibility(View.VISIBLE);
                    content.setVisibility(View.GONE);
                    scanFile();

                } else if (s.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                    if (scanFile()) {
                        otgusbStatsTxt.setVisibility(View.GONE);
                        content.setVisibility(View.VISIBLE);
                    } else {
                        otgusbStatsTxt.setVisibility(View.VISIBLE);
                        content.setVisibility(View.GONE);
                    }

                }
            }
        };
    }

    private boolean scanFile() {
        File dir = new File(StorageUtils.USB_DIR);
        File[] files = dir.listFiles();
        if (files==null){
            return false;
        }
        Logger.i("fileList " + files.length);
        fileAdpater.setFileList(files);
        otgfileList.setAdapter(fileAdpater);
        fileAdpater.notifyDataSetChanged();
        return true;
    }


}
