package com.zs.ui.meet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huaiye.sdk.HYClient;
import com.huaiye.sdk.core.SdkCallback;
import com.huaiye.sdk.sdkabi._api.ApiMeet;
import com.huaiye.sdk.sdkabi._params.SdkParamsCenter;
import com.huaiye.sdk.sdpmsgs.whiteboard.CStopWhiteboardShareRsp;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.common.rx.RxUtils;
import com.zs.models.ModelApis;
import com.zs.models.ModelCallback;
import com.zs.models.auth.bean.Upload;
import com.zs.models.meet.bean.Bean;
import com.zs.models.meet.bean.PhotoBean;
import com.zs.ui.meet.viewholder.ChoosePhotoHolder;
import ttyy.com.jinnetwork.core.work.HTTPResponse;


/**
 * author: admin
 * date: 2018/04/23
 * version: 0
 * mail: secret
 * desc: ChoosePhotoActivity
 */

@BindLayout(R.layout.activity_choose_photo)
public class ChoosePhotoActivity extends AppBaseActivity {

    @BindView(R.id.rv_data)
    RecyclerView rv_data;

    @BindExtra
    public int nMeetID;

    LiteBaseAdapter<PhotoBean> adapter;

    ArrayList<PhotoBean> arrays = new ArrayList<>();
    SimpleDateFormat sdf;
    Bean currentBean;

    public ChoosePhotoActivity() {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initActionBar() {
        getNavigate().setLeftIcon(R.drawable.top_guanbi)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                })
                .setTitlText(AppUtils.getString(R.string.share_img))
                .setRightText(AppUtils.getString(R.string.makesure))
                .setRightTextColor(Color.parseColor("#4D48AE"))
                .setRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentBean == null) {
                            showToast(AppUtils.getString(R.string.selected_share_img));
                            return;
                        }
                        File file = new File(new String(currentBean.data, 0, currentBean.data.length - 1));
                        if (file.length() > 1028 * 1028 * 50) {
                            showToast(AppUtils.getString(R.string.file_is_bigger_than));
                            return;
                        }
                        mZeusLoadView.loadingText(AppUtils.getString(R.string.is_upload_ing)).setLoading();
                        ModelApis.Download().upload(new ModelCallback<Upload>() {
                            @Override
                            public void onSuccess(Upload upload) {
                                if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                    mZeusLoadView.dismiss();

                                Intent intent = new Intent();
                                intent.putExtra("updata", upload);
                                setResult(Activity.RESULT_OK, intent);
                                ChoosePhotoActivity.this.finish();
                            }

                            @Override
                            public void onFailure(HTTPResponse httpResponse) {
                                super.onFailure(httpResponse);
                                showToast(AppUtils.getString(R.string.file_upload_false));
                            }

                            @Override
                            public void onFinish(HTTPResponse httpResponse) {
                                if (mZeusLoadView != null && mZeusLoadView.isShowing())
                                    mZeusLoadView.dismiss();
                            }
                        }, file);
                    }
                });
    }

    @Override
    public void doInitDelay() {
        adapter = new LiteBaseAdapter<>(this,
                arrays,
                ChoosePhotoHolder.class,
                R.layout.item_choose_photo,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bean bean = (Bean) v.getTag();
                        bean.isChecked = !bean.isChecked;

                        if (currentBean != null && currentBean != bean) {
                            currentBean.isChecked = false;
                        }

                        currentBean = bean;

                        adapter.notifyDataSetChanged();
                    }
                }, null);
        rv_data.setLayoutManager(new SafeLinearLayoutManager(this));
        rv_data.setAdapter(adapter);

        loadData();
    }

    /**
     * 加载图片
     */
    private void loadData() {
        new RxUtils<>().doOnThreadObMain(new RxUtils.IThreadAndMainDeal<ArrayList<PhotoBean>>() {
            @Override
            public ArrayList<PhotoBean> doOnThread() {
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
                arrays.clear();
                Map<String, ArrayList<Bean>> map = new HashMap<>();
                while (cursor.moveToNext()) {
                    String date = sdf.format(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)) * 1000);

                    Bean temp = new Bean();
                    temp.name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    temp.data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (map.containsKey(date)) {
                        map.get(date).add(temp);
                    } else {
                        ArrayList<Bean> array = new ArrayList<>();
                        array.add(temp);
                        map.put(date, array);
                    }
                }
                for (Map.Entry<String, ArrayList<Bean>> entry : map.entrySet()) {
                    PhotoBean bean = new PhotoBean();
                    bean.date = entry.getKey();
                    bean.photos = entry.getValue();
                    arrays.add(bean);
                }

                return arrays;
            }

            @Override
            public void doOnMain(ArrayList<PhotoBean> data) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        HYClient.getModule(ApiMeet.class)
                .stopWhiteBoard(SdkParamsCenter.Meet.StopWhiteBoard().setnMeetingID(nMeetID),
                        new SdkCallback<CStopWhiteboardShareRsp>() {
                            @Override
                            public void onSuccess(CStopWhiteboardShareRsp cStopWhiteboardShareRsp) {
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                            }
                        });

    }

}
