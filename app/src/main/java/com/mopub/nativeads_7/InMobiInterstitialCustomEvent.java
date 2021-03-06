//package com.mopub.nativeads;
//
//import android.app.Activity;
//import android.content.Context;
//import android.util.Log;
//
//import com.inmobi.ads.InMobiAdRequestStatus;
//import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
//import com.inmobi.ads.InMobiInterstitial;
//import com.inmobi.sdk.InMobiSdk;
//import com.mopub.common.MoPub;
//import com.mopub.mobileads.CustomEventInterstitial;
//import com.mopub.mobileads.MoPubErrorCode;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import static com.inmobi.sdk.InMobiSdk.IM_GDPR_CONSENT_AVAILABLE;
//
////This file is provided by inmobi, don't change it (inmobi-ads:7.2.2)
//public class InMobiInterstitialCustomEvent extends CustomEventInterstitial
//        implements InMobiInterstitial.InterstitialAdListener2 {
//
//    private static final String TAG = InMobiInterstitialCustomEvent.class.getSimpleName();
//    private CustomEventInterstitialListener mInterstitialListener;
//    private JSONObject serverParams;
//    private String accountId = "";
//    private long placementId = -1;
//    private InMobiInterstitial iMInterstitial;
//    private static boolean mIsInMobiSdkInitialized = false;
//
//
//    @Override
//    protected void loadInterstitial(Context context, CustomEventInterstitialListener interstitialListener,
//            Map<String, Object> localExtras, Map<String, String> serverExtras) {
//
//        mInterstitialListener = interstitialListener;
//
//        Activity activity;
//        if (context != null && context instanceof Activity) {
//            activity = (Activity) context;
//        } else {
//            Log.w(TAG, "Context not an Activity. Returning error!");
//            mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
//            return;
//        }
//
//        try {
//            serverParams = new JSONObject(serverExtras);
//        } catch (Exception e) {
//            Log.e(TAG, "Could not parse server parameters");
//            e.printStackTrace();
//        }
//
//        try {
//            accountId = serverParams.getString("accountid");
//            placementId = serverParams.getLong("placementid");
//        } catch (JSONException e1) {
//            e1.printStackTrace();
//        }
//
//        final JSONObject gdprJson = new JSONObject();
//        if( InMobiGDPR.isConsentUpdated() ){
//            try {
//                gdprJson.put(IM_GDPR_CONSENT_AVAILABLE, InMobiGDPR.getConsent());
//                gdprJson.put("gdpr", InMobiGDPR.isGDPR());
//            } catch (JSONException e) {
//                Log.d(TAG, "Unable to set GDPR consent object");
//                Log.e(TAG, e.getMessage());
//            }
//        }
//
//        if (!mIsInMobiSdkInitialized) {
//            try {
//                InMobiSdk.init(context, accountId, gdprJson);
//                mIsInMobiSdkInitialized = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                mIsInMobiSdkInitialized = false;
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
//                return;
//            }
//        }
//
//        /*
//         * You may also pass the Placement ID by
//         * specifying Custom Event Data in MoPub's web interface.
//         */
//
//
//        iMInterstitial = new InMobiInterstitial(activity, placementId, this);
//		/*
//		Sample for setting up the InMobi SDK Demographic params.
//        Publisher need to set the values of params as they want.
//
//		InMobiSdk.setAreaCode("areacode");
//		InMobiSdk.setEducation(Education.HIGH_SCHOOL_OR_LESS);
//		InMobiSdk.setGender(Gender.MALE);
//		InMobiSdk.setIncome(1000);
//		InMobiSdk.setAge(23);
//		InMobiSdk.setPostalCode("postalcode");
//		InMobiSdk.setLogLevel(LogLevel.DEBUG);
//		InMobiSdk.setLocationWithCityStateCountry("blore", "kar", "india");
//		InMobiSdk.setLanguage("ENG");
//		InMobiSdk.setInterests("dance");
//		InMobiSdk.setYearOfBirth(1980);*/
//        Map<String, String> map = new HashMap<>();
//        map.put("tp", "c_mopub");
//        map.put("tp-ver", MoPub.SDK_VERSION);
//        iMInterstitial.setExtras(map);
//        iMInterstitial.load();
//    }
//
//    /*
//     * Abstract methods from CustomEventInterstitial
//     */
//
//
//    @Override
//    public void showInterstitial() {
//        if (iMInterstitial != null && iMInterstitial.isReady()) {
//            iMInterstitial.show();
//        }
//    }
//
//
//    @Override
//    public void onInvalidate() {
//    }
//
//
//    @Override
//    public void onAdDismissed(InMobiInterstitial ad) {
//        Log.d(TAG, "InMobi interstitial ad dismissed.");
//        if (mInterstitialListener != null) {
//            mInterstitialListener.onInterstitialDismissed();
//        }
//    }
//
//
//    @Override
//    public void onAdDisplayed(InMobiInterstitial ad) {
//        Log.d(TAG, "InMobi interstitial show on screen.");
//        if (mInterstitialListener != null) {
//            mInterstitialListener.onInterstitialShown();
//        }
//    }
//
//
//    @Override
//    public void onAdLoadFailed(InMobiInterstitial ad, InMobiAdRequestStatus status) {
//        Log.d(TAG, "InMobi interstitial ad failed to load.");
//        if (mInterstitialListener != null) {
//
//            if (status.getStatusCode() == StatusCode.INTERNAL_ERROR) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
//            } else if (status.getStatusCode() == StatusCode.REQUEST_INVALID) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
//            } else if (status.getStatusCode() == StatusCode.NETWORK_UNREACHABLE) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_INVALID_STATE);
//            } else if (status.getStatusCode() == StatusCode.NO_FILL) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NO_FILL);
//            } else if (status.getStatusCode() == StatusCode.REQUEST_TIMED_OUT) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_TIMEOUT);
//            } else if (status.getStatusCode() == StatusCode.SERVER_ERROR) {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.SERVER_ERROR);
//            } else {
//                mInterstitialListener.onInterstitialFailed(MoPubErrorCode.UNSPECIFIED);
//            }
//        }
//
//    }
//
//
//    @Override
//    public void onAdReceived(InMobiInterstitial ad) {
//        Log.d(TAG, "InMobi Adserver responded with an Ad");
//    }
//
//
//    @Override
//    public void onAdLoadSucceeded(InMobiInterstitial ad) {
//        Log.d(TAG, "InMobi interstitial ad loaded successfully.");
//        if (mInterstitialListener != null) {
//            mInterstitialListener.onInterstitialLoaded();
//        }
//    }
//
//
//    @Override
//    public void onAdRewardActionCompleted(InMobiInterstitial ad, Map<Object, Object> rewards) {
//        Log.d(TAG, "InMobi interstitial onRewardActionCompleted.");
//
//        if (null != rewards) {
//            Iterator<Object> iterator = rewards.keySet().iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next().toString();
//                String value = rewards.get(key).toString();
//                Log.d("Rewards: ", key + ":" + value);
//            }
//        }
//    }
//
//
//    @Override
//    public void onAdDisplayFailed(InMobiInterstitial ad) {
//        Log.d(TAG, "Interstitial ad failed to display.");
//    }
//
//
//    @Override
//    public void onAdWillDisplay(InMobiInterstitial ad) {
//        Log.d(TAG, "Interstitial ad will display.");
//    }
//
//
//    @Override
//    public void onUserLeftApplication(InMobiInterstitial ad) {
//        Log.d(TAG, "InMobi interstitial ad leaving application.");
//        mInterstitialListener.onLeaveApplication();
//    }
//
//
//    @Override
//    public void onAdInteraction(InMobiInterstitial ad, Map<Object, Object> params) {
//        Log.d(TAG, "InMobi interstitial interaction happening.");
//        if (mInterstitialListener != null) {
//            mInterstitialListener.onInterstitialClicked();
//        }
//    }
//}
