package com.mopub.mobileads;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.ads.InMobiBanner.AnimationType;
import com.inmobi.ads.listeners.BannerAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.DataKeys;
import com.mopub.common.MoPub;
import com.mopub.common.logging.MoPubLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class InMobiBannerCustomEvent extends CustomEventBanner {


    private CustomEventBannerListener mBannerListener;
    private static boolean mIsInMobiSdkInitialized = false;
    private String accountId = "";
    private long placementId = -1;
    private static final String TAG = InMobiBannerCustomEvent.class.getSimpleName();
    private int adWidth = 0;
    private int adHeight = 0;

    private final AdSize BANNER = new AdSize(320, 50);
    private final AdSize MEDIUM_RECTANGLE = new AdSize(300, 250);
    private final AdSize LEADER_BOARD = new AdSize(728, 90);
    private InMobiBanner imbanner;

    @Override
    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener,
                              Map<String, Object> localExtras, Map<String, String> serverExtras) {
        mBannerListener = customEventBannerListener;
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

        try {
            final JSONObject serverParams = new JSONObject(serverExtras);
            accountId = serverParams.getString("accountid");
            placementId = serverParams.getLong("placementid");


            final JSONObject localParmas = new JSONObject(localExtras);
            adWidth = localParmas.getInt(DataKeys.AD_WIDTH);
            adHeight = localParmas.getInt(DataKeys.AD_HEIGHT);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, String.valueOf(placementId));
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, accountId);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (!mIsInMobiSdkInitialized) {
            try {
                InMobiSdk.init(context, accountId, InMobiGDPR.getGDPRConsentDictionary());
                mIsInMobiSdkInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
                mIsInMobiSdkInitialized = false;
                mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
                return;
            }
        } else {
            InMobiSdk.updateGDPRConsent(InMobiGDPR.getGDPRConsentDictionary());
        }
        /*
        Sample for setting up the InMobi SDK Demographic params.
        Publisher need to set the values of params as they want.

		InMobiSdk.setAreaCode("areacode");
		InMobiSdk.setEducation(Education.HIGH_SCHOOL_OR_LESS);
		InMobiSdk.setGender(Gender.MALE);
		InMobiSdk.setAge(23);
		InMobiSdk.setPostalCode("postalcode");
		InMobiSdk.setLogLevel(LogLevel.DEBUG);
		InMobiSdk.setLocationWithCityStateCountry("blore", "kar", "india");
		InMobiSdk.setLanguage("ENG");
		InMobiSdk.setInterests("dance");
		InMobiSdk.setYearOfBirth(1980);*/
        imbanner = new InMobiBanner(context, placementId);
        imbanner.setListener(new BannerAdEventListener() {
            @Override
            public void onAdLoadSucceeded(final InMobiBanner inMobiBanner) {
                super.onAdLoadSucceeded(inMobiBanner);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "InMobi banner ad loaded successfully.");
                if (mBannerListener != null) {
                    if (inMobiBanner != null) {
                        mBannerListener.onBannerLoaded(inMobiBanner);
                    } else {
                        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
                    }
                }
            }


            @Override
            public void onAdLoadFailed(final InMobiBanner inMobiBanner,
                                       final InMobiAdRequestStatus inMobiAdRequestStatus) {
                super.onAdLoadFailed(inMobiBanner, inMobiAdRequestStatus);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad failed to load");

                if (mBannerListener != null) {

                    if (inMobiAdRequestStatus.getStatusCode() == StatusCode.INTERNAL_ERROR) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.INTERNAL_ERROR);
                    } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_INVALID) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                    } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NETWORK_UNREACHABLE) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
                    } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NO_FILL) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.NO_FILL);
                    } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_TIMED_OUT) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_TIMEOUT);
                    } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.SERVER_ERROR) {
                        mBannerListener.onBannerFailed(MoPubErrorCode.SERVER_ERROR);
                    } else {
                        mBannerListener.onBannerFailed(MoPubErrorCode.UNSPECIFIED);
                    }
                }
            }


            @Override
            public void onAdClicked(final InMobiBanner inMobiBanner, final Map<Object, Object> map) {
                super.onAdClicked(inMobiBanner, map);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad interaction");
                mBannerListener.onBannerClicked();
            }


            @Override
            public void onAdDisplayed(final InMobiBanner inMobiBanner) {
                super.onAdDisplayed(inMobiBanner);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad displayed");
            }


            @Override
            public void onAdDismissed(final InMobiBanner inMobiBanner) {
                super.onAdDismissed(inMobiBanner);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad Dismissed");
            }


            @Override
            public void onUserLeftApplication(final InMobiBanner inMobiBanner) {
                super.onUserLeftApplication(inMobiBanner);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "User left applicaton");
                mBannerListener.onLeaveApplication();
            }

            @Override
            public void onRewardsUnlocked(InMobiBanner ad, Map<Object, Object> rewards) {
                super.onRewardsUnlocked(ad, rewards);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad rewarded");
            }
        });
        imbanner.setEnableAutoRefresh(false);
        imbanner.setAnimationType(AnimationType.ANIMATION_OFF);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        Map<String, String> map = new HashMap<String, String>();
        map.put("tp", "c_mopub");
        map.put("tp-ver", MoPub.SDK_VERSION);
        imbanner.setExtras(map);
        final AdSize adSize = calculateAdSize(adWidth, adHeight);

        if (adSize == null) {
            mBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        imbanner.setLayoutParams(new LinearLayout.LayoutParams(Math.round(adSize.getWidth() * dm.density),
                Math.round(adSize.getHeight() * dm.density)));
        imbanner.load();
    }


    private AdSize calculateAdSize(int width, int height) {
        // Use the smallest AdSize that will properly contain the adView
        if (width <= 320 && height <= 50) {
            return BANNER;
        } else if (width <= 300 && height <= 250) {
            return MEDIUM_RECTANGLE;
        } else if (width <= 728 && height <= 90) {
            return LEADER_BOARD;
        } else {
            return null;
        }
    }


    @Override
    protected void onInvalidate() {
        // TODO Auto-generated method stub

    }


    private class AdSize {
        private int mWidth;
        private int mHeight;


        public AdSize(int var1, int var2) {
            mWidth = var1;
            mHeight = var2;
        }


        public int getHeight() {
            return mHeight;
        }


        public int getWidth() {
            return mWidth;
        }
    }
}

