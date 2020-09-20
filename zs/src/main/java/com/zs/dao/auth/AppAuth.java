package com.zs.dao.auth;

import android.text.TextUtils;

import com.zs.common.SP;
import com.zs.models.auth.bean.AuthUser;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppAuth
 */

public class AppAuth {
    public static final String PWD = "password";
    public static final String SIE_HTTP_PORT = "nSieHttpPort";

    private AppAuth() {

    }

    public void setData(String key, String strUserTokenID) {
        put(key, strUserTokenID);
    }

    public String getData(String key) {
        return get(key);
    }


    static class Holder {
        static final AppAuth SINGLETON = new AppAuth();
    }

    public static AppAuth get() {
        return Holder.SINGLETON;
    }

    public void clear() {
        put("strUserID", "");
        put("strUserName", "");
        put("loginName", "");
        put("strSieIP", "");
        put("nSiePort", "");
        put("strSiePlayIP", "");
        put("nSiePlayPort", "");
        put("web_h5", "");
        put("strToken", "");
        put("strTokenHY", "");
        put("AnJianBean", "");
    }

    public void setAuthUser(AuthUser user) {
        // 缓存
        put("strUserID", user.getUserEntity().getId());
        put("strUserName", user.getUserEntity().getUsername());
        put("loginName", user.getUserEntity().getMobile());
//        put("strDomainCode", user.strDomainCode);
//        put("nPriority", user.nPriority);
//
        if (user.getServer() != null) {
            put("strSieIP", user.getServer().getMediaUrl());
            put("nSiePort", user.getServer().getMediaPort());

            put("strSiePlayIP", user.getServer().getRtspUrl());
            put("nSiePlayPort", user.getServer().getRtspPort());

            put("web_h5", user.getServer().getMediaWebUrl());

        }
        put("strToken", user.getData());
//        put(SIE_HTTP_PORT, user.nSieHttpPort);
    }

    public void setNoCenterUser(String user) {
        put("noCenterUser", user);
    }

    public String getNoCenterUser() {
        return get("noCenterUser");
    }

    public String getToken() {
        return get("strToken");
    }

    public String getTokenHY() {
        return get("strTokenHY");
    }

    public String getH5Web() {
        return get("web_h5");
    }

    public void put(String key, Object code) {
        SP.setParam(key, code);
    }

    private String get(String key) {
        return SP.getParam(key, "").toString();
    }

    private String get(String key, String def) {
        return SP.getParam(key, def).toString();
    }

    public String getPassword() {
        return get(PWD);
    }

    public String getUserID() {
        try {
            return get("strUserID");
        } catch (Exception e) {
            return "";
        }
    }

    public int getPriority() {
        try {
            return SP.getInteger("nPriority", 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getAnJian() {
        return get("AnJianBean");
    }

    public String getUserName() {
        return get("strUserName");
    }

    public String getUserLoginName() {
        return get("loginName");
    }

    public String getDomainCode() {
        return get("strDomainCode");
    }

    public String getSieIP() {
        return get("strSieIP");
    }

    public int getSiePort() {
        return Integer.parseInt(get("nSiePort", "9000"));
    }

    public int getSieHttpPort() {
        try {
            return Integer.parseInt(get(SIE_HTTP_PORT, "8000"));
        } catch (Exception e) {
            return 8000;
        }
    }


    public void setEncryptPsw(String encryptPsw) {
        String strUserID = get("strUserID");
        if (TextUtils.isEmpty(strUserID)) {
            return;
        }
        put(strUserID + "_encrypt_psw", encryptPsw);
    }

    public String getEncryptPsw() {
        String strUserID = get("strUserID");
        if (TextUtils.isEmpty(strUserID)) {
            return "";
        }
        String psw = get(strUserID + "_encrypt_psw");
        return "12345678";
    }

}
