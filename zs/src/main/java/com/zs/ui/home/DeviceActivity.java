package com.zs.ui.home;

import android.view.View;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.BuildConfig;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.models.ModelApis;
import com.zs.models.ModelCallback;
import com.zs.models.auth.bean.VersionData;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_device)
public class DeviceActivity extends AppBaseActivity {

    @BindView(R.id.tv_device_version)
    TextView tv_device_version;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("查看设备")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        //显示当前的版本号
        tv_device_version.setText("设备ID:" + AppUtils.getIMEIResult(MCApp.getInstance()));
    }

}
