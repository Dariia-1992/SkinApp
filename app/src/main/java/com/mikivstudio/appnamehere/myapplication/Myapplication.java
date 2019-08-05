package com.mikivstudio.appnamehere.myapplication;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.mikivstudio.appnamehere.BuildConfig;
import com.mikivstudio.appnamehere.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;


public class Myapplication extends Application {
    private static Myapplication mInstance;
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/UPBOLTERS New-Regular.otf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
    }

    public static Context getContext() {
        return mContext;
    }

    public static Myapplication getInstance() {
        return mInstance;
    }

}
