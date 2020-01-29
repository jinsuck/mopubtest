package com.imvu.mopubtest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import com.mopub.nativeads.GooglePlayServicesNative
import kotlinx.android.synthetic.main.activity_main.*

private const val logTag = "MoPubTestActivity"
private val adUnitIds = arrayOf(
    "3cdf839229ce442a81c7b53e81917c0e",
    "4579779369084e168e988fe787ce90e4",
    "a9a626260c0140109cc419fbfeb6306f",
    "3ed646370fa74fae9f60f18fb1f3daef"
)

class MainActivity : AppCompatActivity() {
    var adUnitArrayIndex = 0
    var startLoadingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initMoPub { sdkInitSuccess ->
            Log.d(logTag, "initMoPub success: $sdkInitSuccess")
            if (sdkInitSuccess) {
                buttons_layout.visibility = View.VISIBLE
            }
        }

        button_load_ad.setOnClickListener {
            hideOneAd()
            showOneAd()
        }
    }

    private fun initMoPub(callback: (Boolean) -> Unit) {
        val builder = SdkConfiguration.Builder(adUnitIds[0])
            .withMediationSettings(GooglePlayServicesNative.GooglePlayServicesMediationSettings())
        MoPub.initializeSdk(this, builder.build()) {
            callback(MoPub.isSdkInitialized())
        }
    }

    private fun showOneAd() {
        val adUnitId: String = adUnitIds.get(adUnitArrayIndex++)
        if (adUnitArrayIndex == adUnitIds.size) adUnitArrayIndex = 0

        val adViewContainer = layoutInflater.inflate(R.layout.ad_view, ad_view_anchor)
        val adView: MoPubView = adViewContainer.findViewById(R.id.ad_view)
        adView.adUnitId = adUnitId
        adView.adSize = MoPubView.MoPubAdSize.MATCH_VIEW
        adView.bannerAdListener = bannerAdListener
        adView.loadAd()
        Log.d(logTag, "loadAd $adUnitId")
    }

    private fun hideOneAd() {
        findViewById<MoPubView>(R.id.ad_view)?.run {
            bannerAdListener = null
            destroy()
        }
        ad_view_anchor.removeAllViews()
    }

    private val bannerAdListener = object : MoPubView.BannerAdListener {
        override fun onBannerExpanded(banner: MoPubView?) {
        }

        override fun onBannerLoaded(banner: MoPubView?) {
            Log.d(logTag, "onBannerLoaded")
            updateInfo("loaded (${startLoadingCount++})")
        }

        override fun onBannerCollapsed(banner: MoPubView?) {
        }

        override fun onBannerFailed(banner: MoPubView?, errorCode: MoPubErrorCode?) {
            Log.d(logTag, "onBannerFailed")
            updateInfo("failed (${startLoadingCount++}) $errorCode")
        }

        override fun onBannerClicked(banner: MoPubView?) {
        }
    }

    override fun onDestroy() {
        hideOneAd()
        super.onDestroy()
    }

    private fun updateInfo(message: String) {
        val threadCount = Thread.currentThread().threadGroup.parent?.activeCount() ?: 0
        info_text.text = "$message, thread count $threadCount"
    }
}
