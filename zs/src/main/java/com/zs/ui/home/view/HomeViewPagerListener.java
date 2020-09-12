package com.zs.ui.home.view;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.huaiye.sdk.sdpmsgs.auth.CNotifyGPSStatus;

import java.util.List;

import com.zs.common.AppBaseActivity;
import com.zs.map.baidu.appcluster.MyCluster;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: IMainView
 */

public interface HomeViewPagerListener {

    void itemPagerClick(int postion);

    void itemClose(int postion);

}
