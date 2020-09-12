package com.zs.ui.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;
import com.huaiye.sdk.sdpmsgs.video.CStopMobileCaptureRsp;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.dao.AppDatas;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.ChatUtil;
import com.zs.dao.msgs.StopCaptureMessage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.zs.common.AppUtils.CAPTURE_TYPE;
import static com.zs.common.AppUtils.STRING_KEY_HD1080P;
import static com.zs.common.AppUtils.STRING_KEY_HD720P;
import static com.zs.common.AppUtils.STRING_KEY_VGA;
import static com.zs.common.AppUtils.STRING_KEY_camera;
import static com.zs.common.AppUtils.STRING_KEY_capture;
import static com.zs.common.AppUtils.STRING_KEY_false;
import static com.zs.common.AppUtils.STRING_KEY_mPublishPresetoption;
import static com.zs.common.AppUtils.STRING_KEY_photo;
import static com.zs.common.AppUtils.showToast;
import static com.zs.ui.Capture.CaptureGuanMoOrPushActivity.REQUEST_CODE_CAPTURE;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class CaptureViewLayout extends FrameLayout implements View.OnClickListener {

    public View iv_camera;//链接相册键
    ImageView iv_shanguang;
    View iv_change;
    View iv_waizhi;
    View iv_close;
    View iv_suofang;
    View tv_quxiao;
    ImageView iv_anjian;
    ImageView iv_start_stop;//开关键
    View iv_take_photo;//拍照键
    TextView tv_name;//名称
    TextView tv_size;//内存
    TextView tv_fenbianlv;//分辨率
    View ll_guanlian;//案件信息
    TextView tv_anjian;//案件信息
    TextView tv_time;//案件信息
    View view_cover;
    View fl_root;
    TextureView ttv_capture;
    SurfaceTexture mSurfaceTexture;
    private Camera mCamera;

    ArrayList<String> userId = new ArrayList<>();
    boolean isOfflineMode = true;//是否在线录像
    private final int CAPTURE_STATUS_NONE = 0;
    private final int CAPTURE_STATUS_STARTING = 1;
    private final int CAPTURE_STATUS_CAPTURING = 2;

    int captureStatus;
    boolean isRecord;
    boolean isPaused;
    boolean isFromGuanMo;//是否是观摩启动
    MediaFileDaoUtils.MediaFile mMediaFile;

    /**
     * pc 客户端会多次发消息,采集开始的时候缓存起来
     */
    ArrayList<CaptureMessage> pendingMsg = new ArrayList<>();

    public boolean isCapture() {
        return captureStatus > CAPTURE_STATUS_NONE;
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CaptureViewLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);

        View view = LayoutInflater.from(context).inflate(R.layout.main_capture_layout, null);

        iv_camera = view.findViewById(R.id.iv_camera);
        iv_shanguang = view.findViewById(R.id.iv_shanguang);
        iv_change = view.findViewById(R.id.iv_change);
        iv_waizhi = view.findViewById(R.id.iv_waizhi);
        ttv_capture = view.findViewById(R.id.ttv_capture);
        view_cover = view.findViewById(R.id.view_cover);
        iv_close = view.findViewById(R.id.iv_close);
        fl_root = view.findViewById(R.id.fl_root);
        iv_suofang = view.findViewById(R.id.iv_suofang);
        tv_name = view.findViewById(R.id.tv_name);
        tv_size = view.findViewById(R.id.tv_size);
        tv_fenbianlv = view.findViewById(R.id.tv_fenbianlv);
        ll_guanlian = view.findViewById(R.id.ll_guanlian);
        tv_anjian = view.findViewById(R.id.tv_anjian);
        tv_time = view.findViewById(R.id.tv_time);
        iv_start_stop = view.findViewById(R.id.iv_start_stop);
        iv_take_photo = view.findViewById(R.id.iv_take_photo);
        tv_quxiao = view.findViewById(R.id.tv_quxiao);
        iv_anjian = view.findViewById(R.id.iv_anjian);

        addView(view);

        iv_close.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        iv_shanguang.setOnClickListener(this);
        iv_change.setOnClickListener(this);
        iv_waizhi.setOnClickListener(this);
        iv_suofang.setOnClickListener(this);
        iv_start_stop.setOnClickListener(this);
        iv_anjian.setOnClickListener(this);
        tv_quxiao.setOnClickListener(this);
        iv_camera.setOnClickListener(this);
        iv_take_photo.setOnClickListener(this);
        tv_name.setText(AppAuth.get().getUserLoginName());
        tv_size.setText("剩余内存:" + AppUtils.getAvailableInternalMemorySize(context));
//                + "," +
//                AppUtils.getAvailableExternalMemorySize(context));
        switch (SP.getInteger(STRING_KEY_mPublishPresetoption, 1)) {
            case 1:
                tv_fenbianlv.setText("640x480");
                break;
            case 2:
                tv_fenbianlv.setText("1280x720");
                break;
            case 3:
                tv_fenbianlv.setText("1920x1080");
                break;
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ttv_capture.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurfaceTexture = surface;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });

    }

    //开始预览视屏
    private void startPreviewCamera() {
        mCamera = Camera.open();
        Camera.Parameters paramOld = mCamera.getParameters();

        Camera.Parameters param = mCamera.getParameters();
        switch (SP.getInteger(STRING_KEY_photo, 3)) {
            case 1://640x480
                param.setPictureSize(640, 480);
                break;
            case 2://1280x720
                param.setPictureSize(1280, 720);
                break;
            case 3://1920x1080
                param.setPictureSize(1920, 1080);
                break;
        }
        mCamera.setParameters(param);

        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ttv_capture.setAlpha(1.0f);
        ttv_capture.setRotation(90.0f);
        mCamera.startPreview();
        mCamera.setParameters(paramOld);
    }

    public void toggleOnlineOffline() {
        HYClient.getSdkOptions().Capture().setCaptureOfflineMode(isOfflineMode);
        isOfflineMode = !isOfflineMode;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_anjian:
                //关联信息
                if (ll_guanlian.getVisibility() == VISIBLE) {
                    ll_guanlian.setVisibility(GONE);
                    iv_anjian.setImageResource(R.drawable.zs_add);
                } else {
                    ll_guanlian.setVisibility(VISIBLE);
                    iv_anjian.setImageResource(R.drawable.zs_lianjie);
                }
                break;
            case R.id.tv_quxiao:
                //取消关联
                break;
            case R.id.iv_camera:

                break;
            case R.id.iv_start_stop:
                if (isRecord) {
                    isRecord = false;
                    iv_start_stop.setImageResource(R.drawable.zs_start_bg);
                    AppUtils.isCaptureLayoutShowing = false;
                    if (mMediaFile != null) {
                        mMediaFile = null;
                    }
                    HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                        @Override
                        public void onSuccess(CStopMobileCaptureRsp cStopMobileCaptureRsp) {
                            startPreviewVideo(null, false);
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                        }
                    });
                } else {
                    startPreviewVideo(null, true);
                }
                break;
            case R.id.iv_shanguang:
                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.Foreground) {
                    showToast(AppUtils.getString(R.string.cameraindex_notice));
                    return;
                }
                HYClient.getHYCapture().setTorchOn(!HYClient.getHYCapture().isTorchOn());
                if (HYClient.getHYCapture().isTorchOn()) {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng_press);
                } else {
                    iv_shanguang.setImageResource(R.drawable.btn_shanguangdeng);
                }
                break;
            case R.id.iv_change:
                HYClient.getHYCapture().toggleInnerCamera();
                break;
            case R.id.iv_waizhi:
                HYClient.getHYCapture().requestUsbCamera();

                if (HYClient.getHYCapture().getCurrentCameraIndex() == HYCapture.Camera.USB) {

                } else {
                    showToast(AppUtils.getString(R.string.out_camera));
                }
                break;
            case R.id.iv_close:
                stopCapture();
                break;
            case R.id.iv_suofang:
                toggleBigSmall();
                break;
            case R.id.iv_take_photo:
                if(isRecord) {
                    showToast("正在录制");
                }
                if (HYClient.getMemoryChecker().checkEnough()) {
                    HYClient.getHYCapture().stopCapture(null);
                    //拍照存放路径
                    mMediaFile = MediaFileDaoUtils.get().getImgRecordFile();

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", new File(mMediaFile.getRecordPath()));
                    } else {
                        uri = Uri.fromFile(new File(mMediaFile.getRecordPath()));
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    ((Activity)getContext()).startActivityForResult(intent, REQUEST_CODE_CAPTURE);
                } else {
                    if (getVisibility() != GONE) {
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.local_size_max));
                    }
                }
                break;
        }
    }

    public void onResume() {
        Logger.debug("CaptureiewLayout  onResume() " + isCapturing());
        isPaused = false;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(ttv_capture);
    }

    public void onPause() {
        isPaused = true;
        if (isCapturing())
            HYClient.getHYCapture().setPreviewWindow(null);
    }

    public void onDestroy() {
        if (MCApp.getInstance().getTopActivity() != null) {
            MCApp.getInstance().getTopActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        if (isCapturing() || isCapturStarting()) {
            stopCapture();
        }
    }

    public void stopCapture() {
        isRecord = false;
        iv_start_stop.setImageResource(R.drawable.zs_start_bg);
        if (isCapturing() || isCapturStarting()) {
            AppUtils.isCaptureLayoutShowing = false;
            if (mMediaFile != null) {
                mMediaFile = null;
            }
            HYClient.getHYCapture().stopCapture(null);
            HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
            if (HYClient.getSdkSamples().P2P().isBeingWatched() ||
                    HYClient.getSdkSamples().P2P().isTalking()) {
                HYClient.getSdkSamples().P2P().stopAll();
            }
            view_cover.setVisibility(VISIBLE);
            captureStatus = CAPTURE_STATUS_NONE;
            userId.clear();
            if (getVisibility() != GONE) {
                ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.stop_capture_success));
            }
            isFromGuanMo = false;

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.onClose();
            }
        }
        //不管停止成功不成功,都要gone掉
        setVisibility(GONE);

    }

    /**
     * 开启观摩
     *
     * @param bean
     */
    public void startCaptureFromUser(CaptureMessage bean) {
        if (bean != null) {
            if (!userId.contains(bean.fromUserId)) {
                userId.add(bean.fromUserId);
            }
            if (isCapturStarting()) {
                pendingMsg.add(bean);
            }
        }

        if (isCapturing()) {
            setVisibility(VISIBLE);
            sendPlayerMessage();
        } else {

        }

    }

    /**
     * 关闭观摩
     *
     * @param bean
     */
    public void stopCaptureFromUser(StopCaptureMessage bean) {
        if (userId.contains(bean.fromUserId)) {
            userId.remove(bean.fromUserId);
        }
        if (userId.size() <= 0) {
            if (isFromGuanMo) {
                stopCapture();
            }
        }

    }

    private void sendPlayerMessage() {
        if (pendingMsg.size() > 0) {
            for (int i = 0; i < pendingMsg.size(); i++) {
                CaptureMessage user = pendingMsg.get(i);
                ChatUtil.get().rspGuanMo(user.fromUserId, user.fromUserDomain, user.fromUserName, user.sessionID);
            }
//            pendingMsg.clear();
        }
    }

    public void startPreviewVideo(final CaptureMessage users, boolean localRecord) {
        if (users != null) {
            pendingMsg.add(users);
        }
        if (users != null) {
            iv_start_stop.performClick();
        } else {
            mMediaFile = MediaFileDaoUtils.get().getVideoRecordFile();

            boolean captureType = Boolean.parseBoolean(SP.getParam(CAPTURE_TYPE, STRING_KEY_false).toString());
            if (captureType) {
                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(true);
            } else {
                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
            }
            final Capture.Params params;
            if (localRecord) {
                params = Capture.Params.get()
                        .setEnableServerRecord(false)
                        .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                        .setRecordPath(mMediaFile.getRecordPath())
                        .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                        .setPreview(ttv_capture);
                isRecord = true;
                iv_start_stop.setImageResource(R.drawable.zs_start_rec_bg);
            } else {
                params = Capture.Params.get()
                        .setEnableServerRecord(false)
                        .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                        .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                        .setPreview(ttv_capture);
            }
            toggleShuiYin();
            ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getLayoutParams().height = calcHeightHeight();

            setVisibility(VISIBLE);
            AppUtils.isCaptureLayoutShowing = true;
            captureStatus = CAPTURE_STATUS_STARTING;

            if (HYClient.getHYCapture().isCapturing()) {
                HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                    @Override
                    public void onSuccess(CStopMobileCaptureRsp resp) {
                        // 停止采集成功
                        startCapture(params);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        // 停止采集失败
                    }

                });
            } else {
                startCapture(params);
            }
        }
    }

    private void startCapture(Capture.Params params) {
        HYClient.getHYCapture().startCapture(params,
                new Capture.Callback() {
                    @Override
                    public void onRepeatCapture() {
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        view_cover.setVisibility(GONE);
                        sendPlayerMessage();
                    }

                    @Override
                    public void onSuccess(CStartMobileCaptureRsp resp) {
                        view_cover.setVisibility(GONE);
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        if (!AppUtils.isVideo && !AppUtils.isMeet && getVisibility() != GONE) {
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_success));
                        }
                        sendPlayerMessage();

                        if (iCaptureStateChangeListener != null) {
                            iCaptureStateChangeListener.onOpen();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        if (!AppUtils.isVideo && !AppUtils.isMeet && getVisibility() != GONE) {
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_false));
                            onDestroy();
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                    }
                });
    }


    public void toggleShowHide() {
        if (!isCapturing()) {
            return;
        }

        if (getVisibility() == VISIBLE) {
            setVisibility(INVISIBLE);

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.hide();
            }
        } else {
            setVisibility(VISIBLE);

            if (iCaptureStateChangeListener != null) {
                iCaptureStateChangeListener.show();
            }
        }
    }

    public void toggleShuiYin() {
        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=52:box=1:boxcolor=black:alpha=0.8:text=' "
                + AppDatas.Auth().getUserLoginName()
                + "'";
        // OSD名称初始化
        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);
    }

    private boolean isCapturing() {
        return captureStatus == CAPTURE_STATUS_CAPTURING;
    }

    private boolean isCapturStarting() {
        return captureStatus == CAPTURE_STATUS_STARTING;
    }

    public void toggleBigSmall() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (getLayoutParams().width == LayoutParams.MATCH_PARENT) {
            layoutParams.width = AppUtils.getSize(250);
            layoutParams.height = calcHeightHeight();
            FrameLayout.LayoutParams lp = (LayoutParams) fl_root.getLayoutParams();
            lp.height = LayoutParams.MATCH_PARENT;
            fl_root.setLayoutParams(lp);

        } else {
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.MATCH_PARENT;

            if (SP.getParam(STRING_KEY_capture, "").toString().equals(STRING_KEY_VGA)) {
                //手机一般都是16:9的,放大后要注意4:3的情况
                FrameLayout.LayoutParams lp = (LayoutParams) fl_root.getLayoutParams();
                lp.height = (int) (1.3333 * Float.valueOf(AppUtils.getScreenWidth()));
                fl_root.setLayoutParams(lp);
            }
        }
        setLayoutParams(layoutParams);
    }

    private int calcHeightHeight() {
        int height = AppUtils.getSize(332);
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                height = AppUtils.getSize(332);
                break;
            case STRING_KEY_HD720P:
                height = AppUtils.getSize(415);
                break;
            case STRING_KEY_HD1080P:
                height = AppUtils.getSize(415);
                break;
        }
        return height;
    }

    ICaptureStateChangeListener iCaptureStateChangeListener;

    public void setiCaptureStateChangeListener(ICaptureStateChangeListener iCaptureStateChangeListener) {
        this.iCaptureStateChangeListener = iCaptureStateChangeListener;
    }

    public void toggleCapture() {

    }

    public interface ICaptureStateChangeListener {
        void onOpen();

        void onClose();

        void hide();

        void show();
    }

}
