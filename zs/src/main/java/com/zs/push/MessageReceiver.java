package com.zs.push;

import android.annotation.SuppressLint;

import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageCmExitRsp;
import com.huaiye.cmf.sdp.SdpMessageCmInitRsp;
import com.huaiye.cmf.sdp.SdpMessageCmKeyExpiredNotify;
import com.huaiye.cmf.sdp.SdpMsgFRAlarmNotify;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.core.SdkNotifyCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiEncrypt;
import com.huaiye.sdk.sdkabi._api.ApiFace;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._api.ApiTalk;
import com.huaiye.sdk.sdkabi._params.SdkBaseParams;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi.common.SDKUtils;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.face.CServerNotifyAlarmInfo;
import com.huaiye.sdk.sdpmsgs.io.CNotifyReconnectStatus;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserCancelJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyInviteUserJoinMeeting;
import com.huaiye.sdk.sdpmsgs.meet.CNotifyMeetingStatusInfo;
import com.huaiye.sdk.sdpmsgs.social.CNotifyMsgToUser;
import com.huaiye.sdk.sdpmsgs.social.COfflineMsgToUserReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyTalkbackStatusInfo;
import com.huaiye.sdk.sdpmsgs.talk.CNotifyUserJoinTalkback;
import com.huaiye.sdk.sdpmsgs.talk.trunkchannel.CNotifyUserInviteTrunkChannel;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.ChannelInvistor;
import com.zs.bus.LocalFaceAlarm;
import com.zs.bus.NetStatusChange;
import com.zs.bus.RefMessage;
import com.zs.bus.ServerFaceAlarm;
import com.zs.bus.TalkInvistor;
import com.zs.common.AlarmMediaPlayer;
import com.zs.common.AppUtils;
import com.zs.common.rx.RxUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.CallRecordMessage;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.ChangeUserBean;
import com.zs.dao.msgs.ChatUtil;
import com.zs.dao.msgs.MapMarkBean;
import com.zs.dao.msgs.PlayerMessage;
import com.zs.dao.msgs.StopCaptureMessage;
import com.zs.dao.msgs.VssMessageBean;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.ui.home.MainZSActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.huaiye.sdk.sdkabi._params.SdkBaseParams.ConnectionStatus.Connecting;
import static com.zs.common.AppUtils.BROADCAST_AUDIO_FILE_TYPE_INT;
import static com.zs.common.AppUtils.BROADCAST_AUDIO_TYPE_INT;
import static com.zs.common.AppUtils.BROADCAST_VIDEO_TYPE_INT;
import static com.zs.common.AppUtils.CAPTURE_TYPE_INT;
import static com.zs.common.AppUtils.MSG_TYPE_DIANZI_BOADER;
import static com.zs.common.AppUtils.MSG_TYPE_DIANZI_BOADER_CHANGE;
import static com.zs.common.AppUtils.MSG_TYPE_MARK;
import static com.zs.common.AppUtils.MSG_TYPE_USER;
import static com.zs.common.AppUtils.MSG_TYPE_USER_GPS;
import static com.zs.common.AppUtils.PLAYER_TYPE_DEVICE_INT;
import static com.zs.common.AppUtils.PLAYER_TYPE_INT;
import static com.zs.common.AppUtils.PLAYER_TYPE_PERSON_INT;
import static com.zs.common.AppUtils.STOP_CAPTURE_TYPE_INT;
import static com.zs.common.AppUtils.ZHILING_CODE_TYPE_INT;
import static com.zs.common.AppUtils.ZHILING_FILE_TYPE_INT;
import static com.zs.common.AppUtils.ZHILING_IMG_TYPE_INT;
import static com.zs.common.AppUtils.ctx;
import static com.zs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: PushMessageReceiver
 */

public class MessageReceiver {

    private static final String TAG = MessageReceiver.class.getSimpleName();
    SimpleDateFormat sdf;
    Gson gson = new Gson();

    long currentTime_Connecting;
    long currentTime_Connected;
    RxUtils rxUtils;

    static class Holder {
        static MessageReceiver SINGLETON = new MessageReceiver();
    }

    public static MessageReceiver get() {
        return Holder.SINGLETON;
    }

    @SuppressLint("SimpleDateFormat")
    private MessageReceiver() {

        rxUtils = new RxUtils<>();

        HYClient.getModule(ApiTalk.class).observeTalkingStatus(new SdkNotifyCallback<CNotifyTalkbackStatusInfo>() {
            @Override
            public void onEvent(CNotifyTalkbackStatusInfo info) {
                Logger.debug(TAG, "CNotifyTalkbackStatusInfo " + info.toString());
                EventBus.getDefault().post(info);
            }
        });


        HYClient.getModule(ApiMeet.class).observeMeetingStatus(new SdkNotifyCallback<CNotifyMeetingStatusInfo>() {
            @Override
            public void onEvent(CNotifyMeetingStatusInfo info) {
                EventBus.getDefault().post(info);
            }
        });

        /**
         * 频道邀请
         */
        HYClient.getModule(ApiTalk.class).observerUserInviteTrunkChannel(new SdkNotifyCallback<CNotifyUserInviteTrunkChannel>() {
            @Override
            public void onEvent(CNotifyUserInviteTrunkChannel data) {
                long millis = System.currentTimeMillis();

                EventBus.getDefault().post(new RefMessage());
                EventBus.getDefault().post(new ChannelInvistor(data, millis));
            }
        });

        // 对讲邀请
        HYClient.getModule(ApiTalk.class).observeInviteTalking(new SdkNotifyCallback<CNotifyUserJoinTalkback>() {
            @Override
            public void onEvent(CNotifyUserJoinTalkback cNotifyUserJoinTalkback) {
                long millis = System.currentTimeMillis();

                EventBus.getDefault().post(new RefMessage());
                TalkInvistor invistor = new TalkInvistor(cNotifyUserJoinTalkback, null, millis);
                EventBus.getDefault().post(invistor);
            }
        });

        // 会议邀请
        HYClient.getModule(ApiMeet.class).observeInviteMeeting(new SdkNotifyCallback<CNotifyInviteUserJoinMeeting>() {
            @Override
            public void onEvent(CNotifyInviteUserJoinMeeting cNotifyInviteUserJoinMeeting) {
                long millis = System.currentTimeMillis();
                if (!cNotifyInviteUserJoinMeeting.isSelfMeetCreator()) {
                }
                EventBus.getDefault().post(new RefMessage());
            }
        });
        HYClient.getModule(ApiMeet.class).observeMeetingInviteCancel(new SdkNotifyCallback<CNotifyInviteUserCancelJoinMeeting>() {
            @Override
            public void onEvent(CNotifyInviteUserCancelJoinMeeting cNotifyInviteUserCancelJoinMeeting) {
                EventBus.getDefault().post(cNotifyInviteUserCancelJoinMeeting);
            }
        });

        // SDK登录被踢出
        HYClient.getModule(ApiAuth.class).observeBeKickedOut(new SdkNotifyCallback<CNotifyUserKickout>() {
            @Override
            public void onEvent(final CNotifyUserKickout cNotifyUserKickout) {
//                AppMessages.get().add(MessageData.from(cNotifyUserKickout));
                showToast(AppUtils.getString(R.string.kitout_load));
                AuthApi.get().logout(MCApp.getInstance(), new ModelCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        AppAuth.get().clear();
                        HYClient.getModule(ApiAuth.class).logout(null);
                        EventBus.getDefault().post(cNotifyUserKickout);
                    }
                });
            }
        });

        // 网络状态
        HYClient.getModule(ApiIO.class).observeConnectionStatus(new SdkNotifyCallback<CNotifyReconnectStatus>() {
            @Override
            public void onEvent(final CNotifyReconnectStatus cNotifyReconnectStatus) {
                if (cNotifyReconnectStatus.getConnectionStatus() == SdkBaseParams.ConnectionStatus.Connected) {
                    if (System.currentTimeMillis() - currentTime_Connected < 1000) {
                        return;
                    }

                    currentTime_Connected = System.currentTimeMillis();
                    if (!HYClient.getSdkOptions().User().isRegistered()) {
                        //用户未登录,啥都不干
                        return;
                    }

                } else {
                    if (cNotifyReconnectStatus.getConnectionStatus() == Connecting) {
                        if (System.currentTimeMillis() - currentTime_Connecting < 4 * 1000) {
                            return;
                        }
                        currentTime_Connecting = System.currentTimeMillis();
                    }

                    EventBus.getDefault().post(new NetStatusChange(cNotifyReconnectStatus.getConnectionStatus()));
                }
            }
        });

        // 本地告警
        HYClient.getModule(ApiFace.class).observeLocalFaceAlarm(new SdkNotifyCallback<SdpMsgFRAlarmNotify>() {
            @Override
            public void onEvent(SdpMsgFRAlarmNotify sdpMsgFRAlarmNotify) {
                AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_ALARM_VOICE);
                EventBus.getDefault().post(new LocalFaceAlarm(sdpMsgFRAlarmNotify));
            }
        });

        // 服务器告警
        HYClient.getModule(ApiFace.class).observeServerFaceAlarm(new SdkNotifyCallback<CServerNotifyAlarmInfo>() {
            @Override
            public void onEvent(CServerNotifyAlarmInfo cServerNotifyAlarmInfo) {
                AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_ALARM_VOICE);
                EventBus.getDefault().post(new ServerFaceAlarm(cServerNotifyAlarmInfo));
            }
        });

        // 监听 秘钥过期
        HYClient.getModule(ApiEncrypt.class).observeEncryptKeyExpired(new SdkNotifyCallback<SdpMessageCmKeyExpiredNotify>() {
            public void onEvent(SdpMessageCmKeyExpiredNotify data) {
                if (AppUtils.nEncryptPasswd.isEmpty()) {
                    return;
                }
                HYClient.getModule(ApiEncrypt.class).encryptUnInit(SdkParamsCenter.Encrypt.EncryptUnInit(), new SdkCallback<SdpMessageCmExitRsp>() {
                    @Override
                    public void onSuccess(SdpMessageCmExitRsp resp) {
                        HYClient.getModule(ApiEncrypt.class).encryptInit(SdkParamsCenter.Encrypt.EncryptInit()
                                .setUserId(HYClient.getSdkOptions().User().getUserId())
                                .setTfCardRoot(SDKUtils.getStoragePath(HYClient.getContext(), true))
                                .setPackageName(HYClient.getContext().getPackageName())
                                .setPsw(AppUtils.nEncryptPasswd)
                                .setFileName("rt_sech2.bin"), new SdkCallback<SdpMessageCmInitRsp>() {

                            @Override
                            public void onSuccess(SdpMessageCmInitRsp sdpMessageCmInitRsp) {
                                showToast("重新初始化成功 ");
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                showToast("加密初始化失败" + errorInfo.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        showToast("去初始化失败.");
                    }
                });
            }
        });


        // 监听 离线消息
        Logger.debug("监听 离线消息");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HYClient.getModule(ApiSocial.class).observeOfflineMessages(new SdkNotifyCallback<COfflineMsgToUserReq>() {
            @Override
            public void onEvent(COfflineMsgToUserReq data) {

                for (COfflineMsgToUserReq.Message msg : data.listMsg) {

                    try {
                        JSONObject jsonObject = new JSONObject(msg.strMsg);
                        long millions = sdf.parse(msg.strDateTime).getTime();

                        JSONObject msgBody = jsonObject.getJSONObject("msgbody");

                        if (msgBody.has("nMeetingID")) {
                            CNotifyInviteUserJoinMeeting meetInvite = gson.fromJson(jsonObject.getString("msgbody"), CNotifyInviteUserJoinMeeting.class);
                            try {
                                if (meetInvite != null) {
                                }
                            } catch (Exception e) {

                            }
                        } else if (msgBody.has("nTalkbackID")) {

                            CNotifyUserJoinTalkback talkInvite = gson.fromJson(jsonObject.getString("msgbody"), CNotifyUserJoinTalkback.class);
                        } else if (msgBody.has("strMsgbody")) {
                            VssMessageBean bean = gson.fromJson(msgBody.getString("strMsgbody"), VssMessageBean.class);
                            switch (bean.type) {
                                case CAPTURE_TYPE_INT:
                                case STOP_CAPTURE_TYPE_INT:
                                case PLAYER_TYPE_INT:
                                case PLAYER_TYPE_PERSON_INT:
                                case PLAYER_TYPE_DEVICE_INT:
                                case MSG_TYPE_MARK:
                                case MSG_TYPE_USER:
                                case MSG_TYPE_DIANZI_BOADER:
                                case MSG_TYPE_DIANZI_BOADER_CHANGE:
                                    break;
                                case ZHILING_CODE_TYPE_INT:
                                case ZHILING_IMG_TYPE_INT:
                                case ZHILING_FILE_TYPE_INT:
                                case BROADCAST_AUDIO_TYPE_INT:
                                case BROADCAST_VIDEO_TYPE_INT:
                                case BROADCAST_AUDIO_FILE_TYPE_INT:
                                    if (!(bean.fromUserDomain.equals(AppAuth.get().getDomainCode())
                                            && bean.fromUserId.equals(AppAuth.get().getUserID() + "")))
                                        AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_ZHILLING_VOICE);

                                    ChatUtil.get().saveChangeMsg(bean, true);
                                    break;
                                default:
                                    break;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                EventBus.getDefault().post(new RefMessage());

            }
        });
        HYClient.getModule(ApiSocial.class).observeOnlineMessages(new SdkNotifyCallback<CNotifyMsgToUser>() {
            @Override
            public void onEvent(CNotifyMsgToUser data) {

                VssMessageBean bean = gson.fromJson(data.strMsg, VssMessageBean.class);
                switch (bean.type) {
                    case CAPTURE_TYPE_INT:
                        EventBus.getDefault().post(new CaptureMessage(bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.sessionID));
                        break;
                    case STOP_CAPTURE_TYPE_INT:
                        EventBus.getDefault().post(new StopCaptureMessage(bean.fromUserId, bean.fromUserDomain, bean.fromUserName));
                        break;
                    case PLAYER_TYPE_INT:
                        EventBus.getDefault().post(new PlayerMessage(bean.type, bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.fromUserTokenId, bean.content));
                        break;
                    case PLAYER_TYPE_PERSON_INT:
                    case PLAYER_TYPE_DEVICE_INT:
                        ChatUtil.get().saveChangeMsg(bean, true);
                        break;
                    case MSG_TYPE_MARK:
                        MapMarkBean mapMarkBean = gson.fromJson(bean.content, MapMarkBean.class);
                        EventBus.getDefault().post(mapMarkBean);
                        break;
                    case MSG_TYPE_USER:
                        ChangeUserBean changeUserBean = gson.fromJson(bean.content, ChangeUserBean.class);
                        EventBus.getDefault().post(changeUserBean);
                        break;
                    case MSG_TYPE_USER_GPS:
                        CNotifyGPSStatus gpsStatus = gson.fromJson(bean.content, CNotifyGPSStatus.class);
                        EventBus.getDefault().post(gpsStatus);
                        break;
                    case MSG_TYPE_DIANZI_BOADER:
                    case MSG_TYPE_DIANZI_BOADER_CHANGE:
                        break;
                    case ZHILING_CODE_TYPE_INT:
                    case ZHILING_IMG_TYPE_INT:
                    case ZHILING_FILE_TYPE_INT:
                    case BROADCAST_AUDIO_TYPE_INT:
                    case BROADCAST_VIDEO_TYPE_INT:
                    case BROADCAST_AUDIO_FILE_TYPE_INT:
                        if (!(bean.fromUserDomain.equals(AppAuth.get().getDomainCode())
                                && bean.fromUserId.equals(AppAuth.get().getUserID() + "")))
                            AlarmMediaPlayer.get().play(AlarmMediaPlayer.SOURCE_ZHILLING_VOICE);

                        ChatUtil.get().saveChangeMsg(bean, true);
                        break;
                    default:
                        break;
                }

            }
        });
    }


    private void userLogin(final CQueryUserListRsp.UserInfo resp, final CNotifyReconnectStatus cNotifyReconnectStatus) {
        try {
            EventBus.getDefault().post(new TalkInvistor(null, null, 0));

            EventBus.getDefault().post(new NetStatusChange(cNotifyReconnectStatus.getConnectionStatus()));
        } catch (Exception e) {

        }
    }
}
