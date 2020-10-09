package com.zs.ui.Capture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.KeyCodeEvent;
import com.zs.bus.NetChange;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.rx.RxUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.StopCaptureMessage;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.bean.AnJianBean;
import com.zs.models.auth.bean.AuthUser;
import com.zs.ui.home.view.CaptureViewLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import ttyy.com.jinnetwork.core.work.HTTPResponse;

@BindLayout(R.layout.activity_capture)
public class CaptureGuanMoOrPushActivity extends AppBaseActivity {
    boolean isOffline = false;

    @BindView(R.id.capture_view)
    public CaptureViewLayout captureView;

    StopCaptureMessage stopCaptureMessage;
    ArrayList<SendUserBean> userList = new ArrayList<>();
    private SdpMessageCmStartSessionRsp sessionRsp;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        MCApp.getInstance().guanMoOrPushActivity = this;
    }

    @Override
    public void doInitDelay() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        new RxUtils().doDelay(2000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                initData();
            }
        }, "onNewIntent");
    }

    private void initData() {
        if (null != getIntent() && null != getIntent().getExtras()) {
            stopCaptureMessage = (StopCaptureMessage) getIntent().getSerializableExtra("stopCaptureMessage");
            userList = (ArrayList<SendUserBean>) getIntent().getSerializableExtra("userList");
        }

        startPre();

        changeAnJian();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            changeAnJian();
        }
    }

    private void changeAnJian() {
        if (!TextUtils.isEmpty(AppAuth.get().getAnJian())) {
            AnJianBean bean = new Gson().fromJson(AppAuth.get().getAnJian(), AnJianBean.class);
            captureView.bindAnJian(bean);
        }

    }

    private void startPre() {
        int netStatus = AppUtils.getNetWorkStatus(this);
        if (netStatus == -1) {
            isOffline = true;
            captureView.startPreviewVideo(false);
        } else {
            isOffline = false;
            AppAuth.get().put("strTokenHY", "");
            if (!TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                AuthApi.get().loginHY(AppAuth.get().getUserLoginName(), new ModelCallback<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser) {
                        captureView.startPreviewVideo(false);
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        captureView.startPreviewVideo(false);
                    }
                });
            } else {
                captureView.startPreviewVideo(false);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation location) {
        if (captureView != null) {
            captureView.toggleShuiYin(location);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetChange bean) {
        int netStatus = AppUtils.getNetWorkStatus(this);
        boolean notNet = false;
        if (netStatus == -1) {
            notNet = true;
        } else {
            notNet = false;
        }
        if (isOffline == notNet) {
        } else {
            if (isOffline) { //变动前无网络
                //现在有网络
                AppAuth.get().put("strTokenHY", "");
                if (!TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                    AuthApi.get().loginHY(AppAuth.get().getUserLoginName(), new ModelCallback<AuthUser>() {
                        @Override
                        public void onSuccess(AuthUser authUser) {
                        }
                    });
                }
            } else {//变动前有网络
                //现在无网络
                showToast("网络断开，正在录像切换");
                if(!captureView.onBackPressed(false, false)) {
                    super.onBackPressed();
                }
            }
            isOffline = notNet;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(KeyCodeEvent bean) {
        if (bean.action.equals("zzx_action_record")) {
            if (captureView != null && captureView.iv_start_stop != null) {
                captureView.iv_start_stop.performClick();
            }
        } else if (bean.action.equals("zzx_action_capture")) {
            if (captureView != null && captureView.iv_take_photo != null) {
                captureView.iv_take_photo.performClick();
            }
        } else if (bean.action.equals("zzx_action_mic")) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyUserKickout bean) {
        captureView.onBackPressed(true, true);
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (captureView.onBackPressed(true, false)) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MCApp.getInstance().getTopActivity() != null) {
            MCApp.getInstance().getTopActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        AppUtils.isCaptureLayoutShowing = true;
        MCApp.getInstance().guanMoOrPushActivity = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureView.onPause();
    }

}
