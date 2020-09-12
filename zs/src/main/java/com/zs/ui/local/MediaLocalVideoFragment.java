package com.zs.ui.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.zs.dao.MediaFileDaoUtils;
import com.zs.ui.local.holder.VideoHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    LiteBaseAdapter<File> adapter;
    List<File> datas = new ArrayList<>();
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

        datas.addAll(Arrays.asList(MediaFileDaoUtils.get().getAllVideos()));
        Collections.sort(datas, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return 1;
            }
        });

        adapter = new LiteBaseAdapter<>(getContext(),
                datas,
                VideoHolder.class,
                R.layout.item_local_video,
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
        showEmty();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        showEmty();
    }

    @Override
    public void setModeEdit() {
        adapter.notifyDataSetChanged();
        showEmty();
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
//        List<Integer> positions = adapter.getSelectedPositions();
//        List<MediaFileDao.MediaFile> copy = adapter.getDatasCopy();
//
//        List<MediaFileDao.MediaFile> dels = new ArrayList<>();
//        for (int i = 0; i < positions.size(); i++) {
//            int position = positions.get(i);
//            MediaFileDao.MediaFile module = adapter.getDataForItemPosition(position);
//
//            copy.remove(module);
//            dels.add(module);
//        }
//        datas.clear();
//        adapter.notifyDataSetChanged();
//
//        MediaFileDao.get().del(dels.toArray(new MediaFileDao.MediaFile[]{}));
//
//        parent.setChooseAll(isAllChoosed());
//
        showEmty();
    }

    List<Integer> uploadingIndexs = null;
    List<Integer> uploadedIndexs = null;

    @Override
    public void uploadChoosed() {
//        uploadingIndexs = adapter.getSelectedPositions();
//        uploadedIndexs = new ArrayList<>();
//        if (uploadingIndexs == null
//                || uploadingIndexs.isEmpty()) {
//            ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.selected_need_upload_file));
//            return;
//        }

        uploadVideo();
    }

    long taskSession;

    void uploadVideo() {
        ArrayList<FileTupple> paths = new ArrayList<>();

        fl_progress.setVisibility(View.VISIBLE);
        HYClient.getModule(ApiIO.class).uploadVideo(SdkParamsCenter.IO.UploadVideo()
                        .setFiles(paths),
                new CallbackUploadVideo() {
                    @Override
                    public void onTaskSession(long sessionId) {
                        Log.e("Test", "onTaskSession >>> " + sessionId);
                        taskSession = sessionId;
                    }

                    @Override
                    public boolean onUploadingFileError(int index, String path, int nCode) {
                        Log.e("Test", "onUploadingFileError >>> " + index + " >>> " + path);
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_num_is) + index + AppUtils.getString(R.string.is_error));
                        uploadingIndexs.remove(0);
                        return true;
                    }

                    @Override
                    public void onProgressChanged(int index, String path, int current, int total) {
                        currentUploadedPercent = (float) current / total;
                        Log.e("Test", "onProgressChanged >>> " + index + " >>> " + currentUploadedPercent);
                        pb_progress.setProgress((int) currentUploadedPercent);
                        tv_progress.setText(AppUtils.getString(R.string.current_progress) + (int) currentUploadedPercent + "%");
                        adapter.notifyDataSetChanged();

                        if (current == total) {
                            if (uploadingIndexs.size() > 0) {
                                uploadedIndexs.add(uploadingIndexs.remove(0));
                                currentUploadedPercent = 0;
                            }
                        }

                        showEmty();

                    }

                    @Override
                    public void onSuccess(Boolean resp) {
                        Log.e("Test", "onSuccess >>> " + resp);
                        fl_progress.setVisibility(View.GONE);
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_success));

                        rcv_list.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                List<File> copy = datas;

                                List<File> dels = new ArrayList<>();
                                for (int i = 0; i < uploadedIndexs.size(); i++) {
                                    int position = uploadedIndexs.get(i);
                                    File module = datas.get(position);

                                    copy.remove(module);
                                    dels.add(module);
                                }

                                uploadingIndexs.clear();
                                uploadedIndexs.clear();

                                datas.clear();
                                adapter.notifyDataSetChanged();

//                                MediaFileDao.get().del(dels.toArray(new MediaFileDao.MediaFile[]{}));

                                parent.setChooseAll(isAllChoosed());

                                showEmty();

                            }
                        }, 1200);
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        Log.e("Test", "onError >>> " + errorInfo.toString());
                        ((AppBaseActivity) getContext()).showToast(AppUtils.getString(R.string.upload_false));
                    }

                });

    }

    @Override
    public void cancelCurrentAction() {
        if (isUploading()) {
            File module = datas.get(uploadingIndexs.get(0));
            SdpMsgStopUploadRecordReq req = new SdpMsgStopUploadRecordReq();
            req.m_fileName = module.toString();

            HYClient.getModule(ApiIO.class).cancelUploadVideo(taskSession);

            List<File> copy = datas;

            ArrayList<File> dels = new ArrayList<>();
            for (int i = 0; i < uploadedIndexs.size(); i++) {
                int position = uploadedIndexs.get(i);
                module = datas.get(position);

                copy.remove(module);
                dels.add(module);
            }
            datas.clear();
            adapter.notifyDataSetChanged();

            parent.setChooseAll(isAllChoosed());
        } else {
            clearStates();
        }

        showEmty();
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
}
