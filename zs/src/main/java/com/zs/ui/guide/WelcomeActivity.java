package com.zs.ui.guide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;

import java.util.ArrayList;

import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.SP;
import com.zs.common.rx.RxUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.models.auth.bean.AuthUser;
import com.zs.ui.auth.LoginActivity;
import com.zs.ui.home.MainZSActivity;

import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static com.zs.common.AppUtils.STRING_KEY_false;
import static com.zs.common.AppUtils.STRING_KEY_isAppFirstStarted;
import static com.zs.common.AppUtils.STRING_KEY_needload;
import static com.zs.common.AppUtils.STRING_KEY_true;

@BindLayout(R.layout.activity_welcome)
public class WelcomeActivity extends AppBaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);

//        HYClient.getSdkOptions().encrypt().setEncrypt(true);
//        OptionsNetworkImpl impl = (OptionsNetworkImpl) HYClient.getSdkOptions().Network();
//        impl.setEncryptAddress("124.70.50.244",9001);

        checkPermission();
        AuthApi.get().uploadLog(false);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {

    }

    boolean checkPermission1() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        return permissions.size() == 0;

    }

    /**
     * 检测权限
     */
    private void checkPermission() {
        boolean needReq = false;
        for (int i = 0; i < PERMISSIONS_STORAGE.length; i++) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(WelcomeActivity.this, PERMISSIONS_STORAGE[i])) {
                needReq = true;
            }
        }
        if (needReq) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            gotoNext();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllAgree = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isAllAgree = false;
            }
        }

        if (!isAllAgree) {
            showToast(AppUtils.getString(R.string.has_power_no));
        }
        gotoNext();
    }

    private void gotoNext() {
        if (!isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            // 是否从Launcher启动
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                //finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
                finish();
                return;
            }
        }

        getNavigate().setVisibility(View.GONE);

        if (Boolean.parseBoolean(SP.getParam(STRING_KEY_isAppFirstStarted, STRING_KEY_true).toString())) {
            // 第一次启动
            SP.setParam(STRING_KEY_isAppFirstStarted, STRING_KEY_false);
            startLogin();
        } else {

            if (!SP.getBoolean(STRING_KEY_needload, false)) {
                startLogin();
                return;
            }
            // 权限不足
            boolean isPermissionGranted = checkPermission1();
            // 自动登录
            String account = AppAuth.get().getUserLoginName();
            String password = AppAuth.get().getPassword();

            if (TextUtils.isEmpty(account)
                    || TextUtils.isEmpty(password)
                    || !isPermissionGranted) {
                startLogin();
            } else {
                AuthApi.get().login(this, account, false, new ModelCallback<AuthUser>() {
                    @Override
                    public void onSuccess(AuthUser authUser) {
                        new RxUtils().doDelayOn(1000, new RxUtils.IMainDelay() {
                            @Override
                            public void onMainDelay() {
                                startActivity(new Intent(getSelf(), MainZSActivity.class));
                                finish();
//                                LoginActivity.encryptInit();
                            }
                        });
                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {
                        super.onFailure(httpResponse);
                        startLogin();
                    }

                });

            }
        }
    }

    private void startLogin() {
        new RxUtils().doDelay(1500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                startActivity(new Intent(getSelf(), LoginActivity.class));
                finish();
            }
        }, "start_login");
    }

}
