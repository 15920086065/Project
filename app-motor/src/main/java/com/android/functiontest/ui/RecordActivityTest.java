package com.android.functiontest.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
 * Created by yt on 2017/2/14.
 */
public class RecordActivityTest extends BaseActivity {
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    @Bind(R.id.recordSta)
    TextView recordSta;
    @Bind(R.id.start)
    Button start;
    @Bind(R.id.play)
    Button play;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File audioFile;
    private String recordPath;
    private boolean isRecorder;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, RecordActivityTest.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolumeActivityTest.openActivity(RecordActivityTest.this);
                finish();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecorder) {
                    recordStop();
                } else {
                    recordStart();
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    play();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.e(e.toString());
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

    private void recordStart() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //创建一个临时的音频输出文件
        try {
            mediaRecorder = new MediaRecorder();
            // 第1步：设置音频来源（MIC表示麦克风）
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //第2步：设置音频输出格式（默认的输出格式）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //第3步：设置音频编码方式（默认的编码方式）
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setAudioChannels(1); // MONO
            mediaRecorder.setAudioSamplingRate(8000); // 8000Hz
            mediaRecorder.setAudioEncodingBitRate(64); // seems if change this to
            // 128, still got same file
            // size.
            try {
                //audioFile = File.createTempFile("record_", ".amr");
                //设置sdcard的路径  
                recordPath=Environment.getExternalStorageDirectory().getAbsolutePath();
                recordPath+="/audiorecordtest.3gp";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Logger.i(recordPath);
            //第4步：指定音频输出文件
            mediaRecorder.setOutputFile(/*audioFile.getAbsolutePath()*/recordPath);
            //第5步：调用prepare方法
            mediaRecorder.prepare();
            //第6步：调用start方法开始录音
            mediaRecorder.start();
            recordSta.setText("正在录音");
            isRecorder = true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error :: " + e.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        start.setText("停止录音");
        play.setEnabled(false);
    }

    private void recordStop() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                start.setText("开始录音");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        recordSta.setText("准备录音");
        play.setEnabled(true);
        isRecorder = false;
    }

    private void play() throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(/*audioFile.getPath()*/recordPath);
        mediaPlayer.prepare();
        mediaPlayer.start();
        isRecorder = false;
        recordSta.setText("播放录音");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (!TextUtils.isEmpty(recordPath)){
            File file = new File(recordPath);
            file.delete();
        }
    }
}
