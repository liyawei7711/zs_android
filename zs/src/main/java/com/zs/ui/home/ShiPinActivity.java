package com.zs.ui.home;

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
import static com.zs.common.AppUtils.STRING_KEY_photo;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_shipin)
public class ShiPinActivity extends AppBaseActivity {

    @BindView(R.id.ll_fenbianlv)
    View ll_fenbianlv;
    @BindView(R.id.tv_fenbianlv_di)
    TextView tv_fenbianlv_di;
    @BindView(R.id.tv_fenbianlv_middle)
    TextView tv_fenbianlv_middle;
    @BindView(R.id.tv_fenbianlv_high)
    TextView tv_fenbianlv_high;

    @BindView(R.id.ll_malv)
    View ll_malv;
    @BindView(R.id.tv_malv_di)
    TextView tv_malv_di;
    @BindView(R.id.tv_malv_middle)
    TextView tv_malv_middle;
    @BindView(R.id.tv_malv_high)
    TextView tv_malv_high;

    @BindView(R.id.ll_zhenlv)
    View ll_zhenlv;
    @BindView(R.id.tv_zhenlv_di)
    TextView tv_zhenlv_di;
    @BindView(R.id.tv_zhenlv_middle)
    TextView tv_zhenlv_middle;
    @BindView(R.id.tv_zhenlv_high)
    TextView tv_zhenlv_high;

    @BindExtra
    int type = 1;

    Drawable drawable;

    @Override
    protected void initActionBar() {
        drawable = getResources().getDrawable(R.drawable.zs_duihao);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        String str = "视频分辨率";
        switch (type) {
            case 0:
            case 1:
                if (type == 0) {
                    str = "照片分辨率";
                } else {
                    str = "视频分辨率";
                }
                ll_fenbianlv.setVisibility(View.VISIBLE);
                ll_malv.setVisibility(View.GONE);
                ll_zhenlv.setVisibility(View.GONE);

                if (type == 0) {
                    switch (SP.getInteger(STRING_KEY_photo, 3)) {
                        case 1://640x480
                            tv_fenbianlv_di.setCompoundDrawables(null, null, drawable, null);
                            tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                            tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                            break;
                        case 2://1280x720
                            tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                            tv_fenbianlv_middle.setCompoundDrawables(null, null, drawable, null);
                            tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                            break;
                        case 3://1920x1080
                            tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                            tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                            tv_fenbianlv_high.setCompoundDrawables(null, null, drawable, null);
                            break;
                    }
                }
                switch (SP.getString(STRING_KEY_capture)) {
                    case STRING_KEY_VGA:
                    case STRING_KEY_HD:
                        tv_fenbianlv_di.setCompoundDrawables(null, null, drawable, null);
                        tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                        tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                        );
                        break;
                    case STRING_KEY_HD720P:
                        tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                        tv_fenbianlv_middle.setCompoundDrawables(null, null, drawable, null);
                        tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HDVGA)
                        );
                        break;
                    case STRING_KEY_HD1080P:
                        tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                        tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                        tv_fenbianlv_high.setCompoundDrawables(null, null, drawable, null);
                        HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                                HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                        );
                        break;
                }

                break;
            case 2:
                str = "视频码率";
                ll_fenbianlv.setVisibility(View.GONE);
                ll_malv.setVisibility(View.VISIBLE);
                ll_zhenlv.setVisibility(View.GONE);

                switch (SP.getInteger(STRING_KEY_mPublishPresetoption, 1)) {
                    case 1:
                        tv_malv_di.setCompoundDrawables(null, null, drawable, null);
                        tv_malv_middle.setCompoundDrawables(null, null, null, null);
                        tv_malv_high.setCompoundDrawables(null, null, null, null);
                        break;
                    case 2:
                        tv_malv_di.setCompoundDrawables(null, null, null, null);
                        tv_malv_middle.setCompoundDrawables(null, null, drawable, null);
                        tv_malv_high.setCompoundDrawables(null, null, null, null);
                        break;
                    case 3:
                        tv_malv_di.setCompoundDrawables(null, null, null, null);
                        tv_malv_middle.setCompoundDrawables(null, null, null, null);
                        tv_malv_high.setCompoundDrawables(null, null, drawable, null);
                        break;
                }

                break;
            case 3:
                str = "视频帧率";
                ll_fenbianlv.setVisibility(View.GONE);
                ll_malv.setVisibility(View.GONE);
                ll_zhenlv.setVisibility(View.VISIBLE);
                break;
        }
        getNavigate().setTitlText(str)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @OnClick({R.id.tv_fenbianlv_di,
            R.id.tv_fenbianlv_middle,
            R.id.tv_fenbianlv_high,
            R.id.tv_malv_di,
            R.id.tv_malv_middle,
            R.id.tv_malv_high,
            R.id.tv_zhenlv_di,
            R.id.tv_zhenlv_middle,
            R.id.tv_zhenlv_high})
    void onViewClicked(View view) {
        if(type == 0) {
            showToast("设置无效，图片尺寸跟随采集尺寸。");
            return;
        }
        switch (view.getId()) {
            case R.id.tv_fenbianlv_di:
                changeFenBianLv(1);
                tv_fenbianlv_di.setCompoundDrawables(null, null, drawable, null);
                tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_fenbianlv_middle:
                changeFenBianLv(2);
                tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                tv_fenbianlv_middle.setCompoundDrawables(null, null, drawable, null);
                tv_fenbianlv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_fenbianlv_high:
                changeFenBianLv(3);
                tv_fenbianlv_di.setCompoundDrawables(null, null, null, null);
                tv_fenbianlv_middle.setCompoundDrawables(null, null, null, null);
                tv_fenbianlv_high.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.tv_malv_di:
                changeFenMaLv(1);
                tv_malv_di.setCompoundDrawables(null, null, drawable, null);
                tv_malv_middle.setCompoundDrawables(null, null, null, null);
                tv_malv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_malv_middle:
                changeFenMaLv(2);
                tv_malv_di.setCompoundDrawables(null, null, null, null);
                tv_malv_middle.setCompoundDrawables(null, null, drawable, null);
                tv_malv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_malv_high:
                changeFenMaLv(3);
                tv_malv_di.setCompoundDrawables(null, null, null, null);
                tv_malv_middle.setCompoundDrawables(null, null, null, null);
                tv_malv_high.setCompoundDrawables(null, null, drawable, null);
                break;
            case R.id.tv_zhenlv_di:
                tv_zhenlv_di.setCompoundDrawables(null, null, drawable, null);
                tv_zhenlv_middle.setCompoundDrawables(null, null, null, null);
                tv_zhenlv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_zhenlv_middle:
                tv_zhenlv_di.setCompoundDrawables(null, null, null, null);
                tv_zhenlv_middle.setCompoundDrawables(null, null, drawable, null);
                tv_zhenlv_high.setCompoundDrawables(null, null, null, null);
                break;
            case R.id.tv_zhenlv_high:
                tv_zhenlv_di.setCompoundDrawables(null, null, null, null);
                tv_zhenlv_middle.setCompoundDrawables(null, null, null, null);
                tv_zhenlv_high.setCompoundDrawables(null, null, drawable, null);
                break;
        }
    }

    private void changeFenBianLv(int level) {
        if (type == 0) {
            switch (level) {
                case 1://640x480
                    SP.putInt(STRING_KEY_photo, 1);
                    break;
                case 2://1280x720
                    SP.putInt(STRING_KEY_photo, 2);
                    break;
                case 3://1920x1080
                    SP.putInt(STRING_KEY_photo, 3);
                    break;
            }
//            Camera mCameraDevice = android.hardware.Camera.open(0);
//            Camera.Parameters param = mCameraDevice.getParameters();
//            param.setPictureSize(1920, 1080);//如果不设置会按照系统默认配置最低160x120分辨率
//            mCameraDevice.setParameters(param);
        } else {
            switch (level) {
                case 1:
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.VGA)
                    );
                    break;
                case 2:
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD720P));
                    break;
                case 3:
                    HYClient.getSdkOptions().Capture().setCustomCaptureConfig(
                            HYClient.getSdkOptions().Capture().getCaptureConfigTemplate(SDKCaptureQuality.HD1080P)
                    );
                    break;
            }
            SP.putString(STRING_KEY_capture, HYClient.getSdkOptions().Capture().getCaptureQuality().name());
        }
    }

    private void changeFenMaLv(int level) {
        switch (level) {
            case 1:
                SP.putInteger(STRING_KEY_mPublishPresetoption, 0);
                break;
            case 2:
                SP.putInteger(STRING_KEY_mPublishPresetoption, 1);
                break;
            case 3:
                SP.putInteger(STRING_KEY_mPublishPresetoption, 2);
                break;
        }
    }

    @Override
    public void doInitDelay() {

    }

}
