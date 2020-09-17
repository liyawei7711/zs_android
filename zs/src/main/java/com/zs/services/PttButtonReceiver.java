package com.zs.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huaiye.sdk.logger.Logger;

import com.zs.MCApp;
import com.zs.bus.KeyCodeEvent;
import com.zs.common.AppUtils;

import org.greenrobot.eventbus.EventBus;

public class PttButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("PttButtonReceiver action " +intent.getAction());
        if (!AppUtils.isHide){
            Logger.log("PttButtonReceiver app is hide " + AppUtils.isHide);
            return;
        }
        EventBus.getDefault().post(new KeyCodeEvent(intent.getAction()));
        //适配科里讯设备
        if(intent.getAction().equals("com.dfl.s02.pttDown")){
            return;
        }

        if(intent.getAction().equals("com.dfl.s02.pttup")){
            return;
        }


        Bundle bundle = intent.getExtras();
        if (bundle == null){
            return;
        }

        if (intent.getAction().equals("android.intent.action.PTT_KEY_DOWN")  ){
            boolean haveDown = bundle.getBoolean("action");
            if (haveDown){
            }else {

            }

        }
    }
}
