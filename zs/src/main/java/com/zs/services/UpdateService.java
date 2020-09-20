package com.zs.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.zs.dao.auth.AppAuth;

import java.io.File;

import ttyy.com.jinnetwork.Https;
import ttyy.com.jinnetwork.core.callback.HTTPCallback;
import ttyy.com.jinnetwork.core.work.HTTPRequest;
import ttyy.com.jinnetwork.core.work.HTTPResponse;

/**
 * author: admin
 * date: 2018/02/22
 * version: 0
 * mail: secret
 * desc: UpdateService
 */

public class UpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UpdateService() {
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Https.get("ddd")
                .addHeader("X-Token", AppAuth.get().getToken())
                .setDownloadMode(new File("dd"))
                .setHttpCallback(new HTTPCallback() {
                    @Override
                    public void onPreStart(HTTPRequest httpRequest) {

                    }

                    @Override
                    public void onProgress(HTTPResponse httpResponse, long l, long l1) {

                    }

                    @Override
                    public void onSuccess(HTTPResponse httpResponse) {

                    }

                    @Override
                    public void onCancel(HTTPRequest httpRequest) {

                    }

                    @Override
                    public void onFailure(HTTPResponse httpResponse) {

                    }

                    @Override
                    public void onFinish(HTTPResponse httpResponse) {

                    }
                })
                .build()
                .requestNowAsync();
    }

}
