package org.myaj.myaj.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.myaj.myaj.BuildConfig;
import org.myaj.myaj.network.api.UpdateCheckApi;
import org.myaj.myaj.network.entity.VersionInfo;
import org.myaj.myaj.tool.SimpleObserver;

import com.stardust.util.NetworkUtils;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Stardust on 2017/9/20.
 */

public class VersionService {

    private static final String KEY_DEPRECATED = "KEY_DEPRECATED";
    private static final String KEY_DEPRECATED_VERSION_CODE = "KEY_DEPRECATED_VERSION_CODE";

    private static VersionService sInstance = new VersionService();
    private boolean mDeprecated = false;
    private VersionInfo mVersionInfo;
    private SharedPreferences mSharedPreferences;
    private Retrofit mRetrofit;

    public VersionService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://www.autojs.org/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                        .setLenient()
                        .create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static VersionService getInstance() {
        return sInstance;
    }

    public Observable<VersionInfo> checkForUpdates() {
        return mRetrofit.create(UpdateCheckApi.class)
                .checkForUpdates()
                .subscribeOn(Schedulers.io());
    }


    private void readDeprecatedFromPref(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (mSharedPreferences.getInt(KEY_DEPRECATED_VERSION_CODE, 0) < BuildConfig.VERSION_CODE) {
            mSharedPreferences.edit().remove(KEY_DEPRECATED_VERSION_CODE)
                    .putBoolean(KEY_DEPRECATED, false)
                    .apply();
        }
        mDeprecated = mSharedPreferences.getBoolean(KEY_DEPRECATED, false);
    }


    public void readDeprecatedFromPrefIfNeeded(Context context) {
        if (mSharedPreferences == null) {
            readDeprecatedFromPref(context);
        }
    }

    public boolean isCurrentVersionDeprecated() {
        return mDeprecated;
    }

    public String getCurrentVersionIssues() {
        if (mVersionInfo == null)
            return null;
        VersionInfo.OldVersion oldVersion = mVersionInfo.getOldVersion(BuildConfig.VERSION_CODE);
        if (oldVersion == null)
            return null;
        return oldVersion.issues;
    }

    public Observable<VersionInfo> checkForUpdatesIfNeededAndUsingWifi(Context context) {
        if (mVersionInfo == null) {
            return checkUpdateIfUsingWifi(context);
        }
        return Observable.just(mVersionInfo);

    }

    private Observable<VersionInfo> checkUpdateIfUsingWifi(Context context) {
        if (!NetworkUtils.isWifiAvailable(context)) {
            return Observable.empty();
        }
        Observable<VersionInfo> observable = checkForUpdates();
        observable.subscribe(new SimpleObserver<VersionInfo>() {
            @Override
            public void onNext(@NonNull VersionInfo versionInfo) {
                if (versionInfo.isValid()) {
                    setVersionInfo(versionInfo);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
            }
        });
        return observable;
    }

    private void setVersionInfo(VersionInfo result) {
        mDeprecated = BuildConfig.VERSION_CODE <= result.deprecated;
        mVersionInfo = result;
        if (mDeprecated) {
            mSharedPreferences.edit().putBoolean(KEY_DEPRECATED, mDeprecated)
                    .putInt(KEY_DEPRECATED_VERSION_CODE, BuildConfig.VERSION_CODE)
                    .apply();
        }
    }
}
