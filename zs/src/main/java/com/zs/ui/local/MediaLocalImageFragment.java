package com.zs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.ui.local.holder.ImageHolder;
import com.zs.ui.local.holder.VideoHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: MediaLocalPictureFragment
 */
public class MediaLocalImageFragment extends MediaLocalBaseFragment {
    RecyclerView rcv_list;
    LiteBaseAdapter<File> adapter;
    List<File> datas = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv_list = (RecyclerView) view.findViewById(R.id.rcv_list);
        rcv_list.setLayoutManager(new SafeLinearLayoutManager(getContext()));

        datas.addAll(Arrays.asList(MediaFileDaoUtils.get().getAllImgs()));
        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                ImageHolder.class,
                R.layout.item_local_image_new,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        MediaFileDao.MediaFile module = (MediaFileDao.MediaFile) v.getTag();
//                        if (adapter.getMode() == TagsAdapter.Mode.MultiChoice) {
//                            if (adapter.getSelectedPositions().size() > 0
//                                    && adapter.getSelectedPositions().size() == adapter.getItemCount()) {
//                                parent.setChooseAll(true);
//                            } else {
//                                parent.setChooseAll(false);
//                            }
//                        } else {
                        Intent intent = new Intent(getActivity(), MediaLocalVideoPlayActivity.class);
//                        intent.putExtra("path", module.getRecordPath());
                        startActivity(intent);
//                        }
                    }
                }, "");
        rcv_list.setAdapter(adapter);
    }

    @Override
    public void setModeEdit() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearStates() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearChoosed() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void chooseAll() {
        for (File tmp : datas) {
//            tmp.isSelected = true;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean isAllChoosed() {
        int totalSize = 0;
        for (File tmp : datas) {
//            if (tmp.isSelected) {
//                totalSize++;
//            }
        }
        return datas.size() == totalSize;
    }

    @Override
    public void deleteChoosed() {
        ArrayList<File> temps = new ArrayList<>();
        for (File module : datas) {
//            if (!module.isSelected) {
//                temps.add(module);
//            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void uploadChoosed() {
        Toast.makeText(getContext(), AppUtils.getString(R.string.not_support_upload_img), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelCurrentAction() {

    }

    @Override
    public boolean isUploading() {
        return false;
    }

    MediaLocalParent parent;

    @Override
    public void setParentIntf(MediaLocalParent parentIntf) {
        parent = parentIntf;
    }

}
