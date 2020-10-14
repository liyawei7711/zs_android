package com.zs.ui.local.holder;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class ImageHolder extends LiteViewHolder {

    @BindView(R.id.tv_image_name)
    TextView tv_image_name;
    @BindView(R.id.tv_image_size)
    TextView tv_image_size;
    @BindView(R.id.iv_upload)
    View iv_upload;
    @BindView(R.id.pb_progress)
    ProgressBar pb_progress;

    public ImageHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        tv_image_name = view.findViewById(R.id.tv_image_name);
        tv_image_size = view.findViewById(R.id.tv_image_size);
        iv_upload = view.findViewById(R.id.iv_upload);
        pb_progress = view.findViewById(R.id.pb_progress);
        tv_image_name.setOnClickListener(ocl);
        iv_upload.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        FileUpload bean = (FileUpload) data;
        iv_upload.setTag(bean);
        tv_image_name.setTag(bean);
        tv_image_name.setText(bean.name);
        tv_image_size.setText(Formatter.formatFileSize(context, bean.file.length()));
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
