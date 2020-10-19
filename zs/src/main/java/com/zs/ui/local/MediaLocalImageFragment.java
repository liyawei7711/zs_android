package com.zs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zs.R;
import com.zs.bus.UploadMediaFile;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.dao.auth.AppAuth;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.local.holder.ImageHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.zs.common.AppUtils.showToast;


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
    boolean isClick;
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
    public void onEvent(UploadMediaFile bean) {
        if (!bean.bean.isImg) {
            return;
        }
        for(FileUpload temp : datas) {
            if(temp.file.getAbsolutePath().equals(bean.bean.file.getAbsolutePath())) {
                temp.isUpload = bean.bean.isUpload;
                temp.remainingBytes = bean.bean.remainingBytes;
                temp.totalBytes = bean.bean.totalBytes;
                temp.isImg = bean.bean.isImg;
                int i = datas.indexOf(temp);
                if (temp.isUpload == 3) {
                    temp.file.delete();
                    if (i != -1) {
                        datas.remove(i);
                    }
                    adapter.notifyDataSetChanged();
                }
                if(!isClick) {
                    adapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
                            int netStatus = AppUtils.getNetWorkStatus(getContext());
                            if (netStatus == -1) {
                                showToast("当前无网络");
                                return;
                            }
                            if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                                showToast("当前未登录");
                                return;
                            }
                            AuthApi.get().upload(bean, new ModelCallback<String>() {
                                @Override
                                public void onSuccess(String upload) {
                                }
                            }, iUploadProgress);
                        } else {
                            if(bean.isUpload == 1) {
                                showToast("文件正在上传");
                                return;
                            }
                            int postion = datas.indexOf(bean);
                            ArrayList<String> imageUrl = new ArrayList<>();
                            for(FileUpload temp : datas) {
                                imageUrl.add(temp.file.getAbsolutePath());
                            }
                            Intent intent = new Intent(getActivity(), ImageShowActivity.class);
                            intent.putExtra("imageUrl", imageUrl);
                            intent.putExtra("postion", postion);
                            getActivity().startActivity(intent);
                        }
                    }
                }, "");
        rcv_list.setAdapter(adapter);
        rcv_list.addOnItemTouchListener(new RecyclerView.OnItemTouchListener(){

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isClick = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        isClick = false;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

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
                        if(!isClick) {
                            adapter.notifyItemChanged(i);
                        }
                    }
                }
            } else {
                bean.file.delete();
                adapter.notifyDataSetChanged();
            }
        }
    };

    public void upLoadAll(boolean isAuto) {
        for (FileUpload tmp : datas) {
            if (tmp.isUpload == 0 ||
                    tmp.isUpload == 2) {
                if(isAuto && tmp.file.getName().contains("__")) {
                    break;
                }
                AuthApi.get().upload(tmp, new ModelCallback<String>() {
                    @Override
                    public void onSuccess(String upload) {
                    }
                }, iUploadProgress);
            }
        }
    }

}
