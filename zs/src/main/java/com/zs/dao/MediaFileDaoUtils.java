package com.zs.dao;

import com.huaiye.cmf.JniIntf;
import com.huaiye.sdk.HYClient;
import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.SP;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ttyy.com.datasdao.Datas;
import ttyy.com.datasdao.annos.Column;

import static com.zs.common.AppUtils.CAPTURE_TYPE;
import static com.zs.common.AppUtils.STRING_KEY_false;
import static com.zs.dao.AppDatas.DBNAME;

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

    public File[] getAllVideos() {
        return mVideoDir.listFiles();
    }

    public File[] getAllImgs() {
        return mImageDir.listFiles();
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
        @Column
        private long key;
        @Column
        public long nRecordStartTimeMillions;// 开始时间的Millions
        @Column
        private long nRecordEndTimeMillions;// 当前时间的Millions1
        @Column
        private String strRecordFilePath;// 文件路径
        @Column
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
            fileName.append(getDateDetail().replace(" ", "_").replaceAll(":", "-"))
                    .append("_android");
            if (nMediaType == 0) {
                // 图片
                // yyyy-MM-dd_HH:mm:ss_android_image.jpg
                fileName.append("_image.jpg");
            } else {
                // 视频
                // yyyy-MM-dd_HH:mm:ss_android_video.dat
                fileName.append("_video.dat");
//                fileName.append("_video.mp4");
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
