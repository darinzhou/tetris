package com.easysoftware.tetris.di;

import android.app.Application;
import android.content.Context;

import com.easysoftware.tetris.data.localstorage.LocalStorage;
import com.easysoftware.tetris.data.localstorage.SharedPrefStorage;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private Application mApp;

    public ApplicationModule(final Application app) {
        mApp = app;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    LocalStorage provideLocalStorage(Context context) {
        return new SharedPrefStorage(context);
    }

}
