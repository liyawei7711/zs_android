package com.zs.ui.home;

import android.media.AudioManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.dao.auth.AppAuth;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_liangdu)
public class LiangDuActivity extends AppBaseActivity {

    @BindView(R.id.seekbar_liangdu)
    SeekBar seekbar_liangdu;
    @BindView(R.id.tv_liangdu_num)
    TextView tv_liangdu_num;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("亮度设置")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        seekbar_liangdu.setMax(255);
        String strLiangDu = AppAuth.get().get("liangdu");
        if(TextUtils.isEmpty(strLiangDu)) {
            tv_liangdu_num.setText(systemBrightness + "");
            seekbar_liangdu.setProgress(systemBrightness);
        } else {
            int liangdu = Integer.parseInt(strLiangDu);
            tv_liangdu_num.setText(liangdu + "");
            seekbar_liangdu.setProgress(liangdu);
        }
        seekbar_liangdu.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tv_liangdu_num.setText(progress + "");
                    Window window = LiangDuActivity.this.getWindow();
                    WindowManager.LayoutParams lp = window.getAttributes();
                    if (progress == -1) {
                        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                    } else {
                        lp.screenBrightness = progress;
                    }
                    window.setAttributes(lp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        AppAuth.get().put("liangdu", tv_liangdu_num.getText().toString());
        super.onBackPressed();
    }
}
