package com.android.functiontest.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crlibrary.logs.Logger;
import com.android.crlibrary.utils.PowerUtils;
import com.android.functiontest.R;
import com.android.functiontest.explorer.ConfigUtil;
import com.android.functiontest.explorer.FileControl;
import com.android.functiontest.explorer.FileInfo;
import com.android.functiontest.explorer.Global;
import com.android.functiontest.explorer.NormalListAdapter;
import com.android.functiontest.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;

/**
 * 资源管理器
 * Created by yt on 2017/4/18.
 */
public class ExplorerActivity extends BaseActivity {
    @Bind(R.id.tooblarTitle)
    TextView tooblarTitle;
    @Bind(R.id.id_toolbar)
    Toolbar idToolbar;
    @Bind(android.R.id.list)
    ListView mainListView;
    FileControl mFileControl;
    NormalListAdapter tempAdapter;
    String usbPath = new String(Global.usb_dir);
    String sdcardPath = new String(Global.sdcard_dir);
    String flashPath = new String(Global.flash_dir);
    String internalPath = new String(Global.internal_dir);
    @Bind(R.id.buttonNext)
    Button buttonNext;
    //存储文件路径
    private ArrayList<String> savePaths = null;
    private ArrayList<FileInfo> folderArray = new ArrayList<FileInfo>();
    public boolean openwiththread = false;
    public static String lastPath = null;
    public static boolean isLastPath = false;
    // 此处当点击了"上个" 后的下一次点击"上层"时为删除pit_save_path后的路径记录点
    private boolean isDelSavePath = false;

    public static void openActivity(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, ExplorerActivity.class);
        activity.startActivity(intent);
    }

    /**
     * 初始化
     */
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
        //单击事件
        mainListView.setOnItemClickListener(onItemClickListener);
        mFileControl = new FileControl(this, null, mainListView);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS);
                startActivity(intent);
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
        return R.layout.explorer_activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerUtils.init(this);
        savePaths = new ArrayList<String>();
        setFileAdapter();
        registBroadcastRec();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerUtils.getIntance().LockScreen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerUtils.getIntance().UnLockScreen();
    }

    /**
     * 设置列表
     */
    public void setFileAdapter() {
        synchronized (mFileControl.folder_array) {
            folderArray.clear();
            for (FileInfo mFileInfo : mFileControl.folder_array) {
                folderArray.add(mFileInfo);
            }
        }
        tempAdapter = new NormalListAdapter(this, folderArray);
        mainListView.setAdapter(tempAdapter);
        setTitle(mFileControl.get_currently_path());
    }

    /**
     * 设置标题
     *
     * @param content
     */
    public void setTitle(String content) {
        if (TextUtils.isEmpty(content)) return;
        if (content.startsWith(usbPath)) {
            if (ConfigUtil.NOT_USE_SDCRAD) {
                tooblarTitle.setText(getString(R.string.str_usb2_name) + content.substring(usbPath.length()));
            } else {
                tooblarTitle.setText(getString(R.string.str_usb1_name) + content.substring(usbPath.length()));
            }
        } else if (content.startsWith(sdcardPath)) {
            tooblarTitle.setText(getString(R.string.str_sdcard_name) + content.substring(sdcardPath.length()));
        } else if (content.startsWith(flashPath)) {
            tooblarTitle.setText(getString(R.string.str_flash_name)
                    + content.substring(flashPath.length()));
        } else if (content.startsWith(internalPath)) {
            tooblarTitle.setText(getString(R.string.str_internal_name)
                    + content.substring(internalPath.length()));
        } else {
            tooblarTitle.setText(content);
        }
    }

    /**
     * 打开文件夹
     *
     * @param path
     */
    public void openDir(String path) {
        if (TextUtils.isEmpty(path)) return;
        Logger.v(path);
        fillPath = path;
        File files = new File(path);
        if (!files.exists() || !files.canRead()) {
            FileControl.is_enable_fill = true;
            FileControl.is_finish_fill = true;
            return;
        }
        long file_count = files.listFiles().length;
        Logger.v("file_count = " + file_count);
        //文件数大于1500个 需要加载进度提示
        if (file_count > 1500) {
            openwiththread = true;
        } else {
            openwiththread = false;
        }
        mOpenHandler.post(mOpeningRun);
        mFillHandler.post(mFillRun);
    }

    /*
     * 如下的handle是负责显示出拷贝进度对话框
	 */
    public String fillPath = null;
    Handler mOpenHandler = new Handler();
    Runnable mOpeningRun = new Runnable() {
        @Override
        public void run() {
            mFileControl.refill(fillPath); // 重新refill
        }
    };

    /*
     * 如下的handle是负责打开文件的监听
	 */
    Handler mFillHandler = new Handler();
    Runnable mFillRun = new Runnable() {
        public void run() {
            Logger.d("in the mFillRun, is_finish_fill = " + FileControl.is_finish_fill);
            Logger.d("in the mFillRun, adapte size = " + mFileControl.folder_array.size());
            if (!FileControl.is_finish_fill) { // 若fill还未结束
                if (openwiththread) {
                    setFileAdapter();
                    mainListView.setSelection(mFileControl.folder_array.size() - 6);
                }
                mFillHandler.postDelayed(mFillRun, 1500);
            } else {
                if (openwiththread)
                    setFileAdapter();
                else {
                    if (isLastPath) {
                        isLastPath = false;
                        setFileAdapter();
                    } else {
                        setFileAdapter();
                    }
                }
                mFillHandler.removeCallbacks(mFillRun);
//                if (openingDialog != null)
//                    openingDialog.dismiss();
            }
        }
    };

    /**
     * removeLastPath
     */
    public void removePath() {
        if (isDelSavePath) { // 若到了删除路径保存的点时
            isDelSavePath = false;
            int size = savePaths.size();
            savePaths.remove(size - 1);
        }
    }

    /**
     * 单击事件
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileInfo fileInfo = (FileInfo) parent.getItemAtPosition(position);
            Logger.v(fileInfo.toString());
            //SD卡
            if (fileInfo.file.getPath().equals(Global.sdcard_dir) && mFileControl.is_first_path) {
                if (!StorageUtils.isMountSDCARD(ExplorerActivity.this)) { // 若没有挂载SD卡，则返回，且给出错误提示
                    Toast.makeText(ExplorerActivity.this, getString(R.string.str_sdcard_umount), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //flash
            if (fileInfo.file.getPath().equals(Global.flash_dir)) {
                if (!StorageUtils.isMountFLASH(ExplorerActivity.this)) {
                    Toast.makeText(ExplorerActivity.this,
                            getString(R.string.str_flash_umount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (fileInfo.file.getPath().equals(Global.usb_dir)) {
                if (!StorageUtils.isMountUSB(ExplorerActivity.this)) { // 若没有挂载SD卡，则返回，且给出错误提示
                    Toast.makeText(ExplorerActivity.this,
                            getString(R.string.str_usb1_umount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (fileInfo.file.getPath().equals(Global.internal_dir)) {
                if (!StorageUtils.isMountINTERNAL(ExplorerActivity.this)) {//
                    Toast.makeText(ExplorerActivity.this,
                            getString(R.string.str_internal_unmount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //点击的是文件夹
            if (fileInfo.isDir) {
                openDir(fileInfo.file.getPath());
                savePaths.add(fileInfo.file.getPath());
            }
        }
    };


    /**
     * 注册接收usb/sdcard umount消息
     */
    public void registBroadcastRec() {
		/*
		 * < 设置 监听 "scanner started", "scanner finished" 和 "SD 卡 unmounted" 等事件
		 * >.
		 */
        IntentFilter f = new IntentFilter();
        // f.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        // f.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        f.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        f.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        // f.addAction(Intent.ACTION_MEDIA_MOUNTED);
        f.addDataScheme("file");
        registerReceiver(mScanListener, f);
    }

    /*
	 * 接收usb/sdcard umount消息
	 */
    private BroadcastReceiver mScanListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.d("mScanListener BroadcastReceiver action" + action);
            if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                if (mFileControl != null
                        && intent != null
                        && intent.getData() != null
                        && intent.getData().getPath() != null
                        && mFileControl.get_currently_path() != null
                        && mFileControl.get_currently_path().startsWith(
                        intent.getData().getPath())) {
                    finish();
                } else if (mFileControl.get_currently_path() == null) {
                    Logger.d("mFileControl.get_currently_path() = null~~~~");
                    finish();
                }
            } else if (Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {

            }
        }
    };

    /*
	 * 进入最上层文件列表
	 */
    public void fillFirstPath(FileControl mtmpFileControl) {
        mtmpFileControl.firstFill();
        setFileAdapter();
        setTitle(mtmpFileControl.get_currently_path());
    }

    /**
     * 是否返回到主页
     *
     * @return
     */
    boolean isFirstPath() {
        Logger.e("is_first_path = " + mFileControl.is_first_path + ", get_currently_path = " + mFileControl.get_currently_path());
        if (mFileControl.is_first_path) {
            return true;
        }
        if (mFileControl.get_currently_path().equals(Global.flash_dir)
                || mFileControl.get_currently_path().equals(Global.sdcard_dir)
                || mFileControl.get_currently_path().equals(Global.usb_dir)
                || mFileControl.get_currently_path().equals(Global.internal_dir)) {
            fillFirstPath(mFileControl);
            return false;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Logger.v("is_first_path :: " + mFileControl.is_first_path);
        if (isFirstPath()) {
            super.onBackPressed();
            return;
        }
        //清除最后一次的路径
        removePath();
        openDir(mFileControl.get_currently_parent());
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFirstPath()) {
                return super.onKeyDown(keyCode, event);
            }
            //清除最后一次的路径
            removePath();
            openDir(mFileControl.get_currently_parent());
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScanListener);
    }
}
