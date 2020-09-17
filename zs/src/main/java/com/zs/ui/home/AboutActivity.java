package com.zs.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.OnClick;
import com.zs.BuildConfig;
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
@BindLayout(R.layout.activity_about)
public class AboutActivity extends AppBaseActivity {

    @BindView(R.id.tv_current_version)
    TextView tv_current_version;
    @BindView(R.id.tv_new_version)
    TextView tv_new_version;
    @BindView(R.id.tv_size)
    TextView tv_size;
    @BindView(R.id.tv_content)
    TextView tv_content;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("版本更新")
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
        tv_current_version.setText("当前版本:" + AppUtils.getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);

        //请求版本，检查版本是否需要更新
        requestVersion();

    }

    @OnClick(R.id.tv_sure)
    public void requestVersion() {
        ModelApis.Auth().requestVersion(this, new ModelCallback<VersionData>() {
            @Override
            public void onSuccess(VersionData versionData) {
                tv_new_version.setText("最新版本:" + AppUtils.getString(R.string.activity_about_has_new));
                if (versionData.isNeedToUpdate()) {
                    tv_size.setText("大小:" + versionData.getData().getNewVersion());
                    tv_content.setText("更新内容:" + versionData.getData().getAppDesc());
                } else {
                    tv_size.setText("大小:无需更新");
                    tv_content.setText("更新内容:无需更新");
                }
            }
        });
    }

}
