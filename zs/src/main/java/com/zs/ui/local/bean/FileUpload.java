package com.zs.ui.local.bean;

import com.zs.common.SP;

import java.io.File;
import java.io.Serializable;

import static com.zs.common.AppUtils.STRING_KEY_save_photo;
import static com.zs.common.AppUtils.STRING_KEY_save_video;

public class FileUpload implements Serializable {

    public String name;
    public File file;
    public long remainingBytes;
    public long totalBytes;
    public int isUpload;
    public boolean isImg;

    public FileUpload(String name, File file) {
        this.name = name;
        this.file = file;
        isImg = name.endsWith(".jpg");
        if (isImg) {
            String saveImg = SP.getString(STRING_KEY_save_photo);
            if (saveImg.contains(name)) {
                isUpload = 3;
            }
        } else {
            String saveVideo = SP.getString(STRING_KEY_save_video);
            if (saveVideo.contains(name)) {
                isUpload = 3;
            }
        }
    }
}
