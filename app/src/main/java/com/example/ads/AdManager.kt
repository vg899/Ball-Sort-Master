package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {

    private const val TAG = "BallSortAds"

    // Real Production AdMob Ad Unit IDs
    const val PROD_BANNER_ID = "ca-app-pub-4870901674493228/9582593465"
    const val PROD_INTERSTITIAL_ID = "ca-app-pub-4870901674493228/5132037554"
    const val PROD_REWARDED_ID = "ca-app-pub-4870901674493228/8931856777"
    const val PROD_APP_OPEN_ID = "ca-app-pub-4870901674493228/2505874210"

    // Google Official Sample Ad Units (Auto-fallback when AdMob account is pending approval)
    const val SAMPLE_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    const val SAMPLE_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    const val SAMPLE_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"
    const val SAMPLE_APP_OPEN_ID = "ca-app-pub-3940256099942544/9257395921"

    val bannerAdUnitId: String = PROD_BANNER_ID
    val interstitialAdUnitId: String = PROD_INTERSTITIAL_ID
    val rewardedAdUnitId: String = PROD_REWARDED_ID
    val appOpenAdUnitId: String = PROD_APP_OPEN_ID

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var lastInterstitialShownTime: Long = 0L
    private const val INTERSTITIAL_COOLDOWN_MS = 10_000L // 10 seconds

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private var appOpenAd: AppOpenAd? = null
    private var isAppOpenLoading = false
    private var appOpenLoadTime: Long = 0L

    private val mainHandler = Handler(Looper.getMainLooper())

    var isInitialized = false
        private set

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            try {
                android.webkit.WebView.setWebContentsDebuggingEnabled(false)
            } catch (_: Exception) {}
            MobileAds.initialize(context) { status ->
                isInitialized = true
                Log.d(TAG, "AdMob MobileAds initialized: $status")
                loadInterstitial(context)
                loadRewarded(context)
                loadAppOpen(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds", e)
        }
    }

    // --- REWARDED ADS ---

    fun loadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        // Try production ID first
        RewardedAd.load(
            context,
            PROD_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Production Rewarded Ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Production Rewarded Ad not ready (${loadAdError.code}: ${loadAdError.message}). Loading fallback so ads ALWAYS play...")
                    // Fallback to sample ad unit so user is never blocked while account approval is pending
                    RewardedAd.load(
                        context,
                        SAMPLE_REWARDED_ID,
                        adRequest,
                        object : RewardedAdLoadCallback() {
                            override fun onAdLoaded(sampleAd: RewardedAd) {
                                rewardedAd = sampleAd
                                isRewardedLoading = false
                                Log.d(TAG, "Sample Rewarded Ad ready for playback")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                rewardedAd = null
                                isRewardedLoading = false
                                Log.e(TAG, "Fallback Rewarded Ad failed: ${error.message}")
                            }
                        }
                    )
                }
            }
        )
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null

    /**
     * Show Rewarded Ad.
     * Ensures an ad ALWAYS plays on screen, giving the reward to the player.
     */
    fun showRewarded(
        activity: Activity,
        onUserEarnedReward: (amount: Int, type: String) -> Unit,
        onAdDismissed: () -> Unit = {},
        onAdFailed: () -> Unit = {}
    ) {
        val cachedAd = rewardedAd
        if (cachedAd != null) {
            presentRewardedAd(activity, cachedAd, onUserEarnedReward, onAdDismissed, onAdFailed)
        } else {
            Toast.makeText(activity, "Loading Ad... please wait a moment", Toast.LENGTH_SHORT).show()
            val adRequest = AdRequest.Builder().build()

            // Try production ad, fallback immediately if account is not approved yet
            RewardedAd.load(
                activity,
                PROD_REWARDED_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(loadedAd: RewardedAd) {
                        rewardedAd = null
                        presentRewardedAd(activity, loadedAd, onUserEarnedReward, onAdDismissed, onAdFailed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w(TAG, "Production Ad failed (${error.code}: ${error.message}). Launching fallback...")
                        RewardedAd.load(
                            activity,
                            SAMPLE_REWARDED_ID,
                            adRequest,
                            object : RewardedAdLoadCallback() {
                                override fun onAdLoaded(sampleAd: RewardedAd) {
                                    rewardedAd = null
                                    presentRewardedAd(activity, sampleAd, onUserEarnedReward, onAdDismissed, onAdFailed)
                                }

                                override fun onAdFailedToLoad(fallbackError: LoadAdError) {
                                    Log.e(TAG, "Both ads failed: ${fallbackError.message}")
                                    Toast.makeText(activity, "Could not load ad: ${fallbackError.message}", Toast.LENGTH_LONG).show()
                                    onAdFailed()
                                }
                            }
                        )
                    }
                }
            )
        }
    }

    private fun presentRewardedAd(
        activity: Activity,
        ad: RewardedAd,
        onUserEarnedReward: (amount: Int, type: String) -> Unit,
        onAdDismissed: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        activity.runOnUiThread {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewarded(activity)
                    activity.runOnUiThread { onAdDismissed() }
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    rewardedAd = null
                    loadRewarded(activity)
                    Log.e(TAG, "Failed to display ad: ${adError.message}")
                    activity.runOnUiThread { onAdFailed() }
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Rewarded Ad showed fullscreen successfully")
                }
            }

            ad.show(activity) { rewardItem ->
                activity.runOnUiThread {
                    onUserEarnedReward(rewardItem.amount, rewardItem.type)
                }
            }
        }
    }

    // --- INTERSTITIAL ADS ---

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            PROD_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Production Interstitial loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Production Interstitial failed: ${loadAdError.message}. Using fallback...")
                    InterstitialAd.load(
                        context,
                        SAMPLE_INTERSTITIAL_ID,
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(sampleAd: InterstitialAd) {
                                interstitialAd = sampleAd
                                isInterstitialLoading = false
                                Log.d(TAG, "Fallback Interstitial loaded successfully")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                interstitialAd = null
                                isInterstitialLoading = false
                            }
                        }
                    )
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {}) {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialShownTime < INTERSTITIAL_COOLDOWN_MS) {
            onDismissed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            activity.runOnUiThread {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        lastInterstitialShownTime = System.currentTimeMillis()
                        loadInterstitial(activity)
                        activity.runOnUiThread { onDismissed() }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        interstitialAd = null
                        loadInterstitial(activity)
                        activity.runOnUiThread { onDismissed() }
                    }
                }
                ad.show(activity)
            }
        } else {
            loadInterstitial(activity)
            onDismissed()
        }
    }

    // --- APP OPEN ADS ---

    fun loadAppOpen(context: Context) {
        if (appOpenAd != null || isAppOpenLoading) return
        isAppOpenLoading = true

        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            PROD_APP_OPEN_ID,
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    appOpenLoadTime = System.currentTimeMillis()
                    isAppOpenLoading = false
                    Log.d(TAG, "Production App Open ad loaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Production App Open failed: ${loadAdError.message}. Using fallback...")
                    AppOpenAd.load(
                        context,
                        SAMPLE_APP_OPEN_ID,
                        adRequest,
                        object : AppOpenAd.AppOpenAdLoadCallback() {
                            override fun onAdLoaded(sampleAd: AppOpenAd) {
                                appOpenAd = sampleAd
                                appOpenLoadTime = System.currentTimeMillis()
                                isAppOpenLoading = false
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                appOpenAd = null
                                isAppOpenLoading = false
                            }
                        }
                    )
                }
            }
        )
    }

    fun showAppOpen(activity: Activity, onDismissed: () -> Unit = {}) {
        val isNotExpired = (System.currentTimeMillis() - appOpenLoadTime) < 4 * 3600_000L
        val ad = appOpenAd
        if (ad != null && isNotExpired) {
            activity.runOnUiThread {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd = null
                        loadAppOpen(activity)
                        activity.runOnUiThread { onDismissed() }
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        appOpenAd = null
                        loadAppOpen(activity)
                        activity.runOnUiThread { onDismissed() }
                    }
                }
                ad.show(activity)
            }
        } else {
            loadAppOpen(activity)
            onDismissed()
        }
    }
}
