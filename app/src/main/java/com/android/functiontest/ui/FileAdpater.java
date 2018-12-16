package com.android.functiontest.ui;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.functiontest.R;

import java.io.File;
import java.util.List;

/**
 * Created by amote on 2015/11/26.
 */
public class FileAdpater extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private File[] fileList;

    public FileAdpater(Context context, File[] list) {
        fileList = list;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setFileList(File[] data) {
        this.fileList = data;
    }

    @Override
    public int getCount() {
        return fileList.length;
    }

    @Override
    public Object getItem(int postion) {
        return fileList[postion];
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public View getView(int postion, View convertView, ViewGroup viewGroup) {
        ContentViewHolder contentViewHolder;
        if (convertView == null) {
            contentViewHolder = new ContentViewHolder();
            convertView = layoutInflater.inflate(R.layout.file_list_item, null);
            convertView.setTag(contentViewHolder);
            contentViewHolder.fileName = (TextView) convertView.findViewById(R.id.file_item_name);
            contentViewHolder.filePath = (TextView) convertView.findViewById(R.id.file_item_path);
            contentViewHolder.fileIcon = (ImageView) convertView.findViewById(R.id.file_item_icon);
        } else {
            contentViewHolder = (ContentViewHolder) convertView.getTag();
        }
        File file = fileList[postion];
        contentViewHolder.fileName.setText(file.getName());
        contentViewHolder.filePath.setText(file.toString());
        fileIcon(file, contentViewHolder.fileIcon);
        return convertView;
    }

    private void fileIcon(File file, ImageView wifiIcon) {
     if (file.isDirectory()){

     }else if (file.isFile()){

     }
    }

    class ContentViewHolder {
        TextView fileName;
        TextView filePath;
        ImageView fileIcon;
    }

}
