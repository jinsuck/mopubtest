package com.mopub.mobileads;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.inmobi.sdk.InMobiSdk;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;

import java.util.Map;

public class InMobiAdapterConfiguration extends BaseAdapterConfiguration {
    @NonNull
    @Override
    public String getAdapterVersion() {
        return "1.0.0";
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return "inmobi";
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return InMobiSdk.getVersion();
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull OnNetworkInitializationFinishedListener onNetworkInitializationFinishedListener) {
        onNetworkInitializationFinishedListener.onNetworkInitializationFinished(InMobiAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
    }
}
