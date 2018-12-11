package com.easysoftware.tetris.ui;

import com.easysoftware.tetris.base.MvpContract;
import com.easysoftware.tetris.model.Tetrominoe;

public interface TetrisContract {
    interface View extends MvpContract.MvpView {
        void refresh(int clearedRowCount, long totalScore);
    }

    interface Presenter extends MvpContract.MvpPresenter<View> {

        int getRowCount();
        int getColCount();
        int getColorIdAt(int row, int col);

        Tetrominoe getCurrentTetrominoe();
        Tetrominoe getNextTetrominoe();

        void pause();
        void resume();

        void onLeftClick();
        void onRightClick();
        void onFallToLandClick();
        void onRotateClockwiseClick();
        void onRotateAnticlockwiseClick();
    }
}
