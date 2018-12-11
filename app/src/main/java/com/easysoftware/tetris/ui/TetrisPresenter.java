package com.easysoftware.tetris.ui;

import com.easysoftware.tetris.model.Action;
import com.easysoftware.tetris.model.Tetrominoe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;

public class TetrisPresenter implements Runnable, TetrisContract.Presenter {

    public static final int ROW_COUNT = 16;
    public static final int COL_COUNT = 10;

    protected int[][] mField;
    protected boolean mPlaying;

    protected long mScore;

    protected Tetrominoe mCurrentTetrominoe;
    protected Tetrominoe mNextTetrominoe;

    protected BlockingQueue<Action> mActionQueue;
    protected Thread mActionsThread;

    protected Timer mTimerToFallCurrentTetrominoe;
    protected int mMillisecondsOnFallingOneRow = 1000;

    protected TetrisContract.View mView;

    @Inject
    public TetrisPresenter() {
        mField = new int[ROW_COUNT][COL_COUNT];
        for (int i=0; i<ROW_COUNT; ++i) {
            mField[i] = new int[COL_COUNT];
        }

        mActionQueue = new ArrayBlockingQueue<>(2);
    }

    @Override
    public void run() {
        while (mPlaying) {
            while (!mActionQueue.isEmpty()) {
                // check if game is stopped
                if (!mPlaying) {
                    break;
                }
                // wait for current tetrominoe is ready
                if (mCurrentTetrominoe == null) {
                    continue;
                }

                try {
                    Action action = mActionQueue.take();

                    if (mCurrentTetrominoe.applyAction(action)) {

                        // accumulate score from action
                        mScore += action.getScore();

                        // check if game is stopped before refresh
                        if (!mPlaying) {
                            break;
                        }

                        if (mNextTetrominoe == null && mCurrentTetrominoe.getTopLeftRow() == ROW_COUNT/2) {
                            mNextTetrominoe = new Tetrominoe(COL_COUNT);
                        }

                        mView.refresh(action.getClearedRowCount(), mScore);
                    }
                    if (mCurrentTetrominoe.isLanded()) {
                        generateNewTetrominoe();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateNewTetrominoe() {
        stopFalling();
        if (mNextTetrominoe == null) {
            mNextTetrominoe = new Tetrominoe(COL_COUNT);
        }
        mCurrentTetrominoe = new Tetrominoe(mNextTetrominoe);
        mNextTetrominoe = null;
        startFalling();
    }

    private void startFalling(){
        TimerTask timerTaskToFallCurrentTetrominoe = new TimerTask() {
            @Override
            public void run() {
                try {
                    mActionQueue.put(new Action(Action.ACTION_FALL_ONE_ROW, mField));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mTimerToFallCurrentTetrominoe = new Timer();
        mTimerToFallCurrentTetrominoe.scheduleAtFixedRate(timerTaskToFallCurrentTetrominoe, 0, mMillisecondsOnFallingOneRow);
    }

    private void stopFalling(){
        if (mTimerToFallCurrentTetrominoe != null) {
            mTimerToFallCurrentTetrominoe.cancel();
        }
    }

    private void startGame() {
        mScore = 0;
        mPlaying = true;
        mActionsThread = new Thread(this);
        mActionsThread.start();
        generateNewTetrominoe();
    }

    @Override
    public void start(TetrisContract.View view) {
        mView = view;
        startGame();
    }

    @Override
    public void stop() {
        startFalling();
        mPlaying = false;
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
    public int getColorIdAt(int row, int col) {
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
    public void pause() {
        mPlaying = false;
        try {
            mActionsThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resume() {
        startGame();
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
