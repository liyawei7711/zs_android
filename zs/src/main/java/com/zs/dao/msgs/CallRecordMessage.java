package com.zs.dao.msgs;

import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMsgCommonUDPMsg;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyKickUserMeeting;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.zs.R;
import com.zs.bus.CreateTalkAndVideo;
import com.zs.bus.TalkInvistor;
import com.zs.common.AppUtils;

/**
 * author: admin
 * date: 2018/01/17
 * version: 0
 * mail: secret
 * desc: MessageData
 */
public class CallRecordMessage {
    static final Gson gson = new Gson();

    public static final int NO_ACCEPT = 0;
    public static final int CALL_IN = 1;
    public static final int CALL_OUT = 2;


    public static final int TALK = 0;
    public static final int TALK_VIDEO = 1;
    public static final int MEET = 2;


        public int nMeetingID;//": 1,
        public String strMeetingName;//": "testMeeting",
        public String strMeetingDesc;//": "testDesc",

        public int nMode;//":1,
        public String strStartTime;//": "2018-01-01 10:10:00",
        public int nTimeDuration;//": 10,
        public int nStatus;//":1,
        public String strMainUserDomainCode;//": "testDomain",
        public String strMainUserID;//": "testUser",
        public String strMainUserName;//": "testUserName"
        public int nInitiaterObserve;//": "nForceInvite" 1 watch
        public int nRecordID;//": "nRecordId"
        public int nMeetingType;//1预约  0及时

        public boolean isWatch() {
            return nInitiaterObserve == 1;
        }

    public int getIsRecord() {
        return isRecord;
    }

    int isRecord;
    int nTalkType;
    int nAcceptType;
    public long nMillions;

    String strJoinMeetingJson;
    String strJoinTalkJson;
    String strCreateMeetJson;
    String strCreateTalkJson;

    int nMsgSessionID;
    String name;
    String domain;


    public int getnMsgSessionID() {
        return nMsgSessionID;
    }

    public void setnMsgSessionID(int nMsgSessionID) {
        this.nMsgSessionID = nMsgSessionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getnTalkType() {
        return nTalkType;
    }

    public void setnTalkType(int nTalkType) {
        this.nTalkType = nTalkType;
    }

    public int getnAcceptType() {
        return nAcceptType;
    }

    public void setnAcceptType(int nAcceptType) {
        this.nAcceptType = nAcceptType;
    }

    protected CallRecordMessage() {
        dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeSdf = new SimpleDateFormat("HH:mm:ss");
    }

    //呼出音视频
    public static CallRecordMessage from(CreateTalkAndVideo message) {
        CallRecordMessage data = new CallRecordMessage();
        data.nTalkType = message.hasVideo ? TALK_VIDEO : TALK;
        data.nAcceptType = CALL_OUT;
        data.strCreateTalkJson = gson.toJson(message);
        data.nMillions = System.currentTimeMillis();
        data.domain = message.id;
        data.name = message.name;
        data.isRecord=1;

        return data;
    }

    // 呼入音视频
    public static CallRecordMessage from(TalkInvistor talkInvistor) {
        CallRecordMessage data = new CallRecordMessage();
        if (talkInvistor.talk.getRequiredMediaMode() == SdkBaseParams.MediaMode.AudioAndVideo) {
            data.nTalkType = TALK_VIDEO;
        } else {
            data.nTalkType = TALK;
        }
        data.nAcceptType = NO_ACCEPT;
        data.strJoinTalkJson = gson.toJson(talkInvistor);
        data.nMillions = System.currentTimeMillis();
        data.nMsgSessionID = talkInvistor.talk.nMsgSessionID;
        if (talkInvistor.talk != null) {
            data.domain = talkInvistor.talk.strFromUserID;
            data.name = talkInvistor.talk.strFromUserName;
        }
        data.isRecord=1;

        return data;
    }

    public String getStrJoinMeetingJson() {
        return strJoinMeetingJson;
    }

    public String getStrJoinTalkJson() {
        return strJoinTalkJson;
    }

    public String getStrCreateMeetJson() {
        return strCreateMeetJson;
    }

    public String getStrCreateTalkJson() {
        return strCreateTalkJson;
    }

    SimpleDateFormat dateSdf;
    SimpleDateFormat timeSdf;

    public String getDate() {
        Calendar now = Calendar.getInstance();

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(nMillions);
        Date timeDate = new Date(nMillions);

        int dateOffset = now.get(Calendar.DAY_OF_YEAR) - time.get(Calendar.DAY_OF_YEAR);
        switch (dateOffset) {
            case 0:

                return timeSdf.format(timeDate);
            case 1:

                return AppUtils.getString(R.string.zuotian) + timeSdf.format(timeDate);
            case 2:

                return AppUtils.getString(R.string.qiantian) + timeSdf.format(timeDate);
            default:

                return dateSdf.format(timeDate);
        }
    }


    public String getDer() {
        switch (nTalkType) {
            case TALK:
                return "[语音] ID:";
            case TALK_VIDEO:
                return "[视频] ID:";
            case MEET:
                return "[会议] ID:";
        }
        return "";
    }

    public int getIconResource() {
        switch (nAcceptType) {
            case NO_ACCEPT:
                return R.drawable.tonghuajilu_laidianweijie;
            case CALL_IN:
                return R.drawable.tonghuajilu_laidianyijie;
            case CALL_OUT:
                return R.drawable.tonghuajilu_qudianyijie;
        }

        return 0;
    }
}
