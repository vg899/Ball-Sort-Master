package com.example.ads

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

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
                adUnitId = AdManager.PROD_BANNER_ID
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("BannerAdView", "Banner Ad loaded successfully")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.w("BannerAdView", "Banner load code ${error.code}: ${error.message}. Switching to fallback banner...")
                        if (adUnitId != AdManager.SAMPLE_BANNER_ID) {
                            adUnitId = AdManager.SAMPLE_BANNER_ID
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
