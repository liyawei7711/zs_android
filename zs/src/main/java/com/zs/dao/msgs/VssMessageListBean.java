package com.zs.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * author: admin
 * date: 2018/05/28
 * version: 0
 * mail: secret
 * desc: VssMessageBean
 */

public class VssMessageListBean implements Serializable {

    public int type;
    public int groupType;
    public String groupDomainCode;
    public String groupID;
    public String sessionID;
    public String sessionName;
    public String lastUserId;
    public String lastUserDomain;
    public String lastUserName;
    public String content;
    public ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
    public long time;
    public String ownerId;
    public String ownerDomain;
    public int isRead;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
