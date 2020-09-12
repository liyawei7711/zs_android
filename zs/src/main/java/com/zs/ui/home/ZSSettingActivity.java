package com.zs.ui.home;

import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.RadioGroup;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.zs.R;
import com.zs.common.AppBaseActivity;

/**
 * author: admin
 * date: 2018/05/11
 * version: 0
 * mail: secret
 * desc: SettingActivity
 */
@BindLayout(R.layout.activity_zs_settings)
public class ZSSettingActivity extends AppBaseActivity {
    @BindView(R.id.rg_group)
    RadioGroup rg_group;
    @BindView(R.id.ll_net)
    View ll_net;
    @BindView(R.id.ll_shipin)
    View ll_shipin;
    @BindView(R.id.ll_other)
    View ll_other;

    @Override
    protected void initActionBar() {
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    public void doInitDelay() {
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_shipin:
                        ll_net.setVisibility(View.GONE);
                        ll_shipin.setVisibility(View.VISIBLE);
                        ll_other.setVisibility(View.GONE);
                        break;
                    case R.id.rb_other:
                        ll_net.setVisibility(View.GONE);
                        ll_shipin.setVisibility(View.GONE);
                        ll_other.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.tv_net,
            R.id.rb_net,
            R.id.tv_photo,
            R.id.tv_video,
            R.id.tv_yinliang,
            R.id.tv_update,
            R.id.tv_liangdu,
            R.id.tv_device})
    void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_net:
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                break;
            case R.id.tv_net:
                startActivity(new Intent(getSelf(), NetActivity.class));
                break;
            case R.id.tv_photo:
                Intent intent = new Intent(this, ShiPinActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
                break;
            case R.id.tv_video:
                startActivity(new Intent(this, ShiPinListActivity.class));
                break;
            case R.id.tv_yinliang:
                startActivity(new Intent(getSelf(), YinLiangActivity.class));
                break;
            case R.id.tv_update:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.tv_liangdu:
                startActivity(new Intent(this, LiangDuActivity.class));
                break;
            case R.id.tv_device:
                startActivity(new Intent(this, DeviceActivity.class));
                break;
        }
    }

}
