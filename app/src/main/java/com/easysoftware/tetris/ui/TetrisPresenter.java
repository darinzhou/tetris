package com.easysoftware.tetris.ui;

import com.easysoftware.tetris.data.model.Action;
import com.easysoftware.tetris.data.model.Tetrominoe;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;

public class TetrisPresenter implements TetrisContract.Presenter {

    public static final int ROW_COUNT = 16;
    public static final int COL_COUNT = 10;

    protected TetrisContract.View mView;

    protected int[][] mField = new int[][] {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
    };

    protected Tetrominoe mCurrentTetrominoe;
    protected Tetrominoe mNextTetrominoe;

    protected BlockingQueue<Action> mActionQueue;

    @Inject
    public TetrisPresenter() {
        mActionQueue = new ArrayBlockingQueue<Action>(2);

    }

    @Override
    public void start(TetrisContract.View view) {
        mView = view;

        Random rnd = new Random();
        for (int i=0; i<100; ++i) {
            mNextTetrominoe = new Tetrominoe();
            mCurrentTetrominoe = new Tetrominoe(mNextTetrominoe);

            for (int j=0; j<=ROW_COUNT; ++j) {
                int actionType = rnd.nextInt(4);
                Action action = new Action(actionType, mField);
                if (mCurrentTetrominoe.applyAction(action)) {
                    mView.refresh();
                }
                if (mCurrentTetrominoe.isLanded()) {
                    break;
                }

                action = new Action(Action.ACTION_FALL_ONE_ROW, mField);
                if (mCurrentTetrominoe.applyAction(action)) {
                    mView.refresh();
                }
                if (mCurrentTetrominoe.isLanded()) {
                    break;
                }
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public int getRowCount() {
        return ROW_COUNT;
    }

    @Override
    public int getColCount() {
        return COL_COUNT;
    }

    @Override
    public int getColor(int row, int col) {
        return mField[row][col];
    }

    @Override
    public Tetrominoe getCurrentTetrominoe() {
        return mCurrentTetrominoe;
    }

    @Override
    public Tetrominoe getNextTetrominoe() {
        return mNextTetrominoe;
    }

    @Override
    public void onLeftClick() {
        try {
            mActionQueue.put(new Action(Action.ACTION_MOVE_LEFT, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRightClick() {
        try {
            mActionQueue.put(new Action(Action.ACTION_MOVE_RIGHT, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFallToLandClick() {
        try {
            mActionQueue.put(new Action(Action.ACTION_FALL_UNTIL_LANDED, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRotateClockwiseClick() {
        try {
            mActionQueue.put(new Action(Action.ACTION_ROTATE_CLOCKWISE, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRotateAnticlockwiseClick() {
        try {
            mActionQueue.put(new Action(Action.ACTION_ROTATE_ANTICLOCKWISE, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
