package com.zs.ui.local;

import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.huaiye.cmf.sdp.SdpMessageBase;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.media.player.HYPlayer;
import com.huaiye.sdk.media.player.Player;
import com.huaiye.sdk.media.player.msg.SdkMsgNotifyPlayStatus;
import com.huaiye.sdk.media.player.sdk.mix.VideoCallbackWrapper;
import com.huaiye.sdk.media.player.sdk.params.base.VideoParams;
import com.ttyy.commonanno.anno.BindLayout;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.views.MediaRecordProgress;

/**
 * author: admin
 * date: 2017/09/15
 * version: 0
 * mail: secret
 * desc: MediaLocalVideoPlayActivity
 */
@BindLayout(R.layout.activity_local_play)
public class MediaLocalVideoPlayActivity extends AppBaseActivity {

    TextureView video_texture;
    MediaRecordProgress view_progress;
    ImageView iv_play_status;
    ImageView iv_thumbnail;

    String path;

    boolean isVideoPaused = false;
    boolean initPlayer = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_play);

        path = getIntent().getStringExtra("path");
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    public void doInitDelay() {
        video_texture = findViewById(R.id.video_texture);
        view_progress = findViewById(R.id.view_progress);
        iv_play_status = findViewById(R.id.iv_play_status);
        iv_thumbnail = findViewById(R.id.iv_thumbnail);

        //加载缩略图
        loadThumbNail();
    }


    /**
     * 开始播放录像
     */
    private void startPlay() {
        view_progress.attachVideoTexture(video_texture);

        HYClient.getHYPlayer().startPlay(Player.Params.TypeVideoOfflineRecord()
                .setResourcePath(path)
                .setPreview(video_texture)
                .setMixCallback(new VideoCallbackWrapper() {
                    @Override
                    public void onSuccess(VideoParams param) {
                        super.onSuccess(param);
                        showToast(AppUtils.getString(R.string.player_success));
                    }

                    @Override
                    public void onGetVideoRange(VideoParams param, int start, int end) {
                        super.onGetVideoRange(param, start, end);
                        view_progress.setMaxTime(end);
                    }

                    @Override
                    public void onVideoProgressChanged(VideoParams param, HYPlayer.ProgressType type, int current, int total) {
                        super.onVideoProgressChanged(param, type, current, total);
                        view_progress.setCurrentTime(current);
                    }

                    @Override
                    public void onVideoStatusChanged(VideoParams param, SdpMessageBase msg) {
                        super.onVideoStatusChanged(param, msg);
                        if (msg instanceof SdkMsgNotifyPlayStatus) {
                            SdkMsgNotifyPlayStatus status = (SdkMsgNotifyPlayStatus) msg;
                            if (status.isStopped() && !isFinishing()) {

                                if (!status.isOperationFromUser()) {
                                    showToast(AppUtils.getString(R.string.player_complete));
                                    resetPlayStatus();
//                                    finish();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(VideoParams param, SdkCallback.ErrorInfo errorInfo) {
                        super.onError(param, errorInfo);
                        showToast(errorInfo.getMessage());
                        onBackPressed();
                    }
                }));
    }


    /**
     * 暂停播放
     */
    private void pausePlay(boolean value) {
        HYClient.getHYPlayer().pausePlayEx(value, video_texture);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay(true);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_status:
                //play or pause
                if (initPlayer) {
                    startPlay();
                    initPlayer = false;
                } else {
                    isVideoPaused = HYClient.getHYPlayer().togglePausePlayEx(video_texture)
                            .isPlayPausedEx(video_texture);
                }
                //set PlayStatus
                if (isVideoPaused) {
                    iv_play_status.setImageResource(R.drawable.ic_play_start);
                } else {
                    iv_play_status.setImageResource(R.drawable.ic_play_pause);
                }
                //set setThumbNailVisible
                setThumbNailVisible(false);
                break;
        }
    }


    private void loadThumbNail() {
        Glide.with(this).load(path).into(iv_thumbnail);
        setThumbNailVisible(true);
    }


    private void setThumbNailVisible(boolean visible) {
        if (visible) {
            iv_thumbnail.setVisibility(View.VISIBLE);
        } else {
            iv_thumbnail.setVisibility(View.GONE);
        }
    }


    private void resetPlayStatus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initPlayer = true;
                iv_play_status.setImageResource(R.drawable.ic_play_start);
                view_progress.setCurrentTime(0);
                setThumbNailVisible(true);
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        HYClient.getHYPlayer().stopPlayEx(null, video_texture);
        super.onBackPressed();
    }
}
