package com.zs.ui.device.iviews;

import android.support.v4.widget.SwipeRefreshLayout;

import com.zs.common.AppBaseActivity;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: IDeviceListView
 */

public interface IDeviceListView {
    AppBaseActivity getContext();
    SwipeRefreshLayout getRefView();
}
