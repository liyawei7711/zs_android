package com.zs.ui.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioGroup;

import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.dao.MediaFileDaoUtils;
import com.zs.ui.local.MediaLocalImageFragment;
import com.zs.ui.local.MediaLocalVideoFragment;

import java.util.ArrayList;
import java.util.List;

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
                if(currentFragment != null) {
                    if(currentFragment instanceof MediaLocalImageFragment) {
                        ((MediaLocalImageFragment)currentFragment).upLoadAll();
                    } else if(currentFragment instanceof MediaLocalVideoFragment){
                        ((MediaLocalVideoFragment)currentFragment).upLoadAll();
                    }
                }
            }
        });
        initView();
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
