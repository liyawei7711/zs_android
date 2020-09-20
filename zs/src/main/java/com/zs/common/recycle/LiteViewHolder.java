package com.zs.common.recycle;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.ttyy.commonanno.Finder;
import com.ttyy.commonanno.Injectors;

public abstract class LiteViewHolder extends RecyclerView.ViewHolder implements LiteBaseAdapter.HolderData {
    protected View.OnClickListener ocl;
    protected Context context;
    protected LiteBaseAdapter adapter;

    public LiteViewHolder(Context context, View view, View.OnClickListener ocl, Object obj) {
        super(view);
        this.ocl = ocl;
        this.context = context;
//        ButterKnife.bind(this, view);
        Injectors.get().inject(Finder.View, view, this);
    }

    public void setMyAdapter(LiteBaseAdapter adapter) {
        this.adapter = adapter;
    }


}
