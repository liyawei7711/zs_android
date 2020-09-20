package com.zs.ui.local;

import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

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
    @BindExtra
    String imageUrl;

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

        if(imageUrl.endsWith(".gif")) {
            Glide.with(this).load(imageUrl).listener(requestListener).into(iv_photo);
        } else {
            Glide.with(this).load(imageUrl).apply(mOptions).into(iv_photo);
        }

    }

}
