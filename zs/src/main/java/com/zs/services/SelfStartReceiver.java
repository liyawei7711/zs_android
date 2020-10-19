package com.zs.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import org.greenrobot.eventbus.EventBus;

import com.zs.bus.PhoneStatus;
import com.zs.ui.auth.LoginActivity;
import com.zs.ui.guide.WelcomeActivity;
import com.zs.ui.home.MainZSActivity;

/**
 * Created by Administrator on 2018\11\28.
 */

public class SelfStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            try {
                Thread.sleep(2000L);
                Intent i = new Intent(context, MainZSActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
