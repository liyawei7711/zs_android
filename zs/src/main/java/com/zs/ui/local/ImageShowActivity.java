package com.zs.ui.local;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ttyy.commonanno.anno.BindLayout;
import com.ttyy.commonanno.anno.BindView;
import com.ttyy.commonanno.anno.route.BindExtra;
import com.zs.R;
import com.zs.common.AppBaseActivity;
import com.zs.common.AppUtils;

import java.util.ArrayList;


/**
 * author: zhangzhen
 * date: 2019/07/26
 * version: 0
 * mail: secret
 * desc: ImageShowActivity
 */

@BindLayout(R.layout.activity_image_show)
public class ImageShowActivity extends AppBaseActivity {
    private RequestListener requestListener;
    @BindView(R.id.iv_photo)
    ImageView iv_photo;
    @BindView(R.id.tv_current)
    TextView tv_current;
    @BindExtra
    ArrayList<String> imageUrl;
    @BindExtra
    int postion;

    private RequestOptions mOptions = new RequestOptions()
            .fitCenter()
            .dontAnimate()
            .format(DecodeFormat.PREFER_RGB_565)
            .placeholder(R.drawable.icon_image_default)
            .error(R.drawable.icon_image_error);

    @Override
    protected void initActionBar() {
        getNavigate().setTitlText("查看图片")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void doInitDelay() {

        if(requestListener == null) {
            requestListener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    if (resource instanceof GifDrawable) {
                        //加载一次
                        ((GifDrawable) resource).setLoopCount(100);
                    }
                    return false;
                }
            };
        }

        loadImage(postion);
    }

    private void loadImage(int index) {

        if(index < 0) {
            index = 0;
        } else if(index > imageUrl.size() - 1){
            index = imageUrl.size() - 1;
        }
        tv_current.setText((index+1)+"/"+imageUrl.size());
        if(imageUrl.get(index).endsWith(".gif")) {
            Glide.with(this).load(imageUrl.get(index)).listener(requestListener).into(iv_photo);
        } else {
            Glide.with(this).load(imageUrl.get(index)).apply(mOptions).into(iv_photo);
        }
    }

    int mTouchDownX;
    int mLastTouchX;
    int mLastTouchY;
    long time;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                time = System.currentTimeMillis();
                mTouchDownX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(System.currentTimeMillis() - time < 800) {
                    mLastTouchX = x;
                    if(mTouchDownX - mLastTouchX > AppUtils.getScreenWidth() / 3) {
                        loadImage(++postion);
                    } else if(mLastTouchX - mTouchDownX > AppUtils.getScreenWidth() / 3) {
                        loadImage(--postion);
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}
