package com.easysoftware.tetris.data.localstorage;

import io.reactivex.Observable;

public interface LocalStorage {
    void write(String key, int value);
    int read(String key, int defaultValue);
    Observable<Integer> readObservable(String key, int defaultValue);

    void write(String key, long value);
    long read(String key, long defaultValue);
    Observable<Long> readObservable(String key, long defaultValue);

    void write(String key, String message);
    String read(String key, String defaultValue);
    Observable<String> readObservable(String key, String defaultValue);
}
