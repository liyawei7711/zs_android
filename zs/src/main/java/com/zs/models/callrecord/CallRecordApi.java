package com.zs.models.callrecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.zs.dao.AppDatas;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.VssMessageListBean;
import com.zs.models.ModelCallback;
import com.zs.models.contacts.bean.PersonBean;
import com.zs.models.contacts.bean.SosPersonBean;
import com.zs.models.meet.bean.MeetList;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

public class CallRecordApi {
    String URL;
    SimpleDateFormat sdf;

    private CallRecordApi() {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static CallRecordApi get() {
        return new CallRecordApi();
    }

    public void getCallRecord(int nPage, int size, final ModelCallback<MeetList> callback) {
        URL =  AppDatas.Constants().getSieAddress()  + "get_talkback_list";
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -2);

        String startTime = sdf.format(c.getTime());
        String endTime = sdf.format(new Date());
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())

                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("nMode",1)
                .addParam("strUserDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID()+"")
                .addParam("strQueryStartTime", startTime)
                .addParam("strQueryEndTime",endTime)
                .addParam("nStatus",2)
                .addParam("nPage", nPage)
                .addParam("nSize", size)
                .addParam("nReverse", 2)
                .setHttpCallback(new ModelCallback<MeetList>() {
                    @Override
                    public void onSuccess(MeetList meetList) {
                        if (meetList != null && callback != null) {
                            callback.onSuccess(meetList);
                        }
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                    }
                })
                .build()
                .requestNowAsync();
    }
}
