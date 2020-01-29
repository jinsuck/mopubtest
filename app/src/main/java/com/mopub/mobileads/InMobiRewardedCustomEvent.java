package com.mopub.mobileads;

import static com.inmobi.sdk.InMobiSdk.IM_GDPR_CONSENT_AVAILABLE;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.ads.InMobiInterstitial;
import com.inmobi.ads.listeners.InterstitialAdEventListener;
import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class InMobiRewardedCustomEvent extends CustomEventRewardedVideo {

    private boolean mIsInMobiSdkInitialized = false;
    private InMobiInterstitial inmobiInterstitial;
    private JSONObject serverParams;
    private String accountId = "";
    private String placementId = "";
    private static final String TAG = InMobiRewardedCustomEvent.class.getSimpleName();


    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity activity, @NonNull Map<String, Object> localExtras,
                                            @NonNull Map<String, String> serverExtras) throws Exception {

        try {
            serverParams = new JSONObject(serverExtras);
        } catch (Exception e) {
            MoPubLog.log(MoPubLog.SdkLogEvent.ERROR, TAG, "Could not parse server parameters");
            e.printStackTrace();
        }

        try {
            accountId = serverParams.getString("accountid");
            placementId = serverParams.getString("placementid");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


        if (!mIsInMobiSdkInitialized) {
            try {
                InMobiSdk.init(activity, accountId, InMobiGDPR.getGDPRConsentDictionary());
                mIsInMobiSdkInitialized = true;
            } catch (Exception e) {
                e.printStackTrace();
                mIsInMobiSdkInitialized = false;
                return false;
            }
        }

        return true;

    }


    @Override
    @NonNull
    protected String getAdNetworkId() {
        return placementId;
    }


    @Override
    @Nullable
    protected LifecycleListener getLifecycleListener() {
        return null;
    }


    @Override
    protected boolean hasVideoAvailable() {
        return inmobiInterstitial != null && inmobiInterstitial.isReady();
    }


    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras,
                                          @NonNull Map<String, String> serverExtras) throws Exception {

        /*
         * You may also pass the Placement ID by
         * specifying Custom Event Data in MoPub's web interface.
         */
        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);

        inmobiInterstitial = new InMobiInterstitial(activity, Long.parseLong(getAdNetworkId()), new InterstitialAdEventListener() {


            @Override
            public void onAdLoadSucceeded(final InMobiInterstitial inMobiInterstitial) {
                super.onAdLoadSucceeded(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad load succeeded");
                if (inMobiInterstitial != null) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(InMobiRewardedCustomEvent.class, placementId);
                }
            }


            @Override
            public void onAdLoadFailed(final InMobiInterstitial inMobiInterstitial,
                                       final InMobiAdRequestStatus inMobiAdRequestStatus) {
                super.onAdLoadFailed(inMobiInterstitial, inMobiAdRequestStatus);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad failed to load:" + inMobiAdRequestStatus.getStatusCode().toString());
                if (inMobiAdRequestStatus.getStatusCode() == StatusCode.INTERNAL_ERROR) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.INTERNAL_ERROR);
                } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_INVALID) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NETWORK_UNREACHABLE) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.NETWORK_INVALID_STATE);
                } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.NO_FILL) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.NO_FILL);
                } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.REQUEST_TIMED_OUT) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.NETWORK_TIMEOUT);
                } else if (inMobiAdRequestStatus.getStatusCode() == StatusCode.SERVER_ERROR) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.SERVER_ERROR);
                } else {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(InMobiRewardedCustomEvent.class, getAdNetworkId(),
                            MoPubErrorCode.UNSPECIFIED);
                }
            }


            @Override
            public void onAdReceived(final InMobiInterstitial inMobiInterstitial) {
                super.onAdReceived(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "InMobi Adserver responded with an Ad");
            }


            @Override
            public void onAdClicked(final InMobiInterstitial inMobiInterstitial, final Map<Object, Object> map) {
                super.onAdClicked(inMobiInterstitial, map);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad interaction");
                MoPubRewardedVideoManager.onRewardedVideoClicked(InMobiRewardedCustomEvent.class, placementId);
            }


            @Override
            public void onAdWillDisplay(final InMobiInterstitial inMobiInterstitial) {
                super.onAdWillDisplay(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Rewarded video ad will display.");
            }


            @Override
            public void onAdDisplayed(final InMobiInterstitial inMobiInterstitial) {
                super.onAdDisplayed(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad displayed");
                MoPubRewardedVideoManager.onRewardedVideoStarted(InMobiRewardedCustomEvent.class, placementId);
            }


            @Override
            public void onAdDisplayFailed(final InMobiInterstitial inMobiInterstitial) {
                super.onAdDisplayFailed(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Rewarded video ad failed to display.");
            }


            @Override
            public void onAdDismissed(final InMobiInterstitial inMobiInterstitial) {
                super.onAdDismissed(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "Ad dismissed");
                MoPubRewardedVideoManager.onRewardedVideoClosed(InMobiRewardedCustomEvent.class, placementId);
            }


            @Override
            public void onUserLeftApplication(final InMobiInterstitial inMobiInterstitial) {
                super.onUserLeftApplication(inMobiInterstitial);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "User left application");
            }


            @Override
            public void onRewardsUnlocked(final InMobiInterstitial inMobiInterstitial, final Map<Object, Object> rewards) {
                super.onRewardsUnlocked(inMobiInterstitial, rewards);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, TAG, "InMobi Rewarded video onRewardActionCompleted.");
                if (null != rewards) {
                    Iterator<Object> iterator = rewards.keySet().iterator();
                    String key = "", value = "";
                    while (iterator.hasNext()) {
                        key = iterator.next().toString();
                        value = rewards.get(key).toString();
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, "Rewards: ", key + ":" + value);
                    }
                    try {
                        MoPubRewardedVideoManager.onRewardedVideoCompleted(InMobiRewardedCustomEvent.class, null,
                                MoPubReward.success(key, Integer.parseInt(value)));
                    } catch (Exception e) {
                        MoPubRewardedVideoManager.onRewardedVideoCompleted(InMobiRewardedCustomEvent.class, null,
                                MoPubReward.failure());
                    }
                }
            }
        });

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
		InMobiSdk.setYearOfBirth(1980);
		*/

        Map<String, String> map = new HashMap<String, String>();
        map.put("tp", "c_mopub");
        map.put("tp-ver", MoPub.SDK_VERSION);
        inmobiInterstitial.setExtras(map);
        inmobiInterstitial.load();
    }


    @Override
    protected void onInvalidate() {
    }


    @Override
    protected void showVideo() {
        if (this.hasVideoAvailable()) {
            inmobiInterstitial.show();
        } else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(InMobiRewardedCustomEvent.class, placementId,
                    MoPubErrorCode.VIDEO_PLAYBACK_ERROR);
        }
    }

}
