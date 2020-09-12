package com.zs.ui.auth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.BuildConfig;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.dialog.LogicDialog;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.dao.auth.AppAuth;
import com.zs.models.contacts.bean.PersonModelBean;
import com.zs.ui.auth.holder.OrgBean;
import com.zs.ui.auth.holder.OrgHolder;
import com.zs.ui.device.holder.PersonHolder;
import com.zs.ui.home.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ttyy.com.coder.scanner.QRCodeScannerView;
import ttyy.com.coder.scanner.decode.DecodeCallback;

import static com.zs.common.AppUtils.ctx;
import static com.zs.common.AppUtils.isTest;
import static com.zs.common.AppUtils.nTestNum;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: LoginActivity
 */
@BindLayout(R.layout.activity_selected_org)
public class SelectOrgActivity extends AppBaseActivity {

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    LiteBaseAdapter<OrgBean> adapter;

    ArrayList<OrgBean> datas = new ArrayList<>();
    OrgBean current;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        datas.add(new OrgBean("省厅","1"));
        datas.add(new OrgBean("苏州","2"));
        datas.add(new OrgBean("无锡","3"));
        datas.add(new OrgBean("南京","4"));
        datas.add(new OrgBean("徐州","5"));
        datas.add(new OrgBean("镇江","6"));
        datas.add(new OrgBean("淮安","7"));
        adapter = new LiteBaseAdapter<>(this,
                datas,
                OrgHolder.class,
                R.layout.item_org_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrgBean bean = (OrgBean) v.getTag();
                        bean.isSelected = !bean.isSelected;
                        for(OrgBean temp : datas) {
                            if(temp != bean) {
                                temp.isSelected = false;
                            }
                        }
                        if(bean.isSelected) {
                            current = bean;
                        } else {
                            current = null;
                        }

                        adapter.notifyDataSetChanged();
                    }
                }, "");

        rv_data.setLayoutManager(new GridLayoutManager(this, 4));
        rv_data.setAdapter(adapter);
    }

    @OnClick({R.id.tv_cancel, R.id.iv_back, R.id.tv_sure})
    void onBtnClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_sure:
                if(current == null) {
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("org", current);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

}
