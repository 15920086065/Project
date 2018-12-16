package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import java.io.IOException;

import butterknife.Bind;

/**
 * 音量测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class VolumeActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.volumesText)
    TextView volumesText;
    @Bind(R.id.volumeSeekbar)
    SeekBar volumeSeekbar;


    public AudioManager audioManager;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    private int maxVolume;
    private int currentVolume;
    private MediaPlayer mediaPlayer;
    private AssetManager assetManager;
    private AssetFileDescriptor fileDescriptor;
    protected int outgoing;
    private SoundPool mSoundPool = null;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, VolumeActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText(getString(R.string.ui_settings7));
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
                KeyActivityTest.openActivity(VolumeActivityTest.this);
                finish();
            }
        });

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//获取系统最大音量   
        volumeSeekbar.setMax(maxVolume);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekbar.setProgress(currentVolume);
        volumesText.setText("音量 " + currentVolume * 100 / maxVolume);
        assetManager = getResources().getAssets();
        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Logger.i("onProgressChanged");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeSeekbar.setProgress(currentVolume);
                volumesText.setText("音量 " + currentVolume * 100 / maxVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Logger.i("onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Logger.i("onStopTrackingTouch");
                try {
                    playSound();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e(e.toString());
                    release();
                }
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
        return R.layout.activity_volume;
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
     * 播放音效
     *
     * @throws IOException
     */
    private void playSound() throws IOException {
        release();
        mediaPlayer = new MediaPlayer();
        fileDescriptor = assetManager.openFd("Highwire.ogg");
        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    /**
     * 重置
     */
    private void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

}
