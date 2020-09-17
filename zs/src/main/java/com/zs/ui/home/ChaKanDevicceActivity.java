package com.zs.ui.home;

import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_chakanshebei)
public class ChaKanDevicceActivity extends AppBaseActivity {

    @BindView(R.id.tv_name)
    TextView tv_name;

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

        tv_name.setText("设备ID:   "+ AppUtils.getIMEIResult(this));

    }

}
