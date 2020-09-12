package com.zs.ui.local.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.zs.R;
import com.zs.common.recycle.LiteViewHolder;
import com.zs.dao.MediaFileDao;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class VideoHolder extends LiteViewHolder {

    @BindView(R.id.tv_video_name)
    TextView tv_video_name;
    @BindView(R.id.tv_video_size)
    TextView tv_video_size;

    public VideoHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        File bean = (File) data;
        itemView.setTag(bean);
        tv_video_name.setText(bean.getName());
        tv_video_size.setText(JniIntf.GetRecordFileDuration(bean.getAbsolutePath()) + "");
    }
}
