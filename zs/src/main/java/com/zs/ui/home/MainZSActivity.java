package com.zs.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.meet.CGetMbeConfigParaRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.ErrorMsg;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.dao.AppDatas;
import com.zs.dao.auth.AppAuth;
import com.zs.dao.msgs.CaptureMessage;
import com.zs.dao.msgs.ChatUtil;
import com.zs.map.baidu.GPSLocation;
import com.zs.map.baidu.LocationService;
import com.zs.models.ConfigResult;
import com.zs.models.ModelApis;
import com.zs.models.ModelCallback;
import com.zs.models.ModelSDKErrorResp;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.bean.AuthUser;
import com.zs.models.map.MapApi;
import com.zs.ui.Capture.CaptureGuanMoOrPushActivity;
import com.zs.ui.auth.LoginActivity;
import com.zs.ui.home.holder.MainZSMenuBean;
import com.zs.ui.home.holder.MainZsHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static android.view.View.GONE;
import static com.zs.common.AppUtils.ctx;
import static com.zs.common.ErrorMsg.login_empty_code;
import static com.zs.common.ErrorMsg.login_err_code;

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

    @Override
    protected void initActionBar() {
        ((MCApp) ctx).startTimer();

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
    }

    @Override
    public void doInitDelay() {

        AuthApi.get().startUsDevice(this, "asdasd");

        ModelApis.Auth().getServiceConfig(new ModelCallback<ConfigResult>() {
            @Override
            public void onSuccess(ConfigResult changePwd) {
                ModelApis.Auth().requestVersion(MainZSActivity.this, null);
            }
        });

        if (!TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            ModelApis.Auth().login(this, AppAuth.get().getUserLoginName(), new ModelCallback<AuthUser>() {

                @Override
                public void onSuccess(AuthUser authUser) {
                    changeMenu("登出");
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                    if (!TextUtils.isEmpty(httpResponse.getErrorMessage())) {
                        showToast(ErrorMsg.getMsg(httpResponse.getStatusCode()));
                    }
                }
            });
        }
//        requestConfig();

        datas.add(new MainZSMenuBean(R.drawable.zs_main_photo, "影像采集", 1));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_video, "视频通话", 2));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_local, "媒体回看", 3));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_setting, "设置", 4));
        datas.add(new MainZSMenuBean(R.drawable.zs_main_anjian, "案件关联", 5));
        if (TextUtils.isEmpty(AppDatas.Auth().getUserLoginName())) {
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
                                AuthApi.get().startUsDevice(MainZSActivity.this, "asdasd");
                                AuthApi.get().startUnUsDevice(MainZSActivity.this);
                                break;
                            case 3:
                                startActivity(new Intent(MainZSActivity.this, PhotoAndVideoActivity.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainZSActivity.this, ZSSettingActivity.class));
                                break;
                            case 5:
                                break;
                            case 6:
                                if (TextUtils.isEmpty(AppDatas.Auth().getUserLoginName())) {
                                    startActivityForResult(new Intent(MainZSActivity.this, LoginActivity.class), 1000);
                                } else {
                                    mLogicDialog.setMessageText("要退出登录吗？");
                                    mLogicDialog.setConfirmClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AuthApi.get().logout(MainZSActivity.this, new ModelCallback<Object>() {
                                                @Override
                                                public void onSuccess(Object o) {
                                                    AppDatas.Auth().put("loginName", "");
                                                    changeMenu("登录");
                                                }
                                            });
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            changeMenu("登出");
        }
    }

    private void changeMenu(String str) {
        for (MainZSMenuBean temp : datas) {
            if (temp.code == 6) {
                temp.name = str;

                if (TextUtils.isEmpty(AppDatas.Auth().getUserLoginName())) {
                    temp.img_id = R.drawable.zs_main_denglu;
                } else {
                    temp.img_id = R.drawable.zs_main_dengchu;
                }

                adapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void requestConfig() {
        HYClient.getModule(ApiMeet.class).getMeetConfigureInfo(SdkParamsCenter.Meet.GetMeetConfig(),
                new SdkCallback<CGetMbeConfigParaRsp>() {
                    @Override
                    public void onSuccess(CGetMbeConfigParaRsp cGetMbeConfigParaRsp) {
                        for (CGetMbeConfigParaRsp.ConfigParamsBean temp : cGetMbeConfigParaRsp.lstMbeConfigParaInfo) {
                            if (temp.strMbeConfigParaName.equals("bIfSupportPTP")) {
                                if (temp.strMbeConfigParaValue.equals("1")) {
                                    HYClient.getSdkOptions().P2P().setSupportP2P(true);
                                } else {
                                    HYClient.getSdkOptions().P2P().setSupportP2P(false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {

                    }
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AuthApi.get().startUnUsDevice(this);
    }

}
