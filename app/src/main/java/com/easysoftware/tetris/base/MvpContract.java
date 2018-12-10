package com.easysoftware.tetris.base;

public interface MvpContract {
    interface MvpView {
        void showProgress();

        void hideProgress();

        void showError(String error);
    }

    interface MvpPresenter<V extends MvpView> {
        void start(V view);

        void stop();
    }
}
