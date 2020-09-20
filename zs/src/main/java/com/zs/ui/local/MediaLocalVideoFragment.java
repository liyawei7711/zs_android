package com.zs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiye.cmf.sdp.SdpMsgStopUploadRecordReq;
import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.sdkabi._api.ApiIO;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdkabi._params.io.FileTupple;
import com.huaiye.sdk.sdkabi.abilities.io.callback.CallbackUploadVideo;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.common.rx.RxUtils;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.models.ModelCallback;
import com.zs.models.auth.AuthApi;
import com.zs.ui.local.bean.FileUpload;
import com.zs.ui.local.holder.VideoHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * author: admin
 * date: 2017/09/05
 * version: 0
 * mail: secret
 * desc: MediaLocalVideoFragment
 */

public class MediaLocalVideoFragment extends MediaLocalBaseFragment {
    RecyclerView rcv_list;
    View ll_empty;
    View fl_progress;
    ProgressBar pb_progress;
    TextView tv_progress;

    LiteBaseAdapter<FileUpload> adapter;
    List<FileUpload> datas = new ArrayList<>();
    float currentUploadedPercent = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv_list = view.findViewById(R.id.rcv_list);
        ll_empty = view.findViewById(R.id.ll_empty);
        fl_progress = view.findViewById(R.id.fl_progress);
        pb_progress = view.findViewById(R.id.pb_progress);
        tv_progress = view.findViewById(R.id.tv_progress);

        rcv_list.setLayoutManager(new SafeLinearLayoutManager(getContext()));

        rcv_list.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
                return false;
            }
        });

        datas.addAll(MediaFileDaoUtils.get().getAllVideos());

        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                VideoHolder.class,
                R.layout.item_local_video,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileUpload fileUpload = (FileUpload) v.getTag();
                        if (v.getId() == R.id.iv_upload) {
                            AuthApi.get().upload(fileUpload, new ModelCallback<String>() {
                                @Override
                                public void onSuccess(String upload) {

                                }
                            }, iUploadProgress);
                        } else {

                            Intent intent = new Intent(getActivity(), MediaLocalVideoPlayActivity.class);
                            intent.putExtra("path", fileUpload.file.getAbsolutePath());
                            startActivity(intent);
//                            AuthApi.get().upload(fileUpload, new ModelCallback<String>() {
//                                @Override
//                                public void onSuccess(String upload) {

//                                }
//                            }, iUploadProgress);
                        }
                    }
                }, "");
        rcv_list.setAdapter(adapter);
//        showEmty();
    }

    IUploadProgress iUploadProgress = new IUploadProgress() {
        @Override
        public void onProgress(FileUpload bean, String from) {
            if (bean.isUpload == 3) {
                if (datas.indexOf(bean) != -1) {
                    datas.remove(bean);
                    bean.file.delete();
                }
            }
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        showEmty();
    }

    @Override
    public void setModeEdit() {
    }

    private void showEmty() {
        if (ll_empty != null) {
            if (datas != null && datas.size() <= 0) {
                ll_empty.setVisibility(View.VISIBLE);
            } else {
                ll_empty.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void clearStates() {
    }

    @Override
    public void clearChoosed() {
    }

    @Override
    public void chooseAll() {
    }

    @Override
    public boolean isAllChoosed() {
        return false;
    }

    @Override
    public void deleteChoosed() {
        showEmty();
    }

    List<Integer> uploadingIndexs = null;
    List<Integer> uploadedIndexs = null;

    @Override
    public void uploadChoosed() {

    }

    @Override
    public void cancelCurrentAction() {

    }

    @Override
    public boolean isUploading() {
        return uploadingIndexs != null && uploadingIndexs.size() > 0;
    }

    MediaLocalParent parent;

    @Override
    public void setParentIntf(MediaLocalParent parentIntf) {
        parent = parentIntf;
        showEmty();
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
