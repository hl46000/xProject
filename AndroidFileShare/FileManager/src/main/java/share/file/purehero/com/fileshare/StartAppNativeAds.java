package share.file.purehero.com.fileshare;

import android.app.Activity;

import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import java.util.ArrayList;

/**
 * Created by purehero on 2017-07-11.
 */

public class StartAppNativeAds implements AdEventListener {
    ArrayList<NativeAdDetails> ads = null;

    NativeAdPreferences nativePrefs = new NativeAdPreferences()
            .setAdsNumber(1)                // Load 3 Native Ads
            .setAutoBitmapDownload(false)    // Retrieve Images object
            .setPrimaryImageSize(2);        // 150x150 image

    private final StartAppNativeAd startAppNativeAd;
    public StartAppNativeAds( Activity context ) {
        startAppNativeAd = new StartAppNativeAd( context );
        startAppNativeAd.loadAd(nativePrefs, this);
    }

    @Override
    public void onReceiveAd(Ad ad) {
        ads = startAppNativeAd.getNativeAds();    // get NativeAds list
    }

    @Override
    public void onFailedToReceiveAd(Ad ad) {
    }

    public int getAdsCount() {
        if( ads == null ) return 0;
        return ads.size();
    }

    public String getAdsTitle( int index ) {
        if( ads == null ) return null;
        if( getAdsCount() < index ) return null;
        return ads.get(index).getTitle();
    }

    public String getAdsSubTitle( int index ) {
        if( ads == null ) return null;
        if( getAdsCount() < index ) return null;
        return ads.get(index).getDescription();
    }

    public String getAdsIconUrl( int index ) {
        if( ads == null ) return null;
        if( getAdsCount() < index ) return null;
        return ads.get(index).getImageUrl();
    }

    public String getAdsCategoty( int index ) {
        if( ads == null ) return null;
        if( getAdsCount() < index ) return null;
        return ads.get(index).getCategory();
    }
}
