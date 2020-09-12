package com.zs.dao.msgs;

/**
 * author: admin
 * date: 2018/05/30
 * version: 0
 * mail: secret
 * desc: CaptureMessage
 */

public class StopCaptureMessage {
    public String fromUserId;
    public String fromUserDomain;
    public String fromUserName;

    public StopCaptureMessage(String fromUserId, String fromUserDomain, String fromUserName) {
        this.fromUserId = fromUserId;
        this.fromUserDomain = fromUserDomain;
        this.fromUserName = fromUserName;
    }
}
