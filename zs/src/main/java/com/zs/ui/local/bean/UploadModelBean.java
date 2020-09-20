package com.zs.ui.local.bean;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.huaiye.cmf.JniIntf;
import com.zs.MCApp;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.dao.auth.AppAuth;
import com.zs.models.auth.bean.AnJianBean;

import java.io.Serializable;

import static com.zs.common.AppUtils.STRING_KEY_photo;

public class UploadModelBean implements Serializable {
    public String Authorization = AppAuth.get().getToken();
    public String userId = AppAuth.get().getUserID();
    public String deviceId = AppUtils.getIMEIResult(MCApp.getInstance());
    public String accuracy = "24.25";
    public String dimension = "56.23";
    public String shootTime;
    public long duration;
    public long size;
    public String resolutionRatio;
    public int type;
    public String businessId = "";
    public String fileName;
    public String fileMakeDate;
    public String nodeCode = "";
    public String nodeName = "";
    public String caseName = "";
    public String companyName = "";
    public String caseRelationFlag = "";
    public String keyEvidenceFlag = "0";

    public UploadModelBean(FileUpload tag) {
        //2020-09-12_21-42-20_android_video.dat
        //2020-09-15_21-51-11_video.mp4
        // 2020-09-12_15-24-47_android_image.jpg
        if (!TextUtils.isEmpty(AppAuth.get().getAnJian())) {
            AnJianBean bean = new Gson().fromJson(AppAuth.get().getAnJian(), AnJianBean.class);
            businessId = bean.businessId;
            nodeCode = bean.nodeCode;
            nodeName = bean.nodeName;
            nodeName = bean.nodeName;
            caseName = bean.caseName;
            companyName = bean.companyName;
            caseRelationFlag = "1";
        } else {
            caseRelationFlag = "0";
        }
        accuracy = MCApp.getInstance().locationService.getCurrentBDLocation().getLatitude() + "";
        dimension = MCApp.getInstance().locationService.getCurrentBDLocation().getLongitude() + "";

        shootTime = tag.name.substring(0, 10) + " " + tag.name.substring(11, 19).replaceAll("-", ":");
        size = tag.file.length();

        if (tag.file.getName().endsWith(".jpg")) {
            type = 1;
            resolutionRatio = "1920x1080";
        } else {
            type = 2;
            duration = JniIntf.GetRecordFileDuration(tag.file.getAbsolutePath());
            switch (SP.getInteger(STRING_KEY_photo, 3)) {
                case 1://640x480
                    resolutionRatio = "640x480";
                    break;
                case 2://1280x720
                    resolutionRatio = "1280x720";
                    break;
                case 3://1920x1080
                    resolutionRatio = "1920x1080";
                    break;
            }
        }
        fileName = tag.file.getName();
        fileMakeDate = tag.name.substring(0, 10);
    }
}
