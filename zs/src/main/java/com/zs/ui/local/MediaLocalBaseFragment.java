package com.zs.ui.local;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * author: admin
 * date: 2017/08/14
 * version: 0
 * mail: secret
 * desc: BDLXBaseFragment
 */

public abstract class MediaLocalBaseFragment extends Fragment {

    @Nullable
    @Override
    public abstract View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

}
