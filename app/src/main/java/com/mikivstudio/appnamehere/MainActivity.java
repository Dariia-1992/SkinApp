package com.mikivstudio.appnamehere;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.mikivstudio.appnamehere.fragment.BaseFragment;
import com.mikivstudio.appnamehere.fragment.IRewardAdded;
import com.mikivstudio.appnamehere.fragment.SkinsFragment;
import com.mikivstudio.appnamehere.utils.LocalStorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class MainActivity extends AppCompatActivity implements IMainManager, RewardedVideoAdListener {
    private InterstitialAd interstitialAd;
    private RewardedVideoAd videoAd;

    private IRewardAdded fragmentCallback;
    private IRewardAdded calledOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Banner ad
        AdView adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());

        // Interstitial ad
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(BuildConfig.INTERSTITIAL_ADD_UNITI_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        // Video ad
        videoAd = MobileAds.getRewardedVideoAdInstance(this);
        videoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        setScreen(SkinsFragment.createInstance(SkinsFragment.MODE_LATEST));
    }

    @Override
    protected void onResume() {
        videoAd.resume(this);
        super.onResume();

        if (calledOnResume != null) {
            calledOnResume.onAddReward();
            calledOnResume = null;
        }
    }

    @Override
    protected void onPause() {
        videoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void addScreen(BaseFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null)
            return;

        manager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void setScreen(BaseFragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null)
            return;

        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public void closeScreen() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null)
            return;

        if (manager.getBackStackEntryCount() != 0)
            manager.popBackStack();
    }

    @Override
    public void showDialog(DialogFragment dialog) {
        dialog.show(getSupportFragmentManager(), dialog.getTag());
    }

    private void loadRewardedVideoAd() {
        videoAd.loadAd(BuildConfig.REWARDEVIDEO_ADD_UNITI_ID, new AdRequest.Builder().build());
    }

    public void showInterstitialAd() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
            LocalStorage.setOpensWithoutAd(this, 0);
        }
    }

    public void showVideoAd(@NonNull IRewardAdded callback) {
        if (videoAd != null && videoAd.isLoaded()) {
            fragmentCallback = callback;
            videoAd.show();
        } else {
            showInterstitialAd();
            callback.onAddReward();
        }
    }

    //region Ad listener overrides

    @Override public void onRewardedVideoAdLoaded() { }
    @Override public void onRewardedVideoAdOpened() { }
    @Override public void onRewardedVideoStarted() { }
    @Override public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }
    @Override public void onRewarded(RewardItem rewardItem) {
        if (fragmentCallback != null) {
            calledOnResume = fragmentCallback;
            fragmentCallback = null;
        }
    }
    @Override public void onRewardedVideoAdLeftApplication() { }
    @Override public void onRewardedVideoAdFailedToLoad(int i) { }
    @Override public void onRewardedVideoCompleted() { }

    //endregion
}