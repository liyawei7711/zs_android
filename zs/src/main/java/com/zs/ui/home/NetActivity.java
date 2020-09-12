package com.zs.ui.home;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.huaiye.sdk.HYClient;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.MCApp;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.SP;

import static com.zs.common.AppUtils.STRING_KEY_4G_auto;
import static com.zs.common.AppUtils.STRING_KEY_capture;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_net)
public class NetActivity extends AppBaseActivity {

    @BindView(R.id.cb_auto_upload)
    CheckBox cb_auto_upload;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("上传设置")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        cb_auto_upload.setChecked(SP.getBoolean(STRING_KEY_4G_auto, false));
        cb_auto_upload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SP.putBoolean(STRING_KEY_4G_auto, isChecked);
            }
        });
    }

}
