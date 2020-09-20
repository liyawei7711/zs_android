package com.zs.ui.Capture;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.KeyCodeEvent;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.rx.RxUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.StopCaptureMessage;
import com.zs.models.auth.bean.AnJianBean;
import com.zs.ui.home.view.CaptureViewLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

@BindLayout(R.layout.activity_capture)
public class CaptureGuanMoOrPushActivity extends AppBaseActivity {
    public static final int REQUEST_CODE_CAPTURE = 0x02;//点击拍照标识

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

        captureView.startPreviewVideo(false);
        changeAnJian();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAPTURE) {
                captureView.pushLocalData();
                captureView.startPreviewVideo(false);
            } else {
                changeAnJian();
            }
        }
    }

    private void changeAnJian() {
        if (!TextUtils.isEmpty(AppAuth.get().getAnJian())) {
            AnJianBean bean = new Gson().fromJson(AppAuth.get().getAnJian(), AnJianBean.class);
            captureView.bindAnJian(bean);
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

    @Override
    public void onBackPressed() {
        if (captureView.onBackPressed()) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureView.onDestroy();
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
