package com.zs.ui.home.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import com.zs.R;
import com.zs.common.AppUtils;
import com.zs.common.recycle.LiteBaseAdapter;
import com.zs.common.recycle.SafeLinearLayoutManager;
import com.zs.common.rx.RxUtils;
import com.zs.common.views.PermissionUtils;
import com.zs.common.views.WindowManagerUtils;
import com.zs.dao.AppDatas;
import com.zs.dao.msgs.VssMessageBean;
import com.zs.dao.msgs.VssMessageListBean;
import com.zs.dao.msgs.VssMessageListMessages;
import com.zs.ui.chat.ChatActivity;
import com.zs.ui.home.MainActivity;
import com.zs.ui.home.holder.BroadCastMsgListHolder;

/**
 * author: admin
 * date: 2018/05/16
 * version: 0
 * mail: secret
 * desc: ActionBarLayout
 */

public class BroadCastMsgLayout extends FrameLayout {

    RecyclerView rv_list;

    LiteBaseAdapter<VssMessageBean> adapter;
    ArrayList<VssMessageBean> datas = new ArrayList<>();
    RxUtils rxUtils;

    public BroadCastMsgLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BroadCastMsgLayout(@NonNull final Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
        setVisibility(GONE);
        View view = LayoutInflater.from(context).inflate(R.layout.main_broadcast_msg_layout, null);
        addView(view);

        rv_list = view.findViewById(R.id.rv_list);

        adapter = new LiteBaseAdapter<>(context,
                datas,
                BroadCastMsgListHolder.class,
                R.layout.item_broadcast_msg_layout,
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VssMessageBean tag = (VssMessageBean) v.getTag();
                        datas.remove(tag);
                        adapter.notifyDataSetChanged();
                        if (v.getId() == R.id.iv_close) {
                        } else {
                            if (AppUtils.isTalk) {

                                if (WindowManagerUtils.simpleView instanceof TalkViewLayout) {

                                } else {

                                    if (PermissionUtils.XiaoMiMobilePermission(AppUtils.ctx)) {
                                        return;
                                    }

                                    ((MainActivity) context).waitAcceptLayout.removeTalkView();
                                    WindowManagerUtils.createSmall(AppUtils.getTvl_view(context));
                                }

                                jumpToChat(tag, context);

                            } else {
                                jumpToChat(tag, context);
                            }
                        }
                    }
                }, "");
        SafeLinearLayoutManager layoutManager = new SafeLinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_list.setLayoutManager(layoutManager);
        rv_list.setAdapter(adapter);

    }

    private void jumpToChat(VssMessageBean tag, @NonNull Context context) {
        VssMessageListBean bean = VssMessageListMessages.get().getMessages(tag.sessionID);
        bean.isRead = 1;
        VssMessageListMessages.get().isRead(bean);

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("listBean", bean);
        context.startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(VssMessageBean bean) {
        if (bean.type == AppUtils.CAPTURE_TYPE_INT ||
                bean.type == AppUtils.PLAYER_TYPE_INT ||
                bean.type == AppUtils.PLAYER_TYPE_PERSON_INT ||
                bean.type == AppUtils.PLAYER_TYPE_DEVICE_INT ||
                bean.fromUserId.equals(AppDatas.Auth().getUserID() + "")) {
            return;
        }
//        if (datas.size() == 3) {
//            datas.remove(0);
//        }
        datas.add(bean);
        adapter.notifyDataSetChanged();
//        rxUtils.doDelay(5000, new RxUtils.IMainDelay() {
//            @Override
//            public void onMainDelay() {
//                try {
//                    datas.remove(0);
//                    adapter.notifyDataSetChanged();
//                } catch (Exception e) {
//
//                }
//            }
//        }, System.currentTimeMillis() + "");

    }

    public void showThisView() {
        if (rxUtils == null) {
            rxUtils = new RxUtils();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setVisibility(VISIBLE);
    }

    /**
     * 关闭当前view
     */
    public void closeThisView() {
        EventBus.getDefault().unregister(this);
        rxUtils = null;
        setVisibility(GONE);
    }


}
