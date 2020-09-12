package com.zs.ui.home.present;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkCaller;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.meet.CStartMeetingReq;
import com.huaiye.sdk.sdpmsgs.social.CNotifyUserStatus;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CGetTrunkChannelInfoRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CQueryTrunkChannelListRsp;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelBean;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.TrunkChannelUserBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zs.R;
import com.zs.bus.CreateMeet;
import com.zs.bus.CreateTalkAndVideo;
import com.zs.common.AppUtils;
import com.zs.common.dialog.DeviceListPopupWindow;
import com.zs.common.dialog.GroupListPopupWindow;
import com.zs.common.dialog.LogicDialog;
import com.zs.common.dialog.UserListPopupWindow;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.dao.AppDatas;
import com.zs.dao.msgs.ChangeUserBean;
import com.zs.dao.msgs.ChatUtil;
import com.zs.dao.msgs.VssMessageListBean;
import com.zs.dao.msgs.VssMessageListMessages;
import com.zs.models.ModelCallback;
import com.zs.models.callrecord.CallRecordApi;
import com.zs.models.contacts.ContactsApi;
import com.zs.models.contacts.bean.PersonBean;
import com.zs.models.contacts.bean.PersonModelBean;
import com.zs.models.device.DeviceApi;
import com.zs.models.device.bean.DeviceListBean;
import com.zs.models.device.bean.DevicePlayerBean;
import com.zs.models.meet.bean.MeetList;
import com.zs.ui.channel.ChannelDetailActivity;
import com.zs.ui.chat.ChatActivity;
import com.zs.ui.device.DevicePlayRealActivity;
import com.zs.ui.device.holder.DeviceHolder;
import com.zs.ui.device.holder.GroupHolder;
import com.zs.ui.device.holder.PersonHolder;
import com.zs.ui.home.holder.CallRecordListViewHolder;
import com.zs.ui.home.view.IContactsView;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DeviceListPresent
 */

public class CallRecordPresent {
    private final int PERSON = 0;
    private final int GROUP = 1;
    private final int DEVICE = 2;
    IContactsView iView;

    ArrayList<MeetList.Data> callRecordList = new ArrayList<>();
    LiteBaseAdapter<MeetList.Data> personAdapter;
    int total_person;
    int rel_person;


    boolean isLoad;
    int size = 20;
    int pagePerson = 1;

    public CallRecordPresent(final IContactsView iView) {
        this.iView = iView;
        personAdapter = new LiteBaseAdapter<>(iView.getContext(),
                callRecordList,
                CallRecordListViewHolder.class,
                R.layout.item_call_record_list_view,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        VssMessageListBean bean = (VssMessageListBean) v.getTag();
//                        bean.isRead = 1;
//                        VssMessageListMessages.get().isRead(bean);
//
//                        Intent intent = new Intent(CallRecordActivity.this, ChatActivity.class);
//                        intent.putExtra("listBean", bean);
//                        startActivity(intent);
//
//                        adapter.notifyDataSetChanged();
//                        callRecordDialog.show();
                    }
                }, "");
        personAdapter.setLoadListener(new LiteBaseAdapter.LoadListener() {
            @Override
            public boolean isLoadOver() {
                return !isLoad;
            }

            @Override
            public boolean isEnd() {
                return rel_person >= total_person;
            }

            @Override
            public void lazyLoad() {
                loadPerson(false);
            }
        });
    }

    public void loadPerson(final boolean isRef) {

        if (isRef)
            pagePerson = 1;
        else
            pagePerson++;

        isLoad = true;
//        CallRecordApi.get().getCallRecord(pagePerson, size, new ModelCallback<MeetList>() {
//                    @Override
//                    public void onSuccess(MeetList talk) {
//                        total_person = talk.nTotalSize;
//                        loadFinish();
//                        if (isRef) {
//                            rel_person = 0;
//                            callRecordList.clear();
//                        }
//                        if (talk.lstTalkbackInfo != null) {
//                            rel_person += talk.lstTalkbackInfo.size();
//
//                            for (MeetList.Data bean : talk.lstTalkbackInfo) {
//
//                                callRecordList.add(bean);
//                            }
//                        }
//                        changeViewShow(callRecordList.isEmpty());
//                        personAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onFailure(HTTPResponse httpResponse) {
//                        super.onFailure(httpResponse);
//                        loadFinish();
//                        iView.getContext().showToast(AppUtils.getString(R.string.get_person_false));
//                    }
//                });
    }


    private void loadFinish() {
        isLoad = false;
        iView.getRefView().setRefreshing(false);
    }


    public RecyclerView.Adapter getPersonAdapter() {
        isLoad = false;
        return personAdapter;
    }


    public ArrayList<MeetList.Data> getPersonList() {
        return callRecordList;
    }


    public void changeViewShow(boolean isEmpty) {
        if (isEmpty) {
            iView.getEmptyView().setVisibility(View.VISIBLE);
            iView.getListView().setVisibility(View.GONE);
        } else {
            iView.getEmptyView().setVisibility(View.GONE);
            iView.getListView().setVisibility(View.VISIBLE);
        }
    }

}
