package com.zs.ui.auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.ui.auth.holder.OrgBean;
import com.zs.ui.auth.holder.OrgHolder;

import java.util.ArrayList;

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
        datas.add(new OrgBean("省厅", "180.101.234.103", "36.152.32.85", "18086"));
        datas.add(new OrgBean("苏州", "2", "58.210.227.111", "8086"));
        datas.add(new OrgBean("无锡", "3", "2.20.102.118", "8086"));
        datas.add(new OrgBean("南京", "4", "36.152.32.85", "8086"));
        datas.add(new OrgBean("徐州", "5", "36.152.32.85", "8086"));
        datas.add(new OrgBean("镇江", "6", "36.156.30.203", "8086"));
        datas.add(new OrgBean("淮安", "7", "222.184.59.37", "8086"));
        datas.add(new OrgBean("宿迁", "8", "116.198.207.104", "8086"));
        datas.add(new OrgBean("盐城", "9", "120.195.30.72", "8086"));
        datas.add(new OrgBean("扬州", "10", "58.220.130.166", "8086"));
        datas.add(new OrgBean("南通", "11", "58.221.238.134", "8086"));
        datas.add(new OrgBean("连云港", "12", "117.60.146.201", "8086"));
        datas.add(new OrgBean("常州", "13", "222.185.127.218", "8086"));
        datas.add(new OrgBean("泰州", "14", "218.90.248.152", "8086"));
        datas.add(new OrgBean("测试", "15", "36.152.32.85", "8086"));
        datas.add(new OrgBean("演示", "16", "36.152.32.85", "18086"));
        datas.add(new OrgBean("预发布", "17", "36.152.32.85", "8086"));
        datas.add(new OrgBean("本地", "18", "192.168.0.66", "18086"));
        adapter = new LiteBaseAdapter<>(this,
                datas,
                OrgHolder.class,
                R.layout.item_org_holder,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrgBean bean = (OrgBean) v.getTag();
                        bean.isSelected = !bean.isSelected;
                        for (OrgBean temp : datas) {
                            if (temp != bean) {
                                temp.isSelected = false;
                            }
                        }
                        if (bean.isSelected) {
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
                if (current == null) {
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
