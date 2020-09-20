package com.zs.ui.auth.holder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.recycle.LiteViewHolder;

import java.util.List;

/**
 * author: admin
 * date: 2018/05/07
 * version: 0
 * mail: secret
 * desc: DomainHolder
 */

public class OrgHolder extends LiteViewHolder {
    @BindView(R.id.ll_root)
    LinearLayout ll_root;
    @BindView(R.id.tv_name)
    TextView tv_name;

    public OrgHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(context, view, ocl, obj);
        ll_root = view.findViewById(R.id.ll_root);
        tv_name = view.findViewById(R.id.tv_name);
        itemView.setOnClickListener(ocl);
    }

    @Override
    public void bindData(Object holder, int position, Object data, int size, List datas, Object extr) {
        OrgBean bean = (OrgBean) data;
        itemView.setTag(bean);
        tv_name.setText(bean.name);

        if (bean.isSelected) {
            tv_name.setBackgroundResource(R.drawable.all_blue_shapke_corners);
        } else {
            tv_name.setBackgroundResource(R.drawable.all_huise_shapke_corners);
        }
    }
}
