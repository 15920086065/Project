package com.android.functiontest.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crlibrary.logs.Logger;
import com.android.functiontest.R;
import com.android.functiontest.camera.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;

/**
 * 相机测试
 */
public class CameraUseActivity extends BaseActivity {
    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, CameraUseActivity.class);
        activity.startActivity(intent);
    }

    @Bind(R.id.buttonCancel)
    ImageView buttonCancel;
    @Bind(R.id.buttonNext)
    Button buttonNext;
    /**
     * 图片媒体类型
     **/
    public static final int MEDIA_TYPE_IMAGE = 1;
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.buttonCamera)
    Button buttonCamera;
    /**
     * 摄像头类的对象
     **/
    private Camera mCamera;
    /**
     * SurfaceView对象
     **/
    private CameraPreview mPreview;

    private Camera.CameraInfo cameraInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();// 得到窗口
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 保持屏幕亮
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(CameraUseActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        buttonCamera.setOnClickListener(new OnClickListener() {// 拍照按钮事件
            @Override
            public void onClick(View v) {
                buttonCamera.setEnabled(false);
                // 先启动自动对焦
                mCamera.autoFocus(new AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            Logger.i("对焦成功");
                        } else {
                            Logger.i("对焦失败");
                        }
                        mCamera.cancelAutoFocus();
                        takePicture();
                    }
                });
            }
        });
//        cameraButton = (Button) findViewById(R.id.button_camera);
//        cameraButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // 返回按钮（拍完照片之后需要重新启动Camera的Preview）
//                mCamera.startPreview();
//            }
//        });

        // 获取Camera对象的实例
        try {
            mCamera = getCameraInstance();
            Logger.i(" mCamera " + mCamera);
            if (mCamera == null) {
                Toast.makeText(CameraUseActivity.this, "无法打开相机", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CameraUseActivity.this, "无法打开相机", Toast.LENGTH_LONG).show();
            return;
        }

        // 初始化SurfaceView
        mPreview = new CameraPreview(this, mCamera);
        // 将SurfaceView添加到FrameLayout中
        content.addView(mPreview);
        // 设置相机的属性
        Camera.Parameters params = mCamera.getParameters();
        // JPEG质量设置到最好
        params.setJpegQuality(100);
        // 散光灯模式设置为自动调节
        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        mCamera.setParameters(params);
    }

    /**
     * 初始化
     */
    @Override
    public void initView() {
        idToolbar.setTitle("");
        tooblarTitle.setText("摄像头测试");
        // 设置显示Toolbar
        setSupportActionBar(idToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        idToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCamera.setEnabled(true);
                mCamera.startPreview();
                buttonCamera.setVisibility(View.VISIBLE);
                buttonCancel.setVisibility(View.GONE);
            }
        });

        buttonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordActivityTest.openActivity(CameraUseActivity.this);
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
        return R.layout.activity_cameras;
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

    // PictureCallback回调函数实现
    private PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Logger.i("onPictureTaken");
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                return;
            }
            // 将照片数据data写入指定的文件
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    // 快门的回调函数实现
    private ShutterCallback mShutter = new ShutterCallback() {
        @Override
        public void onShutter() {
            Logger.i("onShutter");
            buttonCamera.setVisibility(View.INVISIBLE);
            buttonCancel.setVisibility(View.VISIBLE);
        }
    };

    private void takePicture() {
        // 1.75秒后启动拍照
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 拍照函数
                mCamera.takePicture(mShutter, null, mPicture);
            }
        }, 1750);
    }

    // 释放Camera对象（务必实现）
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera getCameraInstance() throws Exception {
        cameraInfo = new Camera.CameraInfo();
        Camera mCamera = null;
        if (mCamera == null) {
            int cameraCount = Camera.getNumberOfCameras();
            Logger.i("cameraCount:" + cameraCount);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                for (int i = 0; i < cameraCount; i++) {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(i, info);
                    // find front camera
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCamera = Camera.open(i);
                        Logger.i("to open front camera");
                        Camera.getCameraInfo(i, cameraInfo);
                        return mCamera;
                    }
                }
            }
            if (mCamera == null) {
                Logger.i("AAAAA OPEN camera");
                mCamera = Camera.open();
                Camera.getCameraInfo(0, cameraInfo);
            }
        }
        return mCamera;
    }

    /**
     * 在指定路径创建照片文件
     *
     * @param type
     * @return
     */
    public static File getOutputMediaFile(int type) {
        // 指定照片存放的目录，在SD根目录下的一个文件夹中
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "CameraUseApp");
        // 文件夹不存在，则创建该文件夹
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("CameraUse", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        // 创建照片文件
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 触摸屏幕自动对焦
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {

                }
            });
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
}