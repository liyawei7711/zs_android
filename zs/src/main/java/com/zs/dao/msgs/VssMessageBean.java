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

public class VssMessageBean implements Serializable {

    
    public int type;
    
    public int groupType;
    
    public String groupDomainCode;
    
    public String groupID;
    
    public String sessionID;
    
    public String sessionName;
    
    public String fromUserId;
    
    public String fromUserTokenId;
    
    public String fromUserDomain;
    
    public String fromUserName;
    
    public String content;
    
    public ArrayList<SendUserBean> sessionUserList = new ArrayList<>();
    
    public long time;
    
    public String ownerId;
    
    public String ownerDomain;
    
    public boolean isRead;
    
    public int nEncrypt;

    public String contentSrc;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean contactUser(String string) {
        if (sessionUserList == null)
            return false;
        for (SendUserBean user : sessionUserList) {
            if (user.strUserName.contains(string)) {
                return true;
            }
        }

        if (fromUserName.contains(string)) {
            return true;
        }
        return false;
    }

    public boolean contactSessionName(String string) {
        if (sessionName == null) return false;
        if (string == null) return false;
        if (sessionName.contains(string)) return true;
        return false;
    }

    public boolean contactFromUserName(String string) {
        if (fromUserName == null) return false;
        if (string == null) return false;
        if (fromUserName.contains(string)) return true;
        return false;
    }

    public boolean contactSessionId(String string) {
        if (fromUserId == null) return false;
        if (string == null) return false;
        if (fromUserId.contains(string)) return true;
        return false;

    }

    public boolean contactContent(String string) {
        if (content == null) return false;
        if (string == null) return false;
        if (content.contains(string)) return true;
        return false;
    }
}
