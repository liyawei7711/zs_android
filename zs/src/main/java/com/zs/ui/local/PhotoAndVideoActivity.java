package com.zs.ui.local;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.bus.NetChange;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;
import com.zs.dao.auth.AppAuth;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.zs.common.AppUtils.showToast;

/**
 * author: admin
 * date: 2017/12/28
 * version: 0
 * mail: secret
 * desc: LoginActivity
 */
@BindLayout(R.layout.activity_photo_video)
public class PhotoAndVideoActivity extends AppBaseActivity {

    @BindView(R.id.rg_group)
    RadioGroup rg_group;
    @BindView(R.id.tv_upload_all)
    View tv_upload_all;

    private FragmentManager mSupportFragmentManager;

    private MediaLocalImageFragment imageFragment;
    private MediaLocalVideoFragment videoFragment;
    private MediaLocalBaseFragment currentFragment;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNavigate().setVisibility(View.GONE);
    }

    @Override
    protected void initActionBar() {

    }


    @Override
    public void doInitDelay() {
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_photo:
                        hideOthersFragment(imageFragment, true);
                        break;
                    case R.id.rb_video:
                        hideOthersFragment(videoFragment, true);
                        break;
                }
            }
        });
        tv_upload_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int netStatus = AppUtils.getNetWorkStatus(PhotoAndVideoActivity.this);
                if (netStatus == -1) {
                    showToast("当前无网络");
                    return;
                }
                if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                    showToast("当前未登录");
                    return;
                }
                if (currentFragment != null) {
                    if (currentFragment instanceof MediaLocalImageFragment) {
                        ((MediaLocalImageFragment) currentFragment).upLoadAll();
                    } else if (currentFragment instanceof MediaLocalVideoFragment) {
                        ((MediaLocalVideoFragment) currentFragment).upLoadAll();
                    }
                }
            }
        });
        initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(NetChange bean) {
        int netStatus = AppUtils.getNetWorkStatus(this);
        if (netStatus == 0) {
            if (TextUtils.isEmpty(AppAuth.get().getUserLoginName())) {
                return;
            }
            imageFragment.upLoadAll();
            videoFragment.upLoadAll();
        }
    }

    private void initView() {
        mSupportFragmentManager = getSupportFragmentManager();
        //设置默认选中首页
        imageFragment = new MediaLocalImageFragment();
        videoFragment = new MediaLocalVideoFragment();
        mFragments.add(imageFragment);
        mFragments.add(videoFragment);

        mSupportFragmentManager.beginTransaction().add(R.id.fl_content, imageFragment)
                .add(R.id.fl_content, videoFragment)
                .commit();

        hideOthersFragment(imageFragment, true);
    }

    private void hideOthersFragment(MediaLocalBaseFragment showFragment, boolean add) {
        currentFragment = showFragment;
        FragmentTransaction transaction = mSupportFragmentManager.beginTransaction();
        for (Fragment fragment : mFragments) {
            if (showFragment.equals(fragment)) {
                transaction.show(fragment);
            } else {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

}
