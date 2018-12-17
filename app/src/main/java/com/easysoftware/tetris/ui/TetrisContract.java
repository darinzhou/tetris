package com.easysoftware.tetris.ui;

import com.easysoftware.tetris.base.MvpContract;
import com.easysoftware.tetris.model.Tetrominoe;

public interface TetrisContract {
    interface View extends MvpContract.MvpView {
        void updateMenu();
        void updateScore(int clearedRowCount, long totalScore);
        void refresh();
        void displayGameOverMessage(long score);
        void drawLevel(int level);
        void drawNextTetrominoe();
    }

    interface Presenter extends MvpContract.MvpPresenter<View> {

        int getRowCount();
        int getColCount();
        int getColorIdAt(int row, int col);

        int getLevel();

        Tetrominoe getCurrentTetrominoe();
        Tetrominoe getNextTetrominoe();

        boolean isPlaying();
        boolean isStarted();
        boolean isGameOver();

        void newGame(int level);
        void control();
        void pause();
        void resume();
        void recoverState();

        void onLeftClick();
        void onRightClick();
        void onFallToLandClick();
        void onRotateClockwiseClick();
        void onRotateAnticlockwiseClick();
    }
}
