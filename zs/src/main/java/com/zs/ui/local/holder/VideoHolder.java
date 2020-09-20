package com.zs.ui.local.holder;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiye.cmf.JniIntf;
import com.zs.R;
import com.zs.common.recycle.LiteViewHolder;
import com.zs.ui.local.bean.FileUpload;

import java.util.List;

import com.ttyy.commonanno.anno.BindView;

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
    @BindView(R.id.iv_upload)
    View iv_upload;
    @BindView(R.id.pb_progress)
    ProgressBar pb_progress;

    public VideoHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        tv_video_name = view.findViewById(R.id.tv_video_name);
        tv_video_size = view.findViewById(R.id.tv_video_size);
        iv_upload = view.findViewById(R.id.iv_upload);
        pb_progress = view.findViewById(R.id.pb_progress);
        itemView.setOnClickListener(ocl);
        iv_upload.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        FileUpload bean = (FileUpload) data;
        itemView.setTag(bean);
        iv_upload.setTag(bean);
        tv_video_name.setText(bean.name);
        tv_video_size.setText(JniIntf.GetRecordFileDuration(bean.file.getAbsolutePath()) + "");
        if(bean.totalBytes == 0) {
            pb_progress.setMax(100);
            pb_progress.setProgress(0);
        } else {
            pb_progress.setMax((int) bean.totalBytes);
            pb_progress.setProgress((int) (bean.totalBytes - bean.remainingBytes));
        }
        switch (bean.isUpload) {
            case 0:
            case 2:
                iv_upload.setVisibility(View.VISIBLE);
                pb_progress.setVisibility(View.GONE);
                break;
            case 1:
                iv_upload.setVisibility(View.GONE);
                pb_progress.setVisibility(View.VISIBLE);
                break;
            case 3:
                iv_upload.setVisibility(View.GONE);
                pb_progress.setVisibility(View.GONE);
                break;
        }
    }
}
