package com.zs.ui.Capture;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.huaiye.cmf.sdp.SdpMessageCmStartSessionRsp;
import com.huaiye.sdk.sdpmsgs.social.SendUserBean;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.rx.RxUtils;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.StopCaptureMessage;
import com.zs.models.auth.AuthApi;
import com.zs.ui.home.view.CaptureViewLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

@BindLayout(R.layout.activity_capture)
public class CaptureGuanMoOrPushActivity extends AppBaseActivity {
    public static final int REQUEST_CODE_CAPTURE = 0x02;//点击拍照标识

    @BindView(R.id.capture_view)
    CaptureViewLayout captureView;

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE) {
            captureView.startPreviewVideo(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation location) {
        if(captureView != null) {
            captureView.toggleShuiYin(location);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
