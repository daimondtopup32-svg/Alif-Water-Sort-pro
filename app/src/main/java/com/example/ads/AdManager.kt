package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // User's provided Google AdMob IDs
    const val APP_ID = "ca-app-pub-1131981412237081~3673088402"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-1131981412237081/3962974856"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-1131981412237081/5141248595"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-1131981412237081/9302845720"

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                Log.d(TAG, "AdMob MobileAds initialized successfully with app ID: $APP_ID")
                loadRewardedAd(context)
                loadInterstitialAd(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize AdMob: ${e.message}")
        }
    }

    // =========================================================================
    // REWARDED ADS (Used for watching ads to get free extra tubes)
    // =========================================================================
    fun loadRewardedAd(
        context: Context,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((String) -> Unit)? = null
    ) {
        if (isRewardedLoading || rewardedAd != null) {
            onLoaded?.invoke()
            return
        }

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "RewardedAd successfully loaded")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.e(TAG, "RewardedAd failed to load: ${loadAdError.message}")
                    onFailed?.invoke(loadAdError.message)
                }
            }
        )
    }

    fun isAdAvailable(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onDismissed: () -> Unit,
        onFailed: (String) -> Unit
    ) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    Log.e(TAG, "RewardedAd failed to show: ${adError.message}")
                    loadRewardedAd(activity)
                    onFailed(adError.message)
                }
            }

            currentAd.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onRewardEarned()
            }
        } else {
            loadRewardedAd(activity)
            onFailed("Ad is loading, please try again in a moment")
        }
    }

    // =========================================================================
    // INTERSTITIAL ADS (Shown between levels or on victory transition)
    // =========================================================================
    fun loadInterstitialAd(
        context: Context,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((String) -> Unit)? = null
    ) {
        if (isInterstitialLoading || interstitialAd != null) {
            onLoaded?.invoke()
            return
        }

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "InterstitialAd successfully loaded")
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.e(TAG, "InterstitialAd failed to load: ${loadAdError.message}")
                    onFailed?.invoke(loadAdError.message)
                }
            }
        )
    }

    fun isInterstitialAvailable(): Boolean = interstitialAd != null

    fun showInterstitialAd(
        activity: Activity,
        onDismissed: () -> Unit = {}
    ) {
        val currentAd = interstitialAd
        if (currentAd != null) {
            currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    Log.e(TAG, "InterstitialAd failed to show: ${adError.message}")
                    loadInterstitialAd(activity)
                    onDismissed()
                }
            }
            currentAd.show(activity)
        } else {
            loadInterstitialAd(activity)
            onDismissed()
        }
    }
}

// =========================================================================
// BANNER AD COMPOSABLE (Standard 320x50 Banner at top or bottom)
// =========================================================================
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdManager.BANNER_AD_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
