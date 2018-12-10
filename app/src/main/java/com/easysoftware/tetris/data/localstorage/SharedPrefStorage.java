package com.easysoftware.tetris.data.localstorage;

import android.content.Context;

import java.util.concurrent.Callable;

import io.reactivex.Observable;

public class SharedPrefStorage implements LocalStorage {
    public static final String SHAREDPREF_NAME = "local_storage";
    private Context mContext;

    public SharedPrefStorage(Context context) {
        mContext = context;
    }

    @Override
    public void write(String key, int value) {
        mContext.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)
                .edit().putInt(key, value).apply();
    }

    @Override
    public void write(String key, String message) {
        mContext.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)
                .edit().putString(key, message).apply();
    }

    @Override
    public int read(String key, int defaultValue) {
        return mContext.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)
                .getInt(key, defaultValue);
    }

    @Override
    public Observable<Integer> readObservable(final String key, final int defaultValue) {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return read(key, defaultValue);
            }
        });
    }

    @Override
    public String read(String key, String defaultValue) {
        return mContext.getSharedPreferences(SHAREDPREF_NAME, Context.MODE_PRIVATE)
                .getString(key, defaultValue);
    }

    @Override
    public Observable<String> readObservable(final String key, final String defaultValue) {
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return read(key, defaultValue);
            }
        });
    }
}
