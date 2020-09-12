package com.zs.ui.home;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.AppBaseActivity;

import static android.media.AudioManager.FLAG_PLAY_SOUND;

/**
 * author: admin
 * date: 2017/12/29
 * version: 0
 * mail: secret
 * desc: AboutActivity
 */
@BindLayout(R.layout.activity_yinliang)
public class YinLiangActivity extends AppBaseActivity {

    @BindView(R.id.seekbar_yinliang)
    SeekBar seekbar_yinliang;
    @BindView(R.id.tv_yinliang_num)
    TextView tv_yinliang_num;

    @BindView(R.id.seekbar_lingsheng)
    SeekBar seekbar_lingsheng;
    @BindView(R.id.tv_lingsheng_num)
    TextView tv_lingsheng_num;

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("音量设置")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
    }

    @Override
    public void doInitDelay() {

        final AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//音乐音量
//        int yinyue_max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
//        int yinyue_current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );

//系统音量
//        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
//        current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );

//铃声音量
//        max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING );
//        current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING );

//通话音量
        int tonghua_max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        int tonghua_current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        tv_yinliang_num.setText(tonghua_current + "");
        seekbar_yinliang.setMax(tonghua_max);
        seekbar_yinliang.setProgress(tonghua_current);
        seekbar_yinliang.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tv_yinliang_num.setText(progress + "");
                    mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, progress, FLAG_PLAY_SOUND);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//提示声音音量
        int tishi_max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int tishi_current = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        tv_lingsheng_num.setText(tishi_current + "");
        seekbar_lingsheng.setMax(tishi_max);
        seekbar_lingsheng.setProgress(tishi_current);
        seekbar_lingsheng.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tv_lingsheng_num.setText(progress + "");
                    mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, FLAG_PLAY_SOUND);
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

}
