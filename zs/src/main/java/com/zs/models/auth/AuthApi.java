package com.zs.models.auth;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.logger.Logger;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._api.ApiSocial;
import com.huaiye.sdk.sdkabi._options.OptionsUser;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CUploadLogInfoRsp;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListReq;
import com.huaiye.sdk.sdpmsgs.social.CQueryUserListRsp;
import com.zs.BuildConfig;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.UploadFile;
import com.zs.common.AppUtils;
import com.zs.common.ErrorMsg;
import com.zs.common.SP;
import com.zs.common.dialog.LogicDialog;
import com.zs.dao.AppDatas;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ConfigResult;
import com.zs.models.ModelCallback;
import com.zs.models.ModelSDKErrorResp;
import com.zs.models.auth.bean.AuthUser;
import com.zs.models.auth.bean.ChangePwd;
import com.zs.models.auth.bean.Upload;
import com.zs.models.auth.bean.VersionData;
import com.zs.models.contacts.bean.PersonBean;
import com.zs.models.contacts.bean.PersonModelBean;
import com.zs.models.download.DownloadApi;
import com.zs.models.download.DownloadService;
import com.zs.models.download.ErrorDialogActivity;
import com.zs.models.map.MapApi;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.PostContentType;

import static android.content.Context.BATTERY_SERVICE;
import static com.zs.common.AppUtils.STRING_KEY_needload;
import static com.zs.common.ErrorMsg.login_empty_code;
import static com.zs.common.ErrorMsg.login_err_code;

/**
 * author: admin
 * date: 2018/01/02
 * version: 0
 * mail: secret
 * desc: AuthModel
 */

public class AuthApi {

    String URL;
    File tag = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID + "_" + AppDatas.Auth().getUserName() + "_" + System.currentTimeMillis() + ".zip");
    File tagZip = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files");

    private AuthApi() {
    }

    public static AuthApi get() {
        return new AuthApi();
    }


    public void changpwd(String old, String news, final ModelCallback<ChangePwd> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/mod_user_pwd";
        Https.post(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .addHeader("Connection", "close")
                .addParam("strDomainCode", AppDatas.Auth().getDomainCode())
                .addParam("strUserID", AppDatas.Auth().getUserID() + "")
                .addParam("strOldPassword", AppUtils.getMd5(old))
                .addParam("strNewPassword", AppUtils.getMd5(news))
                .setHttpCallback(new ModelCallback<ChangePwd>() {

                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                    }

                    @Override
                    public void onSuccess(ChangePwd response) {
                        if (callback == null)
                            return;

                        callback.onSuccess(response);

                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "HTTP ERROR AuthApi "));
                        }
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void getServiceConfig(final ModelCallback<ConfigResult> callback) {
        String URL = AppDatas.Constants().getAddressBaseURL() + "vss/httpjson/get_vss_config_para";
        ArrayList<String> params = new ArrayList<>();
        params.add("FILE_SERVICE_IP");
        params.add("FILE_SERVICE_PORT");
        Https.post(URL)
                .addParam("lstVssConfigParaName", params)
                .setHttpCallback(new ModelCallback<ConfigResult>() {

                    @Override
                    public void onSuccess(ConfigResult response) {
                        if (response.nResultCode == 0) {
                            for (int i = 0; i < response.getLstVssConfigParaInfo().size(); i++) {
                                ConfigResult.Info info = response.getLstVssConfigParaInfo().get(i);
                                if (info.getStrVssConfigParaName().equals("FILE_SERVICE_IP")) {
                                    AppDatas.Constants().setFileAddress(info.getStrVssConfigParaValue());
                                }
                                if (info.getStrVssConfigParaName().equals("FILE_SERVICE_PORT")) {
                                    AppDatas.Constants().setFilePort(Integer.parseInt(info.getStrVssConfigParaValue()));
                                }
                            }
                            callback.onSuccess(response);
                        } else {
                            callback.onFinish(null);
                        }
                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (callback != null) {
                            callback.onFinish(httpResponse);
                        }
                    }
                })
                .build()
                .requestNowAsync();

    }

    public void logout(Context context, ModelCallback<Object> callback) {
        URL = "http://36.152.32.85:8086/aj/mediaApk/loginout";
        Https.post(URL)
                .addHeader("Authorization", AppDatas.Auth().getToken())
                .addParam("phone", AppDatas.Auth().getUserLoginName())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .setHttpCallback(callback)
                .build()
                .requestNowAsync();
        if (HYClient.getSdkOptions().User().isRegistered()) {
            HYClient.getModule(ApiAuth.class).logout(null);
        }

    }

    public void loginHY(final String account, final ModelCallback<AuthUser> callback) {
        HYClient.getModule(ApiAuth.class)
                .login(SdkParamsCenter.Auth.Login()
                        .setAddress(AppDatas.Constants().getAddressIP(), 9001)
                        .setUserId(account + "")
                        .setnPriority(999)
                        .setUserName(account), new SdkCallback<CUserRegisterRsp>() {
                    @Override
                    public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                        successDeal(callback, null, cUserRegisterRsp);
                    }

                    @Override
                    public void onError(final ErrorInfo errorInfo) {
                        errorDeal(errorInfo, callback);
                    }
                });
    }

    public void login(final Context context, final String account, final ModelCallback<AuthUser> callback) {
        URL = "http://36.152.32.85:8086/aj/mediaApk/login";
        Https.get(URL)
                .addParam("phone", account)
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .setHttpCallback(new HTTPUIThreadCallbackAdapter() {
                    @Override
                    public void onPreStart(HTTPRequest request) {
                        super.onPreStart(request);
                        Log.i("MCApp", "Auth Login PreStart");
                        if (callback != null) {
                            callback.onPreStart(request);
                        }
                    }

                    @Override
                    public void onSuccess(HTTPResponse response) {
                        super.onSuccess(response);
                        String str = response.getContentToString();
                        System.out.println("ccccccccccccc "+str);
                        AuthUser authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, AuthUser.class);
                        } catch (Exception e) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (authUser == null) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "HTTP ERROR AuthApi"));
                            }
                            return;
                        }
                        if (!"0".equals(authUser.code)) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(-1, authUser.msg));
                            }
                            return;
                        }
                        AppDatas.Auth().setAuthUser(authUser);
                        MapApi.isNewBegin = true;
                        loginHY(account, callback);
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "HTTP ERROR AuthApi "));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void pushGPS(Context context, BDLocation location) {
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Https.post("http://36.152.32.85:8086/aj/mediaApk/deviceTrail")
                .addHeader("Authorization", AppDatas.Auth().getToken())
                .addParam("accuracy", location.getLatitude())
                .addParam("dimension", location.getLongitude())
                .addParam("userId", AppDatas.Auth().getUserID())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .addParam("time", AppUtils.getTime(System.currentTimeMillis()))
                .addParam("electricityQuantity", battery + "")
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                })
                .build()
                .requestNowAsync();
    }

    public void startUnUsDevice(Context context) {
        Https.get("http://36.152.32.85:8086/aj/mediaApk/deviceOutUse")
                .addHeader("Authorization", AppDatas.Auth().getToken())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                })
                .build()
                .requestNowAsync();
    }

    public void startUsDevice(Context context, String caseName) {
        Https.get("http://36.152.32.85:8086/aj/mediaApk/deviceInUse")
                .addHeader("Authorization", AppDatas.Auth().getToken())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .addParam("caseName", caseName)
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                })
                .build()
                .requestNowAsync();
    }

    /**
     * 出错处理
     *
     * @param errorInfo
     * @param callback
     */
    private void errorDeal(SdkCallback.ErrorInfo errorInfo, ModelCallback<AuthUser> callback) {
        if (callback != null) {
            if (errorInfo.getCode() == ErrorMsg.re_load_code) {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, ""));
            } else {
                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, errorInfo.getMessage()));
            }
        }
    }

    /**
     * 成功处理
     *
     * @param callback
     * @param finalAuthUser
     * @param registerRsp
     */
    private void successDeal(ModelCallback<AuthUser> callback, AuthUser finalAuthUser, CUserRegisterRsp registerRsp) {
        SP.putBoolean(STRING_KEY_needload, true);
        if (callback != null) {
            callback.onSuccess(finalAuthUser);
        }

        String strOSDCommand = "drawtext=fontfile="
                + HYClient.getSdkOptions().Capture().getOSDFontFile()
                + ":fontcolor=white:x=0:y=0:fontsize=26:box=1:boxcolor=black:alpha=0.8:text=' "
                + AppDatas.Auth().getUserName()
                + "'";
        // OSD名称初始化
        HYClient.getSdkOptions().Capture().setOSDCustomCommand(strOSDCommand);
    }

    /**
     * 获取版本信息
     *
     * @param context
     * @param callback
     */
    public void requestVersion(final Context context, final ModelCallback<VersionData> callback) {
        String URL = AppDatas.Constants().getFileAddressURL() + "app/android.version";
        Https.get(URL)
                .addHeader("X-Token", AppDatas.Auth().getToken())
                .setHttpCallback(new ModelCallback<VersionData>() {

                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {
                        super.onPreStart(httpRequest);
                    }

                    @Override
                    public void onSuccess(final VersionData versionData) {
                        if (callback != null) {
                            callback.onSuccess(versionData);
                        }
                        if (versionData.isNeedToUpdate()) {
                            final LogicDialog logicDialog = new LogicDialog(context);
                            logicDialog.setCancelable(false);
                            logicDialog.setCanceledOnTouchOutside(false);
                            logicDialog.setMessageText(versionData.message);
                            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                    if (DownloadApi.isLoad) {
                                        return;
                                    }
                                    int netStatus = AppUtils.getNetWorkStatus(context);
                                    if (netStatus == -1) {
                                        // 无网络
                                    } else if (netStatus == 0) {
                                        // wifi
                                        AppUtils.showToast(AppUtils.getString(R.string.download_apk_start));
                                        Intent intent = new Intent(context, DownloadService.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileAddressURL() + versionData.path);
                                        context.startService(intent);
                                    } else if (netStatus == 1) {
                                        // 4G/3G
                                        Intent intent = new Intent(context, ErrorDialogActivity.class);
                                        intent.putExtra("downloadURL", AppDatas.Constants().getFileAddressURL() + versionData.path);
                                        context.startActivity(intent);
                                    }
//                                    logicDialog.setConfirmClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            logicDialog.dismiss();
//                                            ModelApis.Download().download(context, AppDatas.Constants().getFileAddressURL() + versionData.path);
//                                        }
//                                    }).setCancelClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            logicDialog.dismiss();
//                                        }
//                                    });
//                                    logicDialog.show();
                                }
                            }).setCancelClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logicDialog.dismiss();
                                }
                            }).show();
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }

    /**
     * 崩溃时处理
     */
    public void uploadLogOnCrash(boolean isShow) {
        AppUtils.zip(tagZip, tag);
        upload(tag, isShow);
    }

    /**
     * 每次启动检测上传
     */
    public void uploadLog(boolean isShow) {
        if (tag.exists()) {
            upload(tag, isShow);
        }
    }

    /**
     * 上传
     *
     * @param tag
     */
    public void upload(final File tag, final boolean isShow) {
        String URL = "http://36.152.32.85:8086/aj/mediaApk/file";
//        if (tag.length() > 1028 * 1028 * 50) {
//            EventBus.getDefault().post(new UploadFile(2));
//            return;
//        }

        Https.post(URL, PostContentType.MultipartFormdata)
                .addHeader("Authorization", AppDatas.Auth().getToken())
                .addParam("userId", AppAuth.get().getUserID())
                .addParam("deviceId", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("accuracy", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("dimension", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("shootTime", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("duration", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("size", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("resolutionRatio", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("type", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("businessId", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("fileName", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("fileMakeDate", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("nodeCode", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("nodeName", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("caseName", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("companyName", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("caseRelationFlag", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("keyEvidenceFlag", AppUtils.getIMEIResult(MCApp.getInstance()))
                .addParam("file1", tag)
                .setHttpCallback(new ModelCallback<Upload>() {
                    @Override
                    public void onSuccess(final Upload versionData) {
                        tag.deleteOnExit();
                        HYClient.getModule(ApiAuth.class)
                                .uploadLogInfo(SdkParamsCenter.Auth.UploadLogInfo()
                                        .setStrLogPath(versionData.file1_name)
                                        .setStrLogTime(new Date().toString()), new SdkCallback<CUploadLogInfoRsp>() {
                                    @Override
                                    public void onSuccess(CUploadLogInfoRsp cUploadLogInfoRsp) {
                                        if (isShow) {
                                            EventBus.getDefault().post(new UploadFile(0));
                                        }
                                    }

                                    @Override
                                    public void onError(ErrorInfo errorInfo) {
                                        if (isShow) {
                                            EventBus.getDefault().post(new UploadFile(1));
                                        }
                                    }
                                });

                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {
                        super.onFinish(httpResponse);
                        if (isShow) {
                            EventBus.getDefault().post(new UploadFile(1));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }


}
