package com.zs.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.location.BDLocation;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyUserKickout;
import com.huaiye.sdk.sdpmsgs.video.CStopMobileCaptureRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.KeyCodeEvent;
import com.zs.bus.NetChange;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.rx.RxUtils;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.ChatUtil;
import com.zs.map.baidu.GPSLocation;
import com.zs.map.baidu.LocationService;
import com.zs.models.ConfigResult;
import com.zs.models.ModelApis;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.bean.AuthUser;
import com.zs.models.auth.bean.VersionData;
import com.zs.ui.Capture.CaptureGuanMoOrPushActivity;
import com.zs.ui.auth.LoginActivity;
import com.zs.ui.home.holder.MainZSMenuBean;
import com.zs.ui.home.holder.MainZsHolder;
import com.zs.ui.local.IUploadProgress;
import com.zs.ui.local.PhotoAndVideoActivity;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.web.WebJSActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static android.view.View.GONE;
import static com.zs.common.AppUtils.STRING_KEY_4G_auto;
import static com.zs.common.AppUtils.ctx;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: MainActivity
 */
@BindLayout(R.layout.activity_zs_main)
public class MainZSActivity extends AppBaseActivity {

    int REQUEST_EXTERNAL_STORAGE = 1;
    String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.READ_PHONE_STATE
    };

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    @BindExtra
    public boolean isNoCenter;

    LocationService locationService;

    ArrayList<MainZSMenuBean> datas = new ArrayList<>();
    LiteBaseAdapter<MainZSMenuBean> adapter;

    // 最大的屏幕亮度
    private float maxLight;
    // 当前的亮度
    private float currentLight;
    // 用来控制屏幕亮度
    private Handler lightHandler;
    // 60秒时间不点击屏幕，屏幕变暗
    private long delayTime = 6 * 1000L;
    boolean isShow;
    RxUtils rxUtils;

    @Override
    protected void onResume() {
        super.onResume();
        isShow = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShow = false;
    }

    @Override
    protected void initActionBar() {
        ((MCApp) ctx).startTimer();
        EventBus.getDefault().register(this);
        // 屏幕亮度控制
        lightHandler = new Handler(Looper.getMainLooper());
        maxLight = getLightness();
//        startSleepTask();

        checkPermission();

        getNavigate().setVisibility(GONE);

        locationService = ((MCApp) getApplication()).locationService;
        locationService.start();

        GPSLocation.get().setGpsStatusInterface(new GPSLocation.GpsStatusInterface() {
            @Override
            public void gpsSwitchState(boolean gpsOpen) {
                if (gpsOpen) {
                } else {
                }
            }
        });
        GPSLocation.get().startGpsObserver();

        ModelApis.Auth().requestVersion(this, new ModelCallback<VersionData>() {
            @Override
            public void onSuccess(VersionData versionData) {
            }
        });

    }

    @Override
    public void doInitDelay() {
        rxUtils = new RxUtils();
        ModelApis.Auth().requestVersion(MainZSActivity.this, null);
        if (!TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            ModelApis.Auth().login(this, AppAuth.get().getUserLoginName(), false, new ModelCallback<AuthUser>() {

                @Override
                public void onSuccess(AuthUser authUser) {
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                }
            });
        }

        datas.add(new MainZSMenuBean(R.drawable.zs_main_photo, "影像采集", 1));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_video, "视频通话", 2));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_local, "媒体回看", 3));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_setting, "设置", 4));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_anjian, "案件关联", 5));
        if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            datas.add(new MainZSMenuBean(R.drawable.zs_main_denglu, "登录", 6));
        } else {
            datas.add(new MainZSMenuBean(R.drawable.zs_main_dengchu, "登出", 6));
        }
        adapter = new LiteBaseAdapter<>(this,
                datas,
                MainZsHolder.class,
                R.layout.item_main_zs_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainZSMenuBean bean = (MainZSMenuBean) v.getTag();
                        switch (bean.code) {
                            case 1:
                                startActivity(new Intent(MainZSActivity.this, CaptureGuanMoOrPushActivity.class));
                                break;
                            case 2:
                                showToast("正在开发中");
//                                MediaFileDaoUtils.get().clear();
//                                ModelApis.Auth().requestVersion(MainZSActivity.this, new ModelCallback<VersionData>() {
//                                    @Override
//                                    public void onSuccess(VersionData versionData) {
//                                        System.out.println("最新版本:" + AppUtils.getString(R.string.activity_about_has_new));
//                                    }
//                                });
                                break;
                            case 3:
                                startActivity(new Intent(MainZSActivity.this, PhotoAndVideoActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainZSActivity.this, ZSSettingActivity.class));
                                break;
                            case 5:
                                int netStatus = AppUtils.getNetWorkStatus(MainZSActivity.this);
                                if (netStatus == -1) {
                                    showToast("当前无网络");
                                    return;
                                }
                                if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                                    showToast("当前未登录");
                                    return;
                                }
                                startActivity(new Intent(MainZSActivity.this, WebJSActivity.class));
                                break;
                            case 6:
                                if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                                    startActivityForResult(new Intent(MainZSActivity.this, LoginActivity.class), 1000);
                                } else {
                                    mLogicDialog.setMessageText("要退出登录吗？");
                                    mLogicDialog.setConfirmClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AuthApi.get().logout(MainZSActivity.this, new ModelCallback<Object>() {
                                                @Override
                                                public void onSuccess(Object o) {
//                                                    finish();
                                                }
                                            });
                                            AppAuth.get().clear();
                                            HYClient.getModule(ApiAuth.class).logout(null);
                                            changeMenu();
                                        }
                                    }).setCancelClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                        }
                                    }).show();
                                }
                                break;
                        }
                    }
                }, "");

        rv_data.setLayoutManager(new GridLayoutManager(this, 2));
        rv_data.setAdapter(adapter);

        AppUtils.createkuaijieicon();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onEvent(new NetChange());
            changeMenu();
        }
    }

    private void changeMenu() {
        for (MainZSMenuBean temp : datas) {
            if (temp.code == 6) {
                if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                    temp.img_id = R.drawable.zs_main_denglu;
                    temp.name = "登录";
                } else {
                    temp.img_id = R.drawable.zs_main_dengchu;
                    temp.name = "登出";

                    if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                        startActivityForResult(new Intent(MainZSActivity.this, LoginActivity.class), 1000);
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    long lastMillions = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentMillions = System.currentTimeMillis();
            long delta = currentMillions - lastMillions;
            lastMillions = currentMillions;
            if (delta < 2000) {
                // 登出操作
//                AuthApi.get().logout();
                Logger.log("双击退出");
//                ((MCApp) ctx).gotoClose();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return true;
            }

            showToast(AppUtils.getString(R.string.double_click_exit));
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void checkPermission() {
        boolean needReq = false;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(MainZSActivity.this, PERMISSIONS_STORAGE[i])) {
                needReq = true;
            }
        }
        if (needReq) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BDLocation location) {
        AuthApi.get().pushGPS(this, location);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CaptureMessage bean) {
        if (HYClient.getHYCapture().isCapturing()) {
            if (!MCApp.userId.contains(bean.fromUserId)) {
                MCApp.userId.add(bean.fromUserId);
            }
            ChatUtil.get().rspGuanMo(bean.fromUserId, bean.fromUserDomain, bean.fromUserName, bean.sessionID);
        } else {
            Intent intent = new Intent(this, CaptureGuanMoOrPushActivity.class);
            intent.putExtra("captureMessage", bean);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(KeyCodeEvent bean) {
        if (!isShow) {
            return;
        }
        if (bean.action.equals("zzx_action_record")) {
            startActivity(new Intent(MainZSActivity.this, CaptureGuanMoOrPushActivity.class));
        } else if (bean.action.equals("zzx_action_capture")) {
            startActivity(new Intent(MainZSActivity.this, CaptureGuanMoOrPushActivity.class));
        } else if (bean.action.equals("zzx_action_mic")) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetChange bean) {
        if (!isShow) {
            return;
        }
//        if(BuildConfig.DEBUG) {
//            return;
//        }
        int netStatus = AppUtils.getNetWorkStatus(this);
        if (netStatus == 0) {
            ArrayList<FileUpload> videos = MediaFileDaoUtils.get().getAllVideosAuto();
            ArrayList<FileUpload> images = MediaFileDaoUtils.get().getAllImgsAuto();
            for (FileUpload temp : videos) {
                onEvent(temp);
            }
            for (FileUpload temp : images) {
                onEvent(temp);
            }
        } else if (netStatus == 1) {
            if (SP.getBoolean(STRING_KEY_4G_auto, false)) {
                ArrayList<FileUpload> videos = MediaFileDaoUtils.get().getAllVideosAuto();
                ArrayList<FileUpload> images = MediaFileDaoUtils.get().getAllImgsAuto();
                for (FileUpload temp : videos) {
                    onEvent(temp);
                }
                for (FileUpload temp : images) {
                    onEvent(temp);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FileUpload bean) {
//        if(BuildConfig.DEBUG) {
//            return;
//        }
        System.out.println("ccccccccccccccccccccccc start");
        if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            return;
        }
        AuthApi.get().upload(bean, new ModelCallback<String>() {
            @Override
            public void onSuccess(String upload) {

            }
        }, new IUploadProgress() {
            @Override
            public void onProgress(final FileUpload bean, String from) {
                if (bean.isUpload == 3) {
                    rxUtils.doDelayOn(300, new RxUtils.IMainDelay() {
                        @Override
                        public void onMainDelay() {
                            System.out.println("resp pre onResponse success delete " + bean.file);
                            bean.file.delete();
                        }
                    });
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CNotifyUserKickout bean) {
        changeMenu();
    }

    /**
     * 获取亮度
     */
    private float getLightness() {
        WindowManager.LayoutParams localLayoutParams = this.getWindow().getAttributes();
        return localLayoutParams.screenBrightness;
    }

    /**
     * 设置亮度
     */
    private void setLightness(int light) {
        currentLight = light;
        WindowManager.LayoutParams localLayoutParams = this.getWindow().getAttributes();
        localLayoutParams.screenBrightness = (light / 255.0F);
        this.getWindow().setAttributes(localLayoutParams);
    }

    /**
     * 开启休眠任务
     */
    private void startSleepTask() {
        setLightness((int) maxLight);
        stopSleepTask();
        lightHandler.postDelayed(sleepWindowTask, delayTime);
    }

    /**
     * 结束休眠任务
     */
    private void stopSleepTask() {
        lightHandler.removeCallbacks(sleepWindowTask);
    }

    /**
     * 休眠任务
     */
    Runnable sleepWindowTask = new Runnable() {
        @Override
        public void run() {
            setLightness(1);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
        EventBus.getDefault().unregister(this);
    }
}
