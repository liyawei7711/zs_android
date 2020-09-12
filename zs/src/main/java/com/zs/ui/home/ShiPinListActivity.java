package com.zs.ui.home;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdkabi._options.symbols.SDKCaptureQuality;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.SP;

import static com.zs.common.AppUtils.STRING_KEY_HD;
import static com.zs.common.AppUtils.STRING_KEY_HD1080P;
import static com.zs.common.AppUtils.STRING_KEY_HD720P;
import static com.zs.common.AppUtils.STRING_KEY_VGA;
import static com.zs.common.AppUtils.STRING_KEY_capture;
import static com.zs.common.AppUtils.STRING_KEY_mPublishPresetoption;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_shipin_list)
public class ShiPinListActivity extends AppBaseActivity {

    @BindExtra
    int type = 1;


    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("视频设置")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @OnClick({R.id.tv_fenbianlv,
            R.id.tv_malv})
    void onViewClicked(View view) {
        Intent intent = new Intent(this, ShiPinActivity.class);
        switch (view.getId()) {
            case R.id.tv_fenbianlv:
                intent.putExtra("type", 1);
                break;
            case R.id.tv_malv:
                intent.putExtra("type", 2);
                break;
            case R.id.tv_zhenlv:
                intent.putExtra("type", 3);
                break;
        }
        startActivity(intent);
    }


    @Override
    public void doInitDelay() {

    }

}
