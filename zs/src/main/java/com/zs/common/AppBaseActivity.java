package com.zs.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.huaiye.cmf.sdp.SdpMsgFindLanCaptureDeviceRsp;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi.common.PushService;
import com.huaiye.sdk.sdpmsgs.auth.CSetKeepAliveIntervalRsp;
import com.huaiye.sdk.sdpmsgs.meet.CGetMeetingInfoRsp;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;
import com.zs.MCApp;
import com.zs.bus.AcceptDiaoDu;
import com.zs.bus.CaptureZhiFaMessage;
import com.zs.bus.LocalFaceAlarm;
import com.zs.bus.LogoutBean;
import com.zs.bus.LowPowerMsg;
import com.zs.bus.NetStatusChange;
import com.zs.bus.ServerFaceAlarm;
import com.zs.bus.TalkInvistor;
import com.zs.bus.ZhiFaClickMessage;
import com.zs.common.dialog.LogicDialog;
import com.zs.common.dialog.LogicTimeDialog;
import com.zs.common.dialog.ZeusLoadView;
import com.zs.common.rx.RxUtils;
import com.zs.common.views.NavigateView;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.PlayerMessage;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.KickOutUIObserver;
import com.zs.models.auth.bean.AuthUser;
import com.zs.ui.auth.LoginActivity;
import com.zs.ui.guide.WelcomeActivity;
import com.zs.ui.home.KeyCodeSettingActivity;
import com.zs.ui.home.LiangDuActivity;
import com.zs.ui.home.MainZSActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static com.zs.common.AppUtils.ctx;
import static com.zs.common.AppUtils.jiaSuDevice;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: AppBaseActivity
 */

public abstract class AppBaseActivity extends FragmentActivity {//implements MessageObserver {

    public static final int P2P_MAX_TRY = 6;
    private int count_connecting = 0;

    protected FrameLayout userContent;
    protected NavigateView navigate;

    protected LogicDialog mLogicDialog;
    protected LogicTimeDialog mLogicTimeDialog;
    public ZeusLoadView mZeusLoadView;
    protected Map<String, CQueryUserListRsp.UserInfo> map = new HashMap<>();
    private LinearLayout.LayoutParams full_content;
    protected boolean isResumed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.shiftLanguage();

        if (!(this instanceof LoginActivity)) {
            ((MCApp) getApplicationContext()).addActivity(this);
        }

        Log.i("AppBaseOpen", "open_activity " + this.getClass().getSimpleName());
        // Content 初始化
        LinearLayout contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);

        userContent = new FrameLayout(this);
        Injectors.get().inject(Finder.View, userContent, this);

        navigate = new NavigateView(this);

        full_content = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        contentView.addView(navigate, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        contentView.addView(userContent, full_content);


        //在安卓4.4上,硬件加速会耗费大量的内存,4.4的手机内存普遍不高.会造成绘制错误,所以在4.4以及以下停用硬件加速
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && !(this instanceof MainZSActivity) && !jiaSuDevice.contains(Build.MODEL)) {
            contentView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            getWindow().getDecorView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            Log.d("FEIFEIFEI", " not open hard layer");
        }
        setContentView(contentView);

        mZeusLoadView = new ZeusLoadView(this);

        mLogicDialog = new LogicDialog(this);
        mLogicTimeDialog = new LogicTimeDialog(this);


        initActionBar();
        init();

    }

    private void init() {
        new RxUtils().doDelay(10, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                doInitDelay();
            }
        }, "init");
    }

    protected abstract void initActionBar();

    public abstract void doInitDelay();


    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;

        String strLiangDu = AppAuth.get().get("liangdu");
        if(TextUtils.isEmpty(strLiangDu)) {
        } else {
            int liangdu = Integer.parseInt(strLiangDu);
            Window window = getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.screenBrightness = liangdu;
            window.setAttributes(lp);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        // 后台接受消息
        if (AppBaseActivity.this instanceof LoginActivity ||
                AppBaseActivity.this instanceof WelcomeActivity) {
            return;
        }
        PushService.actionStop(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppBaseActivity.this instanceof LoginActivity ||
                AppBaseActivity.this instanceof WelcomeActivity) {
            return;
        }
        PushService.actionStart(this, new PushService.IKeepLiveListener() {
            @Override
            public void onReceiverMsg(CSetKeepAliveIntervalRsp cSetKeepAliveIntervalRsp, boolean b) {
                Logger.debug("pushService onReceiverMsg start");
                if (cSetKeepAliveIntervalRsp != null) {
                    Logger.debug("pushService onReceiverMsg " + cSetKeepAliveIntervalRsp.toString());
                    Logger.debug("pushService onReceiverMsg  isStop " + b);
                }
                if (AppBaseActivity.this instanceof MainZSActivity) {
                    return;
                }
                if (cSetKeepAliveIntervalRsp.nResultCode == 1720200007) {//彻底掉线
                    needLoad();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        ((MCApp) getApplicationContext()).removeActivity(this);
        PushService.actionStop(this);

        EventBus.getDefault().unregister(this);
        Log.i("AppBaseClose", "close_activity " + this.getClass().getSimpleName());
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AcceptDiaoDu status) {
        if (this instanceof MainZSActivity) {

        } else {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CGetMeetingInfoRsp status) {
        if (this instanceof MainZSActivity) {

        } else {
            finish();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKickedOut(LogoutBean data) {
        if (data.isClearAll()) {
            finish();
        } else {
            if (!(this instanceof LoginActivity)) {
                finish();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLowPower(LowPowerMsg msg) {
        if (this == MCApp.getInstance().getTopActivity()) {
            getLogicDialog()
                    .setTitleText("提醒")
                    .setMessageText("当前电量过低,请留意")
                    .setConfirmText("知道了")
                    .show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkStatusChanged(NetStatusChange data) {
        if (data == null) {
            return;
        }
        Logger.debug("onNetworkStatusChanged " + data.data.name() + " " + data.data.ordinal());
        if (this instanceof LoginActivity) {
            return;
        }
        if (MCApp.getInstance().getTopActivity() != null && this != MCApp.getInstance().getTopActivity()) {
            return;
        }


        switch (data.data) {
            case Connected:
                Logger.debug("onNetworkStatusChanged Connected stopP2P ");
                stopP2P();
                break;
            case Disconnected:
                //告知界面,关闭各种采集
                EventBus.getDefault().post(new KickOutUIObserver.UIKickout());

                if (data.haveTryLogin) {
                    //已经尝试过自动登录还是掉线
                    onDisconnected();
                } else {
                    // p2p超过2分钟后,业务服务器会把用户踢掉,这时候掉线再尝试下登录
                    String account = AppAuth.get().getUserLoginName();

                    if (!TextUtils.isEmpty(account)) {
                        AuthApi.get().login(this, account, new ModelCallback<AuthUser>() {

                            @Override
                            public void onSuccess(AuthUser authUser) {
                                stopP2P();
                            }

                            @Override
                            public void onFailure(HTTPResponse httpResponse) {
                                super.onFailure(httpResponse);
                                onDisconnected();
                            }
                        });
                    } else {
                        onDisconnected();
                    }
                }


                break;
            case Connecting:
//                if (HYClient.getSdkOptions().P2P().isSupportP2P()) {
//                    if (HYClient.getSdkOptions().P2P().isP2PRunning()) {
//                        return;
//                    }
//                    count_connecting++;
//                    if (count_connecting > P2P_MAX_TRY) {
//                        startP2P();
//                    } else {
//                        p2pConnectRetry(P2P_MAX_TRY - count_connecting + 1);
//                    }
//                }

                if (!AppUtils.isHide && !HYClient.getSdkOptions().P2P().isSupportP2P()) {
//                    showToast(AppUtils.getString(R.string.has_server_connecting));
                }
                break;
        }
    }

    /**
     * @param remainTry 还需要尝试几次
     */
    public void p2pConnectRetry(int remainTry) {

    }

    public void startP2P() {
    }

    public void stopP2P() {
        count_connecting = 0;
    }


    private void onDisconnected() {
        //p2p的情况下,断链了也不提示
        if (!HYClient.getSdkOptions().P2P().isP2PRunning()) {
            Logger.debug("onNetworkStatusChanged Disconnected and not in p2p");
            needLoad();
        }
    }

    /**
     *
     */
    private void needLoad() {
        if (MCApp.getInstance().getTopActivity() != this) {
            return;
        }
        ((MCApp) ctx).gotoLogin(true);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkInvite(TalkInvistor data) {
        if (data == null) {
            return;
        }
        if (data.talk == null && data.p2p_talk == null) {
            return;
        }

        if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
            return;
        }
        closeWhenInvite();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerFaceAlarm(ServerFaceAlarm data) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocalFaceAlarm(LocalFaceAlarm data) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CaptureMessage bean) {
        if (AppUtils.isTalk || AppUtils.isVideo || AppUtils.isMeet) {
            return;
        }
        closeWhenInvite();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CaptureZhiFaMessage bean) {
        closeWhenInvite();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ZhiFaClickMessage bean) {
        closeWhenInvite();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PlayerMessage bean) {
        closeWhenInvite();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SdpMsgFindLanCaptureDeviceRsp bean) {
        closeWhenInvite();
    }


    public void showToast(String text) {
        AppUtils.showToast(text);
    }

    public LogicDialog getLogicDialog() {
        return mLogicDialog;
    }

    public LogicTimeDialog getLogicTimeDialog() {
        return mLogicTimeDialog;
    }

    public NavigateView getNavigate() {
        return navigate;
    }

    public AppBaseActivity getSelf() {
        return this;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!(this instanceof KeyCodeSettingActivity)) {
            if (keyCode == AppUtils.ptt_key) {
                if (isStart) {
                    isStart = false;
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    boolean isStart;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!(this instanceof KeyCodeSettingActivity)) {
            if (keyCode == AppUtils.ptt_key) {
                if (!isStart) {
                    isStart = true;
                }
            } else if (keyCode == AppUtils.sos_key) {
                if (AppUtils.sos_key != -1) {
                    EventBus.getDefault().post(new ZhiFaClickMessage());
                }
            } else if (keyCode == AppUtils.camera_key) {
                if (AppUtils.camera_key != -1) {
                }
            } else if (keyCode == AppUtils.video_key) {
                if (AppUtils.video_key != -1) {
                    EventBus.getDefault().post(new CaptureZhiFaMessage());
                }
            } else if (keyCode == AppUtils.ptt_channel_left) {
                if (AppUtils.ptt_channel_left != -1) {
                }
            } else if (keyCode == AppUtils.ptt_channel_right) {
                if (AppUtils.ptt_channel_right != -1) {
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 当有邀请语音,视频,会议的时候.关掉当前页面,只留下首页
     * 如果当前APP在后台,直接拉起首页
     */
    private void closeWhenInvite() {
        if (this instanceof MainZSActivity) {
            if (AppUtils.isHide) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ScreenNotify screenNotify = ScreenNotify.get();
                        screenNotify.wakeUpAndUnlock();
                        screenNotify.openApplicationFromBackground();
                    }
                }, 1000);
            }
        } else {
            finish();
        }
    }


    /**
     * 退出应用广播Receiver
     */
//    class ExitAppReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals("com.huaiye.vvs.exitapp")) {
//                if (AppBaseActivity.this.isFinishing()) {
//                    return;
//                }
//                finish();
//            } else if (action.equals("com.huaiye.vvs.gotologin")) {
//                if (AppBaseActivity.this instanceof LoginActivity) {
//
//                } else {
//                    if (AppBaseActivity.this.isFinishing()) {
//                        return;
//                    }
//                    finish();
//                }
//            }
//        }
//    }

}
