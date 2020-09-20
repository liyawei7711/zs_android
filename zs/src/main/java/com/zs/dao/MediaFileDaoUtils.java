package com.zs.dao;

import android.text.TextUtils;

import com.huaiye.sdk.HYClient;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.ui.local.bean.FileUpload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.zs.common.AppUtils.CAPTURE_TYPE;
import static com.zs.common.AppUtils.STRING_KEY_false;
import static com.zs.common.AppUtils.STRING_KEY_save_photo;
import static com.zs.common.AppUtils.STRING_KEY_save_video;

/**
 * author: admin
 * date: 2017/09/15
 * version: 0
 * mail: secret
 * desc: LocalMediaDao
 */
public class MediaFileDaoUtils {
    private static final SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }


    File mVideoDir;
    File mImageDir;

    private MediaFileDaoUtils() {
        mVideoDir = HYClient.getContext().getExternalFilesDir("videos");
        mImageDir = HYClient.getContext().getExternalFilesDir("images");

        mVideoDir.mkdirs();
        mImageDir.mkdirs();
    }

    static class Holder {
        static final MediaFileDaoUtils INSTANCE = new MediaFileDaoUtils();
    }

    public static MediaFileDaoUtils get() {
        return Holder.INSTANCE;
    }

    public ArrayList<FileUpload> getAllVideos() {
        String currentFileName = "";
        if (MCApp.getInstance().guanMoOrPushActivity != null) {
            try {
                currentFileName = MCApp.getInstance().guanMoOrPushActivity.captureView.mMediaFile.getRecordPath();
                if(!MCApp.getInstance().guanMoOrPushActivity.captureView.isStart) {
                    currentFileName = "";
                }
            } catch (Exception e) {
            }
        }
        ArrayList<FileUpload> datas = new ArrayList<>();
        String saveVideo = SP.getString(STRING_KEY_save_video);
        StringBuilder sb = new StringBuilder();
        for (File temp : mVideoDir.listFiles()) {
            FileUpload fileUpload = new FileUpload(temp.getName(), temp);
            if (fileUpload.isUpload == 3) {
                fileUpload.file.delete();
                break;
            }
            if (!TextUtils.isEmpty(currentFileName)) {
                if (temp.getName().equals(new File(currentFileName).getName())) {
                    break;
                }
            }
            datas.add(fileUpload);
            if (saveVideo.contains(temp.getName())) {
                sb.append(temp.getName());
            }
        }
        SP.putString(STRING_KEY_save_video, sb.toString());
        return datas;
    }

    public ArrayList<FileUpload> getAllImgs() {
        String currentFileName = "";
        if (MCApp.getInstance().guanMoOrPushActivity != null) {
            try {
                currentFileName = MCApp.getInstance().guanMoOrPushActivity.captureView.mMediaFile.getRecordPath();
                if(!MCApp.getInstance().guanMoOrPushActivity.captureView.isStart) {
                    currentFileName = "";
                }
            } catch (Exception e) {
            }
        }
        ArrayList<FileUpload> datas = new ArrayList<>();
        String saveImg = SP.getString(STRING_KEY_save_photo);
        StringBuilder sb = new StringBuilder();
        for (File temp : mImageDir.listFiles()) {
            FileUpload fileUpload = new FileUpload(temp.getName(), temp);
            if (fileUpload.isUpload == 3) {
                fileUpload.file.delete();
                break;
            }
            if (!TextUtils.isEmpty(currentFileName)) {
                if (temp.getName().equals(new File(currentFileName).getName())) {
                    break;
                }
            }
            datas.add(fileUpload);
            if (saveImg.contains(temp.getName())) {
                sb.append(temp.getName());
            }
        }
        SP.putString(STRING_KEY_save_photo, sb.toString());
        return datas;
    }

    public void clear() {
        for (File temp : mImageDir.listFiles()) {
            temp.delete();
        }
        for (File temp : mVideoDir.listFiles()) {
            temp.delete();
        }
    }

    public MediaFile getVideoRecordFile() {
        MediaFile data = new MediaFile();
        data.nMediaType = 1;

        boolean captureType = Boolean.parseBoolean(SP.getParam(CAPTURE_TYPE, STRING_KEY_false).toString());
        if (captureType) {
            data.strRecordFilePath = new File(mVideoDir, data.getRecordName()).getPath();
        }
        return data;
    }

    public MediaFile getImgRecordFile() {
        MediaFile data = new MediaFile();
        data.nMediaType = 0;
        data.strRecordFilePath = new File(mImageDir, data.getRecordName()).getPath();
        return data;
    }

    public static class MediaFile {
        private long key;
        public long nRecordStartTimeMillions;// 开始时间的Millions
        private long nRecordEndTimeMillions;// 当前时间的Millions1
        private String strRecordFilePath;// 文件路径
        private int nMediaType;// 0:图片 1:视频

        public boolean isSelected;// 0:图片 1:视频

        MediaFile() {
            key = System.currentTimeMillis();
            nRecordStartTimeMillions = key;
        }

        public String getRecordPath() {

            return strRecordFilePath;
        }

        public String getRecordName() {
            StringBuilder fileName = new StringBuilder();
            fileName.append(getDateDetail().replace(" ", "_").replaceAll(":", "-"));
//                    .append("_android");
            if (nMediaType == 0) {
                // 图片
                // yyyy-MM-dd_HH:mm:ss_android_image.jpg
//                fileName.append("_image.jpg");
                fileName.append(".jpg");
            } else {
                // 视频
                // yyyy-MM-dd_HH:mm:ss_android_video.dat
//                fileName.append("_video.dat");
//                fileName.append(".dat");
                fileName.append(".mp4");
            }
            return fileName.toString();
        }

        private String getTimeLength(int seconds) {
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hour = minutes / 60;
            minutes = minutes % 60;

            StringBuilder sb = new StringBuilder();
            if (hour > 0) {
                sb.append(hour).append(AppUtils.getString(R.string.hour));
            }

            if (minutes > 0) {
                sb.append(minutes).append(AppUtils.getString(R.string.minute));
            }

            if (seconds > 0) {
                sb.append(seconds).append(AppUtils.getString(R.string.second));
            }

            return sb.toString();
        }

        public String getDateDetail() {

            return sdf.format(new Date(nRecordStartTimeMillions));
        }

        protected void del() {
            File file = new File(strRecordFilePath);
            if (file.exists())
                file.delete();

            int i = 1;
            while (true) {
                String path = strRecordFilePath + i;
                file = new File(path);
                if (file.exists()) {
                    file.delete();
                } else {
                    break;
                }
            }
        }
    }
}
