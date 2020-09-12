package com.zs.models.meet.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.zs.dao.msgs.CallRecordMessage;

/**
 * author: admin
 * date: 2018/01/19
 * version: 0
 * mail: secret
 * desc: MeetList
 */

public class RecordMeetList implements Serializable {

    public int nResultCode;
    public String strResultDescribe;
    public int nTotalSize;
    public ArrayList<CallRecordMessage> lstMeetingInfo;
}
