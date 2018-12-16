package com.android.functiontest.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by yt on 2017/2/15.
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TPView(this));
    }
}
