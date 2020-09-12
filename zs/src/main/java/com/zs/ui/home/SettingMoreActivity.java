package com.zs.ui.home;

import android.content.Intent;
import android.view.View;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.OnClick;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.zs.R;
import com.zs.bus.UploadFile;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.rx.RxUtils;
import com.zs.models.auth.AuthApi;
import com.zs.ui.iperf.IperfActivity;

@BindLayout(R.layout.activity_setting_more)
public class SettingMoreActivity extends AppBaseActivity {


    @Override
    protected void initActionBar() {
        getNavigate().setTitlText(AppUtils.getString(R.string.setting_more))
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {

    }

    @OnClick({R.id.view_upload_log,R.id.view_bandwidth})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.view_upload_log:
                mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
                new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal() {
                    @Override
                    public Object doOnThread() {
                        AuthApi.get().uploadLogOnCrash(true);
                        return "";
                    }

                    @Override
                    public void doOnMain(Object data) {
                    }
                });
                break;
            case R.id.view_bandwidth:
                startActivity(new Intent(getSelf(),IperfActivity.class));
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UploadFile status) {
        if (status.code == 0) {
            mZeusLoadView.setLoadingText(AppUtils.getString(R.string.upload_success));
        } else if (status.code == 1) {
            mZeusLoadView.setLoadingText(AppUtils.getString(R.string.upload_false));
        } else {
            mZeusLoadView.setLoadingText(AppUtils.getString(R.string.upload_file_too_big));
        }
        new RxUtils().doDelay(500, new RxUtils.IMainDelay() {
            @Override
            public void onMainDelay() {
                mZeusLoadView.dismiss();
            }
        }, "dismiss");
    }
}
