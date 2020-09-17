package com.zs.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.BuildConfig;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.ErrorMsg;
import com.zs.common.dialog.LogicDialog;
import com.zs.dao.AppConstants;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ModelApis;
import com.zs.models.ModelCallback;
import com.zs.models.auth.bean.AuthUser;
import com.zs.models.auth.bean.ErWeiMaBean;
import com.zs.ui.auth.holder.OrgBean;
import com.zs.ui.home.MainZSActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ttyy.com.coder.scanner.QRCodeScannerView;
import ttyy.com.coder.scanner.decode.DecodeCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

import static com.zs.common.AppUtils.isTest;
import static com.zs.common.AppUtils.nTestNum;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: LoginActivity
 */
@BindLayout(R.layout.activity_auth)
public class LoginActivity extends AppBaseActivity {

    @BindView(R.id.rg_group)
    RadioGroup rg_group;
    @BindView(R.id.ll_input)
    View ll_input;
    @BindView(R.id.tv_phone)
    EditText tv_phone;
    @BindView(R.id.tv_org)
    TextView tv_org;
    @BindView(R.id.qr_scanner)
    QRCodeScannerView qr_scanner;
    @BindView(R.id.view_load)
    View view_load;

    @BindExtra
    int showKickOutDialog;

    OrgBean bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_phone) {
                    ll_input.setVisibility(View.VISIBLE);
                    qr_scanner.setVisibility(View.GONE);
                } else {
                    ll_input.setVisibility(View.GONE);
                    qr_scanner.setVisibility(View.VISIBLE);
                    qr_scanner.startDecodeDelay(800);
                }
            }
        });
//        tv_phone.setText(AppAuth.get().getUserLoginName());

        if (!TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
            ModelApis.Auth().login(this, AppAuth.get().getUserLoginName(), new ModelCallback<AuthUser>() {

                @Override
                public void onSuccess(AuthUser authUser) {
                    jumpToMain(false, AppAuth.get().getUserLoginName());
                }

                @Override
                public void onFailure(HTTPResponse httpResponse) {
                    super.onFailure(httpResponse);
                }
            });
        }

        qr_scanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecodeSuccess(String s) {
                ErWeiMaBean erWeiMaBean = new Gson().fromJson(s, ErWeiMaBean.class);
                bean = new OrgBean("", "", erWeiMaBean.server, erWeiMaBean.prot);
                tv_phone.setText(erWeiMaBean.phone);
                login();
            }

            @Override
            public void onDecodeFail(String s) {
            }
        });

        checkPermission();
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            bean = (OrgBean) data.getSerializableExtra("org");
            tv_org.setText(bean.name);
        }
    }

    @Override
    public void doInitDelay() {
        if (showKickOutDialog == 1) {
            final LogicDialog logicDialog = new LogicDialog(this);
            logicDialog.setCancelable(false);
            logicDialog.setCanceledOnTouchOutside(false);
            logicDialog.setMessageText(AppUtils.getString(R.string.has_connected_false_out));
            logicDialog.setCancelButtonVisibility(View.GONE);
            logicDialog.setConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }

    @OnClick({R.id.ll_login, R.id.btn_ip_set, R.id.tv_org_btn, R.id.btn_wuzhongxin})
    void onBtnClicked(View view) {
        if (view_load.getVisibility() == View.VISIBLE) {
            showToast(AppUtils.getString(R.string.is_loading));
            return;
        }

        switch (view.getId()) {
            case R.id.ll_login:
                // 登录
                login();
                break;
            case R.id.btn_ip_set:
                // 设置服务器
                break;
            case R.id.tv_org_btn:
                startActivityForResult(new Intent(LoginActivity.this, SelectOrgActivity.class), 1000);
                break;
            case R.id.btn_wuzhongxin:
                // 无中心

                if (BuildConfig.DEBUG) {
                    AppAuth.get().setNoCenterUser("lyw " + System.currentTimeMillis());
                } else {
                    if (TextUtils.isEmpty(tv_phone.getText())) {
                        showToast(AppUtils.getString(R.string.count_empty));
                        return;
                    }
                    AppAuth.get().setNoCenterUser(tv_phone.getText().toString());
                }

                if (isTest) {
                    File file = new File(Environment.getExternalStorageDirectory(), "vss_android_num.txt");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                            AppUtils.writeToTxt(file, nTestNum + "");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    String content = AppUtils.getTxtContent(file);
                    int num = Integer.parseInt(content);
                    if (num <= 0) {
                        showToast(getString(R.string.max_num));
                        return;
                    } else {
                        file.deleteOnExit();
                        try {
                            file.createNewFile();
                            AppUtils.writeToTxt(file, (--num) + "");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                jumpToMain(true, "");
                break;
        }
    }

    private void jumpToMain(boolean isNoCenter, String iphone) {
        AppUtils.isMeet = false;
        AppUtils.isTalk = false;
        AppUtils.isVideo = false;
        AppAuth.get().put("loginName", iphone);
        setResult(RESULT_OK);
        startActivity(new Intent(this, MainZSActivity.class));
    }

    void login() {
        if (TextUtils.isEmpty(tv_phone.getText())) {
            showToast(AppUtils.getString(R.string.count_pwd_empty));
            return;
        }
        if (bean == null) {
            showToast("请选择组织");
            return;
        }
        AppConstants.setAddress(bean.ip, bean.port);
        ModelApis.Auth().login(this, tv_phone.getText().toString(), new ModelCallback<AuthUser>() {

            @Override
            public void onPreStart(HTTPRequest httpRequest) {
                super.onPreStart(httpRequest);
                view_load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(AuthUser authUser) {
                jumpToMain(false, tv_phone.getText().toString());
            }

            @Override
            public void onFailure(HTTPResponse httpResponse) {
                super.onFailure(httpResponse);
                view_load.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(httpResponse.getErrorMessage())) {
                    showToast(ErrorMsg.getMsg(httpResponse.getStatusCode()));
                }
            }
        });
    }

    void checkPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[]{}), 1000);
        }

    }

}
