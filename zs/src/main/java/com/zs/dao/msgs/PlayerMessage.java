package com.zs.dao.msgs;

import com.zs.models.contacts.bean.PersonModelBean;
import com.zs.models.device.bean.DevicePlayerBean;

/**
 * author: admin
 * date: 2018/05/30
 * version: 0
 * mail: secret
 * desc: CaptureMessage
 */

public class PlayerMessage {

    public int type;
    public String userId;
    public String userDomain;
    public String userName;
    public String userTokenId;
    public String content;

    public PlayerMessage(int type, String userId, String userDomain, String userName, String userTokenId, String content) {
        this.type = type;
        this.userId = userId;
        this.userDomain = userDomain;
        this.userName = userName;
        this.userTokenId = userTokenId;
        this.content = content;
    }
}
