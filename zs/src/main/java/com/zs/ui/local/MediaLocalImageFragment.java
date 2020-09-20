package com.zs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.local.holder.ImageHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
    LiteBaseAdapter<FileUpload> adapter;
    List<FileUpload> datas = new ArrayList();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FileUpload bean) {
        if (bean.isImg) {
            bean.file.delete();
            int i = datas.indexOf(bean);
            if (bean.isUpload == 3) {
                datas.remove(i);
                adapter.notifyItemRemoved(i);
            } else {
                adapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onViewCreated(view, savedInstanceState);
        rcv_list = (RecyclerView) view.findViewById(R.id.rcv_list);
        rcv_list.setLayoutManager(new SafeLinearLayoutManager(getContext()));

        datas.addAll(MediaFileDaoUtils.get().getAllImgs());
        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                ImageHolder.class,
                R.layout.item_local_image_new,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileUpload bean = (FileUpload) v.getTag();
                        if (v.getId() == R.id.iv_upload) {
                            AuthApi.get().upload(bean, new ModelCallback<String>() {
                                @Override
                                public void onSuccess(String upload) {
                                }
                            }, iUploadProgress);
                        } else {
                            Intent intent = new Intent(getActivity(), ImageShowActivity.class);
                            intent.putExtra("imageUrl", bean.file.getAbsolutePath());
                            getActivity().startActivity(intent);
                        }
                    }
                }, "");
        rcv_list.setAdapter(adapter);
    }

    IUploadProgress iUploadProgress = new IUploadProgress() {
        @Override
        public void onProgress(FileUpload bean, String from) {
            if (datas.contains(bean)) {
                if (bean.isImg) {
                    int i = datas.indexOf(bean);
                    if (bean.isUpload == 3) {
                        bean.file.delete();
                        datas.remove(i);
                        adapter.notifyItemRemoved(i);
                    } else {
                        adapter.notifyItemChanged(i);
                    }
                }
            } else {
                bean.file.delete();
                adapter.notifyDataSetChanged();
            }
        }
    };

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

    }

    @Override
    public boolean isAllChoosed() {
        int totalSize = 0;
        for (FileUpload tmp : datas) {
//            if (tmp.isSelected) {
//                totalSize++;
//            }
        }
        return datas.size() == totalSize;
    }

    @Override
    public void deleteChoosed() {
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

    public void upLoadAll() {
        for (FileUpload tmp : datas) {
            if (tmp.isUpload == 0 ||
                    tmp.isUpload == 2) {
                AuthApi.get().upload(tmp, new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String upload) {
                    }
                }, iUploadProgress);
            }
        }
    }
}
