package com.zs.models.auth;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiAuth;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.auth.CUploadLogInfoRsp;
import com.huaiye.sdk.sdpmsgs.auth.CUserRegisterRsp;
import com.zs.BuildConfig;
import com.zs.MCApp;
import com.zs.R;
import com.zs.bus.UploadFile;
import com.zs.common.AppUtils;
import com.zs.common.ErrorMsg;
import com.zs.common.SP;
import com.zs.common.dialog.LogicDialog;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.rx.RxUtils;
import com.zs.dao.AppConstants;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ConfigResult;
import com.zs.models.ModelCallback;
import com.zs.models.ModelSDKErrorResp;
import com.zs.models.auth.bean.AnJianBean;
import com.zs.models.auth.bean.AuthUser;
import com.zs.models.auth.bean.CommonBean;
import com.zs.models.auth.bean.DeviceTrailBean;
import com.zs.models.auth.bean.Upload;
import com.zs.models.auth.bean.VersionData;
import com.zs.models.download.DownloadApi;
import com.zs.models.download.DownloadService;
import com.zs.models.download.ErrorDialogActivity;
import com.zs.ui.local.IUploadProgress;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.local.bean.UploadModelBean;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPUIThreadCallbackAdapter;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;
import ttyy.com.jinnetwork.core.work.method_post.PostContentType;

import static android.content.Context.BATTERY_SERVICE;
import static com.zs.common.AppUtils.STRING_KEY_needload;
import static com.zs.common.AppUtils.STRING_KEY_save_photo;
import static com.zs.common.AppUtils.STRING_KEY_save_video;
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
    File tag = new File(Environment.getExternalStorageDirectory() + "/" + BuildConfig.APPLICATION_ID + "_" + AppAuth.get().getUserLoginName() + "_" + System.currentTimeMillis() + ".zip");
    File tagZip = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files");

    private AuthApi() {
    }

    public static AuthApi get() {
        return new AuthApi();
    }


    public void getServiceConfig(final ModelCallback<ConfigResult> callback) {
        String URL = "http://192.168.1.2/vss/httpjson/get_vss_config_para";
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
                                }
                                if (info.getStrVssConfigParaName().equals("FILE_SERVICE_PORT")) {
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
        URL = AppConstants.getAddressBaseURL() + "aj/mediaApk/loginout";
        Https.get(URL)
                .addHeader("Authorization", AppAuth.get().getToken())
                .addParam("phone", AppAuth.get().getUserLoginName())
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
                        .setAddress(AppConstants.getSieAddressIP(), Integer.parseInt(AppConstants.getSieAddressPort()))
                        .setAutoKickout(true)
                        .setUserId(account + "")
                        .setnPriority(999)
                        .setUserName(account), new SdkCallback<CUserRegisterRsp>() {
                    @Override
                    public void onSuccess(CUserRegisterRsp cUserRegisterRsp) {
                        System.out.println("ccccccccccccccccccccc onSuccess:"+cUserRegisterRsp.strUserTokenID);
                        AppAuth.get().put("strTokenHY", cUserRegisterRsp.strUserTokenID);
                        successDeal(callback, null, cUserRegisterRsp);
                    }

                    @Override
                    public void onError(final ErrorInfo errorInfo) {
                        System.out.println("ccccccccccccccccccccc onError:"+errorInfo.toString());
                        errorDeal(errorInfo, callback);
                    }
                });
    }

    public void login(final Context context, final String account, final ModelCallback<AuthUser> callback) {
        URL = AppConstants.getAddressBaseURL() + "aj/mediaApk/login";
        AppAuth.get().put("strTokenHY", "");
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
                        System.out.println("ccccccccccccc " + str);
                        AuthUser authUser = null;
                        try {
                            authUser = new Gson().fromJson(str, AuthUser.class);
                            AppAuth.get().setAuthUser(authUser);
                        } catch (Exception e) {
                            CommonBean bean = new Gson().fromJson(str, CommonBean.class);
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, bean.msg));
                            }
                            return;
                        }
                        if (authUser == null) {
                            if (callback != null) {
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_empty_code, "登录出错"));
                            }
                            return;
                        }
                        if (!"0".equals(authUser.code)) {
                            if (callback != null) {
                                if (BuildConfig.DEBUG) {
                                    loginHY(account, callback);
                                }
                                CommonBean bean = new Gson().fromJson(str, CommonBean.class);
                                callback.onFailure(new ModelSDKErrorResp().setErrorMessage(-1, bean.msg));
                            }
                            return;
                        }
                        loginHY(account, callback);
                    }

                    @Override
                    public void onFailure(HTTPResponse response) {
                        super.onFailure(response);
                        if (callback != null) {
                            callback.onFailure(new ModelSDKErrorResp().setErrorMessage(login_err_code, "登录出错"));
                        }
                    }
                })
                .build()
                .requestNowAsync();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void pushGPS(Context context, BDLocation location) {
        if(TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            return;
        }
        BatteryManager batteryManager = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        DeviceTrailBean mediaDeviceTrailEntity = new DeviceTrailBean();
        mediaDeviceTrailEntity.accuracy = location.getLatitude() + "";
        mediaDeviceTrailEntity.dimension = location.getLongitude() + "";
        mediaDeviceTrailEntity.userId = AppAuth.get().getUserID();
        mediaDeviceTrailEntity.time = AppUtils.getTime(System.currentTimeMillis());
        mediaDeviceTrailEntity.electricityQuantity = battery + "";
        mediaDeviceTrailEntity.deviceId = battery + "";
        Https.post(AppConstants.getAddressBaseURL() + "aj/mediaApk/deviceTrail")
                .addHeader("Authorization", AppAuth.get().getToken())
//                .addParam("mediaDeviceTrailEntity", mediaDeviceTrailEntity)
                .addParam("accuracy", location.getLatitude() + "")
                .addParam("dimension", location.getLongitude() + "")
                .addParam("userId", AppAuth.get().getUserID())
                .addParam("time", AppUtils.getTime(System.currentTimeMillis()))
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .addParam("electricityQuantity", battery + "")
                .setHttpCallback(null)
                .build()
                .requestNowAsync();
    }

    public void startUnUsDevice(Context context) {
        Https.get(AppConstants.getAddressBaseURL() + "aj/mediaApk/deviceOutUse")
                .addHeader("Authorization", AppAuth.get().getToken())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                })
                .build()
                .requestNowAsync();
    }

    public void startUsDevice(Context context, AnJianBean bean) {
        Https.post(AppConstants.getAddressBaseURL() + "aj/mediaApk/deviceInUse")
                .addHeader("Authorization", AppAuth.get().getToken())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .addParam("caseName", bean.caseName)
                .addParam("businessId", bean.businessId)
                .addParam("companyName", bean.companyName)
                .addParam("nodeCode", bean.nodeCode)
                .addParam("nodeName", bean.nodeName)
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }
                })
                .build()
                .requestNowAsync();
    }

    public void changeCapture(Context context, String url, final boolean isCapture, AnJianBean bean) {

        Https.post(AppConstants.getAddressBaseURL() + "aj/mediaApk/captureState")
                .addHeader("Authorization", AppAuth.get().getToken())
                .addParam("userId", AppAuth.get().getUserID())
                .addParam("deviceId", AppUtils.getIMEIResult(context))
                .addParam("url", url)
                .addParam("captureState", isCapture ? 1 : 0)
                .setHttpCallback(new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        System.out.println("ccccccccccccccccccccccccccc captureState:onSuccess："+(isCapture ? 1 : 0));
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        System.out.println("ccccccccccccccccccccccccccc captureState:onFailure："+(isCapture ? 1 : 0));
                    }
                })
                .build()
                .requestNowAsync();

        if (isCapture) {
            startUsDevice(context, bean);
        } else {
            startUnUsDevice(context);
        }

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
                + AppAuth.get().getUserName()
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
        String URL = AppConstants.getAddressBaseURL() + "aj/mediaApk/lastVersionInfo";
        Https.post(URL)
                .addHeader("Authorization", AppAuth.get().getToken())
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
                            logicDialog.setMessageText(versionData.getData().getAppDesc());
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
                                        intent.putExtra("downloadURL", versionData.getData().getAppAccessoryBaseBean().getAccessoryDownUrl());
                                        context.startService(intent);
                                    } else if (netStatus == 1) {
                                        // 4G/3G
                                        Intent intent = new Intent(context, ErrorDialogActivity.class);
                                        intent.putExtra("downloadURL", versionData.getData().getAppAccessoryBaseBean().getAccessoryDownUrl());
                                        context.startActivity(intent);
                                    }
//                                    logicDialog.setConfirmClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            logicDialog.dismiss();
//                                            ModelApis.Download().download(context, AppConstants.getFileAddressURL() + versionData.path);
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

    public void upload(final File tag, final boolean isShow) {
        String URL = AppConstants.getAddressBaseURL() + "aj/mediaApk/file";
//        if (tag.length() > 1028 * 1028 * 50) {
//            EventBus.getDefault().post(new UploadFile(2));
//            return;
//        }

        Https.post(URL, PostContentType.MultipartFormdata)
                .addHeader("Authorization", AppAuth.get().getToken())
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

    /**
     * 上传
     *
     * @param tag
     */
    public void upload(final FileUpload tag,
                       final ModelCallback<String> callback,
                       final IUploadProgress progress) {
        UploadModelBean bean = new UploadModelBean(tag);

        OkHttpClient Client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mediaUploadReqDtoJsonStr", new Gson().toJson(bean))
                .addFormDataPart("file", tag.file.getName(),
                        createCustomRequestBody(MediaType.parse("multipart/form-data"), new File(tag.file.getAbsolutePath()), new ProgressListener() {
                            @Override
                            public void onProgress(final long totalBytes, final long remainingBytes, boolean done) {
                                tag.remainingBytes = remainingBytes;
                                tag.totalBytes = totalBytes;
                                if (remainingBytes == 0) {
                                    tag.isUpload = 3;
                                    if (tag.isImg) {
                                        String saveImg = SP.getString(STRING_KEY_save_photo);
                                        if (!saveImg.contains(tag.name)) {
                                            SP.putString(STRING_KEY_save_photo, saveImg + tag.name);
                                        }
                                    } else {
                                        String saveVideo = SP.getString(STRING_KEY_save_video);
                                        if (!saveVideo.contains(tag.name)) {
                                            SP.putString(STRING_KEY_save_video, saveVideo + tag.name);
                                        }
                                    }
                                } else {
                                    tag.isUpload = 1;
                                }
                                new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                                    @Override
                                    public Object doOnThread() {
                                        return "";
                                    }

                                    @Override
                                    public void doOnMain(Object data) {
                                        if(progress != null) {
                                            progress.onProgress(tag, "553");
                                        }
                                    }
                                });
                            }
                        }))
                .build();

        Request request = new Request.Builder()
                .url(AppConstants.getAddressBaseURL() + "aj/mediaApk/file")
                .addHeader("Authorization", AppAuth.get().getToken())
                .post(requestBody)
                .build();

        Client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                try {
                    tag.remainingBytes = 100;
                    tag.totalBytes = 100;
                    tag.isUpload = 2;
                    callback.onFailure(null);
                } catch (Exception ex) {

                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String strResp = response.body().string();
                    tag.remainingBytes = 100;
                    tag.totalBytes = 100;
                    tag.isUpload = 3;
                } catch (Exception e) {
                    tag.isUpload = 2;
                    callback.onFailure(null);
                }
                new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                    @Override
                    public Object doOnThread() {
                        return "";
                    }

                    @Override
                    public void doOnMain(Object data) {
                        if(progress != null) {
                            System.out.println("ccccccccccccccccccccccc start success " +tag.file.getName());
                            progress.onProgress(tag, "603");
                        }
                    }
                });

            }
        });
    }

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file, final ProgressListener listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), remaining -= readCount, remaining == 0);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public interface ProgressListener {
        void onProgress(long totalBytes, long remainingBytes, boolean done);
    }

}
