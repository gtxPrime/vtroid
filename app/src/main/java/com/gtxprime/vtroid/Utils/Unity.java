package com.gtxprime.vtroid.Utils;

import android.app.Activity;
import android.widget.LinearLayout;

public class Unity {
    private String bannerId = "Banner_Android";
    private String interstitialId = "Interstitial_Android";
    private String rewardedId = "Rewarded_Android";

    public interface GlobalAdListener {
        void onClick();
        void onComplete();
        void onError(String reason);
        void started(Boolean isStarted);
    }

    public Unity(Activity act) {
    }

    public Unity(Activity act, String gameId, boolean testMode) {
    }

    public Unity(Activity act, String gameId) {
    }

    public void setAdUnit(String bannerId, String interstitialId, String rewardedId) {
        this.bannerId = bannerId;
        this.interstitialId = interstitialId;
        this.rewardedId = rewardedId;
    }

    public void loadBanner(Activity act, LinearLayout view) {
    }

    public void loadBanner(Activity act, LinearLayout view, GlobalAdListener globalAdListener) {
        if (globalAdListener != null) {
            globalAdListener.onComplete();
        }
    }

    public void loadInterstitialAd(Activity act) {
    }

    public void loadInterstitialAd(Activity act, GlobalAdListener globalAdListener) {
        if (globalAdListener != null) {
            globalAdListener.onComplete();
        }
    }

    public void loadRewardedAd(Activity act) {
    }

    public void loadRewardedAd(Activity act, GlobalAdListener globalAdListener) {
        if (globalAdListener != null) {
            globalAdListener.onComplete();
        }
    }
}
