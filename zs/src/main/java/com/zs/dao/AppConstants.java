package com.zs.dao;

import com.zs.common.SP;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AppConstants
 */

public class AppConstants {
    //服务器登录
    static String strAddress = "36.152.32.85";
    static String nPort = "8086";

    static String strSieAddress = "36.154.50.211";
    static String strSiePort = "9001";

    static String strSiePlayerAddress = "36.154.50.211";
    static String strSiePlayerSiePort = "554";

    protected AppConstants() {
        strAddress = SP.getParam("IP", strAddress).toString();
        nPort = SP.getParam("Port", nPort).toString();
    }

    public static String getSieAddressIP() {
        return strSieAddress;
    }

    public static String getSieAddressPort() {
        return strSiePort;
    }

    public static String getSiePlayerddressIP() {
        return strSiePlayerAddress;
    }

    public static String getSiePlayerAddressPort() {
        return strSiePlayerSiePort;
    }

    public static void setAddress(String ip, String port) {
        strAddress = ip;
        nPort = port;

        SP.setParam("IP", strAddress);
        SP.setParam("Port", nPort);
    }


    public static String getSieAddress() {
        return "http://" + strSieAddress + ":" + strSiePort + "/";
    }

    public static String getAddressBaseURL() {
        return "http://" + strAddress + ":" + nPort + "/";
    }

    public String getAddressWithoutPort() {
        return "http://" + strAddress;
    }

    public String getAddressBaseURLTarget() {
        return "http://" + strAddress + ":" + nPort + "/ECSFileServer/";
    }

}
