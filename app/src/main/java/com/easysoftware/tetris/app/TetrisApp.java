package com.easysoftware.tetris.app;

import android.app.Application;

import com.easysoftware.tetris.di.ActivityComponent;
import com.easysoftware.tetris.di.AppComponent;
import com.easysoftware.tetris.di.ApplicationModule;
import com.easysoftware.tetris.di.DaggerAppComponent;

public class TetrisApp extends Application {

    private AppComponent mAppComponent;
    private ActivityComponent mActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        createAppComponent();
    }

    private void createAppComponent() {
        mAppComponent = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public ActivityComponent createActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = mAppComponent.addSubComponent();
        }
        return mActivityComponent;
    }

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    public void releaseActivityComponent() {
        mActivityComponent = null;
    }
}
