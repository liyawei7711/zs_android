package com.zs.ui.device.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteViewHolder;
import com.zs.models.device.bean.DevicePlayerBean;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class DeviceHolder extends LiteViewHolder {

    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.view_divider)
    View view_divider;

    public DeviceHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        DevicePlayerBean bean = (DevicePlayerBean) data;
        itemView.setTag(bean);

        tv_device_name.setText(bean.strChannelName + AppUtils.getString(R.string.device_name));

        if (bean.nOnlineState == 1) {
            tv_status.setText(AppUtils.getString(R.string.online));
        } else {
            tv_status.setText(AppUtils.getString(R.string.offline));
        }

        if (position == datas.size() - 1) {
            view_divider.setVisibility(View.GONE);
        } else {
            view_divider.setVisibility(View.VISIBLE);
        }
    }
}
