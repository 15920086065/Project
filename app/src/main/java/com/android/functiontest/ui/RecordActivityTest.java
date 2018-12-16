package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;

/**
 * 录音测试
 * <p/>
 * Created by yt on 2017/2/14.
 */
public class RecordActivityTest extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.recordSta)
    TextView recordSta;
    @Bind(R.id.start)
    Button startBtn;
    @Bind(R.id.play)
    Button playBtn;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;

    private static final String WAKEUP_ACTION = "com.jlx.openapi.tts.wakeup";
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File audioFile;
    private String recordPath;
    private boolean isRecorder;
    private AudioManager mAudioManager;
    private File mSampleFile = null;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, RecordActivityTest.class);
        activity.startActivity(intent);
    }

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Logger.i("focusChange: " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Logger.i("AudioManager.AUDIOFOCUS_LOSS");
                Toast.makeText(RecordActivityTest.this, "AUDIOFOCUS_LOSS", Toast.LENGTH_SHORT).show();
                //startWakeup();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                Logger.i("AudioManager.AUDIOFOCUS_GAIN");
                Toast.makeText(RecordActivityTest.this, "AUDIOFOCUS_GAIN", Toast.LENGTH_SHORT).show();
                //stopWakeup();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText("录音测试");
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonNext.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
    }


    /**
     * 初始化布局文件
     *
     * @return
     */
    @Override
    public int initLayout() {
        return R.layout.activity_record;
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

    private void startRecording(int outputfileformat, String extension, Context context) {
        stop();
        requestAudioFocus();
        stopWakeup();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mSampleFile == null) {
            File sampleDir = Environment.getExternalStorageDirectory();
            if (!sampleDir.canWrite()) // Workaround for broken sdcard support on the device.
                sampleDir = new File("/sdcard/sdcard");
            try {
                mSampleFile = File.createTempFile("recording", extension, sampleDir);
            } catch (IOException e) {
                anandonAudioFocu();
                return;
            }
        }

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(/*MediaRecorder.OutputFormat.AMR_NB*/outputfileformat);

        //第3步：设置音频编码方式（默认的编码方式）
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setAudioChannels(1); // MONO
//        mediaRecorder.setAudioSamplingRate(8000); // 8000Hz
//        mediaRecorder.setAudioEncodingBitRate(64); // seems if change this to
        mediaRecorder.setOutputFile(mSampleFile.getAbsolutePath());
        //第5步：调用prepare方法
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            anandonAudioFocu();
            stopRecording();
            return;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "IllegalStateException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            anandonAudioFocu();
            stopRecording();
            return;
        }

        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "IllegalStateException: " + e.getMessage(), Toast.LENGTH_LONG).show();
            anandonAudioFocu();
            stopRecording();
            return;
        }

        recordSta.setText("正在录音");
        startBtn.setText("停止录音");
        playBtn.setEnabled(false);
        isRecorder = true;
    }

    private void stopWakeup() {
        Logger.e("stopWakeup");
        Intent mIntent = new Intent();
        mIntent.setAction(WAKEUP_ACTION);
        mIntent.putExtra("start", false);
        sendBroadcast(mIntent);
    }

    private void startWakeup() {
        Logger.e("startWakeup");
        Intent mIntent = new Intent();
        mIntent.setAction(WAKEUP_ACTION);
        mIntent.putExtra("start", true);
        sendBroadcast(mIntent);
    }

    private boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void anandonAudioFocu() {
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        startWakeup();
    }

    private void startPlay() {
        stop();
        requestAudioFocus();

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mSampleFile.getPath());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    anandonAudioFocu();
                    stop();
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            isRecorder = false;
            recordSta.setText("播放录音");
            playBtn.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
            anandonAudioFocu();
            stop();
            return;
        }
    }

    private void stop() {
        stopPlay();
        stopRecording();
        recordSta.setText("准备录音");
        startBtn.setText("开始录音");
        startBtn.setEnabled(true);
        playBtn.setEnabled(true);
        isRecorder = false;
    }

    private void stopRecording() {
        if (mediaRecorder == null)
            return;
        try {
            mediaRecorder.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void stopPlay() {
        if (mediaPlayer == null)
            return;
        try {
            mediaPlayer.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    private void deleteFile() {
        if (mSampleFile != null) {
            mSampleFile.delete();
            mSampleFile = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonNext) {
            VolumeActivityTest.openActivity(RecordActivityTest.this);
            finish();
        } else if (v.getId() == R.id.play) {
            startPlay();
        } else if (v.getId() == R.id.start) {
            if (isRecorder) {
                anandonAudioFocu();
                stop();
            } else {
                startRecording(MediaRecorder.OutputFormat.THREE_GPP, ".3gpp", this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        anandonAudioFocu();
        stop();
        deleteFile();
    }

}
