package com.mikivstudio.appnamehere.utils;

import android.widget.ImageView;

import com.mikivstudio.appnamehere.R;
import com.mikivstudio.appnamehere.myapplication.Myapplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by Mr. Wasir on 2/26/2018.
 */

public class ConstantFunctions {
    public static RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL); // because file name is always same

    public static void loadImage(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext())
                .load(url)
                .thumbnail(Glide.with(Myapplication.getContext()).load(R.drawable.default_skin))
                .apply(requestOptions)
                .into(iv);

    }

    public static void loadImageWithoutThumbnail(String url, final ImageView iv) {
        Glide.with(Myapplication.getContext())
                .load(url)
                .apply(requestOptions)
                .into(iv);

    }

}
