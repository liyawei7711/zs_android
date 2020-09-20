package com.zs.ui.home.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;
import com.zs.R;
import com.zs.common.recycle.LiteViewHolder;

import java.util.List;

import com.ttyy.commonanno.anno.BindView;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class MainZsHolder extends LiteViewHolder {


    @BindView(R.id.main_icon)
    ImageView main_icon;
    @BindView(R.id.main_name)
    TextView main_name;

    public MainZsHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        main_icon = view.findViewById(R.id.main_icon);
        main_name = view.findViewById(R.id.main_name);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        MainZSMenuBean bean = (MainZSMenuBean) data;
        itemView.setTag(bean);
        main_name.setText(bean.name);
        main_icon.setImageResource(bean.img_id);
    }

}
