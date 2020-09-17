package com.zs.dao.msgs;

import com.zs.common.AppUtils;


/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: MessageData
 */
public class BroadcastMessage {


    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int DOWNING = 3;

    public static final int TYPE_AUDIO = 1;
    public static final int TYPE_VIDEO = 2;


    int type;
    int state;
    String down_path;
    String save_path;

    String userId;

    public BroadcastMessage(int type, String path) {
        this.type = type;
        down_path = path;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDown_path() {
        return down_path;
    }

    public void setDown_path(String down_path) {
        this.down_path = down_path;
    }

    public String getSave_path() {
        return save_path;
    }

    public void setSave_path(String save_path) {
        this.save_path = save_path;
    }
}
