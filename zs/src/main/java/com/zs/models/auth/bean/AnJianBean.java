package com.zs.models.auth.bean;

import android.text.TextUtils;

import java.io.Serializable;

public class AnJianBean implements Serializable {
    public String caseName = "";
    public String companyName = "";
    public String businessId = "";
    public String nodeCode = "";
    public String nodeName = "";

    public String getName() {
        if(TextUtils.isEmpty(caseName)) {
            return companyName;
        } else {
            return caseName;
        }
    }
}
