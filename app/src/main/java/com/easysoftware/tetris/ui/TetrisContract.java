package com.easysoftware.tetris.ui;

import android.graphics.Color;

import com.easysoftware.tetris.base.MvpContract;
import com.easysoftware.tetris.data.model.Tetrominoe;

import java.util.List;

public interface TetrisContract {
    interface View extends MvpContract.MvpView {
        void refresh();
    }

    interface Presenter extends MvpContract.MvpPresenter<View> {
        int getRowCount();
        int getColCount();
        int getColor(int row, int col);
        Tetrominoe getCurrentTetrominoe();
        Tetrominoe getNextTetrominoe();
        void onLeftClick();
        void onRightClick();
        void onFallToLandClick();
        void onRotateClockwiseClick();
        void onRotateAnticlockwiseClick();
    }
}
