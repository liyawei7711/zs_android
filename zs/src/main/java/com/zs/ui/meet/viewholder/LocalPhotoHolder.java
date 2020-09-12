package com.zs.ui.meet.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import com.zs.R;
import com.zs.common.recycle.LiteViewHolder;
import com.zs.models.meet.bean.LocalPhotoBean;

/**
 * Created by Administrator on 2018\2\27 0027.
 */

public class LocalPhotoHolder extends LiteViewHolder {

    @BindView(R.id.iv_image)
    ImageView iv_image;
    @BindView(R.id.cb_status)
    CheckBox cb_status;
    @BindView(R.id.cb_view)
    View cb_view;

    public LocalPhotoHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        itemView.setOnClickListener(ocl);
        cb_view.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        LocalPhotoBean bean = (LocalPhotoBean) data;
        itemView.setTag(bean);
        cb_view.setTag(bean);
        Glide.with(context)
                .asBitmap()
                .load(bean.data)
                .apply(new RequestOptions().centerCrop())
                .into(iv_image);
        cb_status.setChecked(bean.isChecked);
    }
}
