package com.zs.ui.home.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.capture.Capture;
import com.huaiye.sdk.media.capture.HYCapture;
import com.huaiye.sdk.sdkabi._options.symbols.SDKCaptureQuality;
import com.huaiye.sdk.sdpmsgs.video.CStartMobileCaptureRsp;
import com.huaiye.sdk.sdpmsgs.video.CStopMobileCaptureRsp;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AlarmMediaPlayer;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.common.rx.RxUtils;
import com.zs.dao.AppConstants;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.bean.AnJianBean;
import com.zs.ui.local.PhotoAndVideoActivity;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.web.WebJSActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.zs.common.AlarmMediaPlayer.SOURCE_PERSON_VOICE;
import static com.zs.common.AppUtils.STRING_KEY_4G_auto;
import static com.zs.common.AppUtils.STRING_KEY_HD1080P;
import static com.zs.common.AppUtils.STRING_KEY_HD720P;
import static com.zs.common.AppUtils.STRING_KEY_VGA;
import static com.zs.common.AppUtils.STRING_KEY_camera;
import static com.zs.common.AppUtils.STRING_KEY_capture;
import static com.zs.common.AppUtils.STRING_KEY_mPublishPresetoption;
import static com.zs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class CaptureViewLayout extends FrameLayout implements View.OnClickListener {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd mm:ss");
    public View iv_camera;//链接相册键
    ImageView iv_shanguang;
    View iv_change;
    View iv_waizhi;
    View iv_close;
    View iv_suofang;
    View tv_quxiao;
    ImageView iv_anjian;
    public ImageView iv_start_stop;//开关键
    public View iv_take_photo;//拍照键
    TextView tv_local_time;
    TextView tv_name;//名称
    TextView tv_size;//内存
    TextView tv_fenbianlv;//分辨率
    View ll_guanlian;//案件信息
    TextView tv_anjian;//案件信息
    View ll_start_record;//录像时间
    TextView tv_time;//录像时间
    View view_cover;
    View fl_root;
    TextureView ttv_capture;

    private final int CAPTURE_STATUS_NONE = 0;
    private final int CAPTURE_STATUS_STARTING = 1;
    private final int CAPTURE_STATUS_CAPTURING = 2;

    int captureStatus;
    public boolean isStart;
    boolean isPaused;
    boolean isFromGuanMo;//是否是观摩启动
    public MediaFileDaoUtils.MediaFile mMediaMP4File;
    public MediaFileDaoUtils.MediaFile mMediaImgFile;

    Disposable timeDisposable;

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
        tv_local_time = view.findViewById(R.id.tv_local_time);
        tv_name = view.findViewById(R.id.tv_name);
        tv_size = view.findViewById(R.id.tv_size);
        tv_fenbianlv = view.findViewById(R.id.tv_fenbianlv);
        ll_guanlian = view.findViewById(R.id.ll_guanlian);
        tv_anjian = view.findViewById(R.id.tv_anjian);
        tv_time = view.findViewById(R.id.tv_time);
        ll_start_record = view.findViewById(R.id.ll_start_record);
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
        tv_name.setText(AppAuth.get().getUserName());
        tv_size.setText("剩余内存:" + AppUtils.getAvailableInternalMemorySize(context));
//                + "," +
//                AppUtils.getAvailableExternalMemorySize(context));
        showDataTime();
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

    }

    long lstTakePicker;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_anjian:
                //关联信息
                if (bean == null) {
                    Intent intent = new Intent(getContext(), WebJSActivity.class);
                    intent.putExtra("fromCapture", true);
                    ((Activity) getContext()).startActivityForResult(intent, 100);
                } else {
                    if (ll_guanlian.getVisibility() == VISIBLE) {
                        ll_guanlian.setVisibility(GONE);
                    } else {
                        ll_guanlian.setVisibility(VISIBLE);
                    }
                }
                break;
            case R.id.tv_quxiao:
                //取消关联
                bindAnJian(null);
                break;
            case R.id.iv_camera:
                getContext().startActivity(new Intent(getContext(), PhotoAndVideoActivity.class));
                break;
            case R.id.iv_start_stop:
                iv_start_stop.setEnabled(false);
                if (isStart) {
                    onBackPressed(true, true);
                } else {
                    startPreviewVideo(true);
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
                break;
            case R.id.iv_take_photo:
//                if (isStart) {
//                    showToast("正在录制");
//                }
//                if (HYClient.getMemoryChecker().checkEnough()) {
//                    HYClient.getHYCapture().stopCapture(null);
//                    //拍照存放路径
//                    mMediaFile = MediaFileDaoUtils.get().getImgRecordFile();
//
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    Uri uri;
//                    if (Build.VERSION.SDK_INT >= 24) {
//                        uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", new File(mMediaFile.getRecordPath()));
//                    } else {
//                        uri = Uri.fromFile(new File(mMediaFile.getRecordPath()));
//                    }
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    ((Activity) getContext()).startActivityForResult(intent, REQUEST_CODE_CAPTURE);
//                } else {
//                    if (getVisibility() != GONE) {
//                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.local_size_max));
//                    }
//                }
                if (System.currentTimeMillis() - lstTakePicker < 1300) {
                    showToast("正在处理上传，请稍后");
                    return;
                }
                mMediaImgFile = MediaFileDaoUtils.get().getImgRecordFile();
                lstTakePicker = System.currentTimeMillis();
                if (HYClient.getMemoryChecker().checkEnough()) {
                    final String finalStr = mMediaImgFile == null ? "" : mMediaImgFile.getRecordPath();
                    HYClient.getHYCapture().snapShotCapture(mMediaImgFile.getRecordPath(), new SdkCallback<String>() {

                        @Override
                        public void onSuccess(String s) {
                            AlarmMediaPlayer.get().play(true, SOURCE_PERSON_VOICE, null, null);
                            new RxUtils<>().doDelayOn(1000, new RxUtils.IMainDelay() {
                                @Override
                                public void onMainDelay() {
                                    showToast("拍照成功，正在处理");
                                    pushLocalData(finalStr);
                                }
                            });
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {

                        }
                    });
                }
                break;
        }
    }

    public void onResume() {
        isPaused = false;
        if (isCapturing()) {
            HYClient.getHYCapture().setPreviewWindow(ttv_capture);
        }
    }

    public void onPause() {
        isPaused = true;
        if (isCapturing()) {
            HYClient.getHYCapture().setPreviewWindow(null);
        }
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
        isStart = false;
        iv_start_stop.setImageResource(R.drawable.zs_start_bg);
        if (isCapturing() || isCapturStarting()) {
            AppUtils.isCaptureLayoutShowing = false;
            if (mMediaMP4File != null) {
                mMediaMP4File = null;
            }
            changeClickAble(false);
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp cStopMobileCaptureRsp) {
                    changeClickAble(true);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    changeClickAble(true);
                }
            });
            if (HYClient.getSdkSamples().P2P().isBeingWatched() ||
                    HYClient.getSdkSamples().P2P().isTalking()) {
                HYClient.getSdkSamples().P2P().stopAll();
            }
            view_cover.setVisibility(VISIBLE);
            captureStatus = CAPTURE_STATUS_NONE;
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

    private void sendPlayerMessage(CStartMobileCaptureRsp resp) {
        if (resp != null) {
            String playerUrl = "rtsp://"
                    + AppConstants.getSiePlayerddressIP()
                    + ":"
                    + AppConstants.getSiePlayerAddressPort()
                    + "/2337/rtsp://"
                    + AppAuth.get().getTokenHY()
                    + ":"
                    + AppConstants.getSiePlayerAddressPort()
                    + "?BitRate=512;FrameRate=25;IFrame=100;DmgType=2337";
            System.out.println("cccccccccccccccccccccccccccccccc resp " + playerUrl);
            AuthApi.get().changeCapture(getContext(), playerUrl, true, bean == null ? new AnJianBean() : bean);
        }
        AlarmMediaPlayer.get().play(true, SOURCE_PERSON_VOICE, null, null);
    }

    public void startPreviewVideo(final boolean isStart) {
        this.isStart = isStart;
        mMediaMP4File = MediaFileDaoUtils.get().getVideoRecordFile();
        final Capture.Params params;

        int netStatus = AppUtils.getNetWorkStatus(getContext());
        if (netStatus == -1) {
            System.out.println("cccccccccccccccccccccccccccc no wifi " + mMediaMP4File.getRecordPath());
            // 无网络
            HYClient.getSdkOptions().Capture().setCaptureOfflineMode(true);
        } else {
            System.out.println("cccccccccccccccccccccccccccc has wifi :" + AppAuth.get().getTokenHY());
            if (TextUtils.isEmpty(AppAuth.get().getTokenHY())) {
                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(true);
            } else {
                HYClient.getSdkOptions().Capture().setCaptureOfflineMode(false);
            }
        }
        if (isStart) {
            params = Capture.Params.get()
                    .setEnableServerRecord((netStatus == -1) ? false : true)
                    .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                    .setRecordPath(mMediaMP4File.getRecordPath())
                    .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                    .setPreview(ttv_capture);
            iv_start_stop.setImageResource(R.drawable.zs_start_rec_bg);
        } else {
            params = Capture.Params.get()
                    .setEnableServerRecord(false)
                    .setCaptureOrientation(HYCapture.CaptureOrientation.SCREEN_ORIENTATION_PORTRAIT)
                    .setCameraIndex(SP.getInteger(STRING_KEY_camera, -1) == 1 ? HYCapture.Camera.Foreground : HYCapture.Camera.Background)
                    .setPreview(ttv_capture);
        }
        ((Activity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ttv_capture.getLayoutParams().height = calcHeightHeight();

        toggleShuiYin(MCApp.getInstance().locationService.getCurrentBDLocation());
        setVisibility(VISIBLE);
        AppUtils.isCaptureLayoutShowing = true;
        captureStatus = CAPTURE_STATUS_STARTING;

        changeClickAble(false);
        if (HYClient.getHYCapture().isCapturing()) {
            System.out.println("cccccccccccccccc stopCapture          :" + System.currentTimeMillis());
            HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                @Override
                public void onSuccess(CStopMobileCaptureRsp resp) {
                    // 停止采集成功
                    System.out.println("cccccccccccccccc stopCapture onSuccess:" + System.currentTimeMillis());
                    startCapture(params, isStart);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    System.out.println("cccccccccccccccc stopCapture   onError:" + System.currentTimeMillis());
                    // 停止采集失败
                    startCapture(params, isStart);
//                    changeClickAble(true);
                }

            });
        } else {
            startCapture(params, isStart);
        }
    }

    private void startCapture(Capture.Params params, final boolean isStart) {
        HYClient.getHYCapture().startCapture(params,
                new Capture.Callback() {
                    @Override
                    public void onRepeatCapture() {
                        System.out.println("cccccccccccccccccccccccccccc startCapture onRepeatCapture");
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        view_cover.setVisibility(GONE);
                        sendPlayerMessage(null);
                        changeClickAble(true);
                    }

                    @Override
                    public void onSuccess(CStartMobileCaptureRsp resp) {
                        System.out.println("cccccccccccccccccccccccccccc startCapture onSuccess");
                        changeClickAble(true);
                        view_cover.setVisibility(GONE);
                        captureStatus = CAPTURE_STATUS_CAPTURING;
                        if (isStart && getVisibility() != GONE) {
                            startTimer();
                            sendPlayerMessage(resp);
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_success));
                        }
                        // rtsp://36.154.50.211:554/2337/rtsp://18952280597_3:554?BitRate=512;FrameRate=25;IFrame=100;DmgType=2337
                        if (iCaptureStateChangeListener != null) {
                            iCaptureStateChangeListener.onOpen();
                        }
                    }

                    @Override
                    public void onError(ErrorInfo error) {
                        System.out.println("cccccccccccccccccccccccccccc startCapture onError:" + error.toString());
                        changeClickAble(true);
                        if (getVisibility() != GONE) {
                            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.capture_false));
                            onDestroy();
                        }
                    }

                    @Override
                    public void onCaptureStatusChanged(SdpMessageBase msg) {
                    }
                });
    }

    public void toggleShuiYin(BDLocation location) {
//        String strOSDCommand = "drawtext=fontfile="
//                + HYClient.getSdkOptions().Capture().getOSDFontFile()
//                + ":fontcolor=white:x=0:y=0:fontsize=40:box=1:boxcolor=black:alpha=0.8:text=' "
//                + AppAuth.get().getUserName()
//                + "\n"
//                + location.getLatitude()
//                + ","
//                + location.getLongitude()
//                + "'";
        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=40:box=1:boxcolor=black:alpha=0.8:text=' "
                + ""
                + "'";
        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);
    }

    private boolean isCapturing() {
        return captureStatus == CAPTURE_STATUS_CAPTURING;
    }

    private boolean isCapturStarting() {
        return captureStatus == CAPTURE_STATUS_STARTING;
    }

    private int calcHeightHeight() {
        int height = AppUtils.getSize(332);
        switch (SP.getParam(STRING_KEY_capture, "").toString()) {
            case STRING_KEY_VGA:
                height = AppUtils.getSize(332);
                HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                        HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                );
                break;
            case STRING_KEY_HD720P:
                height = AppUtils.getSize(415);
                HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                        HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                );
                break;
            case STRING_KEY_HD1080P:
                height = AppUtils.getSize(415);
                HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                        HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                );
                break;
        }
        return height;
    }

    private void startTimer() {
        stopTime();
        timeDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long o) throws Exception {
                        int hour = (int) (o / (60 * 60));
                        int min = (int) ((o - hour * 3600) / 60);
                        int second = (int) (o - hour * 3600 - min * 60);
                        String strH, strM, strS;
                        strH = hour < 10 ? "0" + hour : "" + hour;
                        strM = min < 10 ? "0" + min : "" + min;
                        strS = second < 10 ? "0" + second : "" + second;
                        tv_time.setText(strH + ":" + strM + ":" + strS);
                    }
                });
    }

    private void closeMedia() {
        AuthApi.get().changeCapture(getContext(), "", false, bean == null ? new AnJianBean() : bean);
        AlarmMediaPlayer.get().play(true, SOURCE_PERSON_VOICE, null, null);
    }

    private void stopTime() {
        tv_time.setText("");
        if (timeDisposable != null && !timeDisposable.isDisposed()) {
            timeDisposable.dispose();
        }
    }

    ICaptureStateChangeListener iCaptureStateChangeListener;
    AnJianBean bean;

    public void bindAnJian(AnJianBean bean) {
        this.bean = bean;
        if (bean != null) {
            iv_anjian.setImageResource(R.drawable.zs_lianjie);
            tv_anjian.setText(bean.companyName);
        } else {
            iv_anjian.setImageResource(R.drawable.zs_add);
            ll_guanlian.setVisibility(GONE);
            tv_anjian.setText("");
            AppAuth.get().put("AnJianBean", "");
        }
    }

    public boolean onBackPressed(boolean isUser, final boolean fromCaptureView) {
        if (ll_guanlian.getVisibility() == VISIBLE) {
            ll_guanlian.setVisibility(GONE);
            if (isUser) {
                return true;
            }
        }
        if (isStart) {
            isStart = false;
            stopTime();
            changeClickAble(false);
            int netStatus = AppUtils.getNetWorkStatus(getContext());
            boolean notNet = false;
            if (netStatus == -1) {
                HYClient.getHYCapture().stopCapture(null);
                closeMedia();
                iv_start_stop.setImageResource(R.drawable.zs_start_bg);
                AppUtils.isCaptureLayoutShowing = false;
                changeClickAble(true);
                if (fromCaptureView) {
                    startPreviewVideo(false);
                }
            } else {
                HYClient.getHYCapture().stopCapture(new SdkCallback<CStopMobileCaptureRsp>() {
                    @Override
                    public void onSuccess(CStopMobileCaptureRsp cStopMobileCaptureRsp) {
                        System.out.println("ccccccc " + mMediaMP4File.getRecordPath() + ":" + new File(mMediaMP4File.getRecordPath()).exists() + ":" + new File(mMediaMP4File.getRecordPath()).length());
                        pushLocalData(mMediaMP4File == null ? "" : mMediaMP4File.getRecordPath());
                        if (mMediaMP4File != null) {
                            mMediaMP4File = null;
                        }
                        closeMedia();
                        iv_start_stop.setImageResource(R.drawable.zs_start_bg);
                        AppUtils.isCaptureLayoutShowing = false;
                        changeClickAble(true);
                        if (fromCaptureView) {
                            startPreviewVideo(false);
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        closeMedia();
                        iv_start_stop.setImageResource(R.drawable.zs_start_bg);
                        AppUtils.isCaptureLayoutShowing = false;
                        changeClickAble(true);
                        if (fromCaptureView) {
                            startPreviewVideo(false);
                        }
                    }
                });
            }
        }
        return false;
    }

    public void pushLocalData(String str) {
        System.out.println("resp pre onResponse pushLocalData:" + str);
        int netStatus = AppUtils.getNetWorkStatus(getContext());
        if (netStatus == 0) {
            File recordFile = new File(str);
            EventBus.getDefault().post(new FileUpload(recordFile.getName(), recordFile));
        } else if (netStatus == 1) {
            if (SP.getBoolean(STRING_KEY_4G_auto, false)) {
                File recordFile = new File(str);
                EventBus.getDefault().post(new FileUpload(recordFile.getName(), recordFile));
            }
        }
    }

    private void showDataTime() {
        new RxUtils<>().doDelayOn(1000, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                tv_local_time.setText(sdf.format(new Date(System.currentTimeMillis())));
                showDataTime();
            }
        });
    }

    private void changeClickAble(boolean enable) {
        iv_close.setEnabled(enable);
        iv_camera.setEnabled(enable);
        iv_shanguang.setEnabled(enable);
        iv_change.setEnabled(enable);
        iv_waizhi.setEnabled(enable);
        iv_suofang.setEnabled(enable);
        iv_start_stop.setEnabled(enable);
        iv_anjian.setEnabled(enable);
        tv_quxiao.setEnabled(enable);
        iv_camera.setEnabled(enable);
        iv_take_photo.setEnabled(enable);
    }

    public interface ICaptureStateChangeListener {
        void onOpen();

        void onClose();

        void hide();

        void show();
    }

}
