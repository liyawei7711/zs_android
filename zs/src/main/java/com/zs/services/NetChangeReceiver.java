package com.zs.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huaiye.sdk.logger.Logger;
import com.zs.bus.KeyCodeEvent;
import com.zs.bus.NetChange;
import com.zs.common.AppUtils;

import org.greenrobot.eventbus.EventBus;

public class NetChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("PttButtonReceiver action " +intent.getAction());
        EventBus.getDefault().post(new NetChange());
    }

}
