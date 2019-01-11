package com.easysoftware.tetris.ui;

import com.easysoftware.tetris.model.Action;
import com.easysoftware.tetris.model.Tetrominoe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.inject.Inject;

public class TetrisPresenter implements Runnable, TetrisContract.Presenter {

    public static final int LEVEL_BASIC = 0;
    public static final int LEVEL_INTERMEDIATE = 1;
    public static final int LEVEL_ADVANCED = 2;

    public static final int SCORE_TO_INTERMEDIATE = 10000;
    public static final int SCORE_TO_ADVANCED = 20000;

    public static final int ROW_COUNT = 16;
    public static final int COL_COUNT = 10;

    public static final int ACTION_QUEUE_SIZE = 16;

    protected int[][] mField;
    protected boolean mPlaying;
    protected boolean mStarted;

    protected int mLevel;
    protected long mScore;
    protected int mLastClearedRowCount;
    protected int mTopUnemptyRow;

    protected Tetrominoe mCurrentTetrominoe;
    protected Tetrominoe mNextTetrominoe;

    protected BlockingQueue<Action> mActionQueue;
    protected Thread mActionsThread;

    protected Timer mTimerToFallCurrentTetrominoe;
    protected int mMillisecondsOnFallingOneRow;

    protected TetrisContract.View mView;

    @Inject
    public TetrisPresenter() {
        mTopUnemptyRow = ROW_COUNT;
    }

    private void setLevel(int level) {
        mLevel = level;
        switch (mLevel) {
            case LEVEL_ADVANCED:
                mMillisecondsOnFallingOneRow = 200;
                break;
            case LEVEL_INTERMEDIATE:
                mMillisecondsOnFallingOneRow = 500;
                break;
            case LEVEL_BASIC:
            default:
                mMillisecondsOnFallingOneRow = 1000;
                break;
        }
    }

    private boolean upgrade() {
        int level = mLevel;
        if (mLevel == LEVEL_BASIC && mScore > SCORE_TO_INTERMEDIATE) {
            level = LEVEL_INTERMEDIATE;
        } else if (mLevel == LEVEL_INTERMEDIATE && mScore > SCORE_TO_ADVANCED) {
            level = LEVEL_ADVANCED;
        }

        if (level != mLevel) {
            setLevel(level);
            mView.drawLevel(mLevel);
            return true;
        }

        return false;
    }

    private void init(int level) {
        mScore = 0;
        mLastClearedRowCount = 0;

        setLevel(level);

        mField = new int[ROW_COUNT][COL_COUNT];
        for (int i=0; i<ROW_COUNT; ++i) {
            mField[i] = new int[COL_COUNT];
        }

        mCurrentTetrominoe = null;
        mNextTetrominoe = null;

        mActionQueue = new ArrayBlockingQueue<>(ACTION_QUEUE_SIZE);
    }

    @Override
    public void run() {
        while (mPlaying) {
            while (!mActionQueue.isEmpty()) {
                try {
                    if (!takeAction(mActionQueue.take())) {
                        stopGame();
                        mView.displayGameOverMessage(mScore);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int topUnemptyRow() {
        int r;
        for (r=0; r<ROW_COUNT; ++r) {
            for (int c = 0; c < COL_COUNT; ++c) {
                if (mField[r][c] != 0) {
                    return r;
                }
            }
        }
        return r;
    }

    private boolean takeAction(Action action) {
        if (action == null || !action.applyTo(mCurrentTetrominoe)) {
            return true;
        }

        // accumulate score from action
        mScore += action.getScore();

        // check upgrade
        upgrade();

        // update scene
        mView.refresh();

        // if current tetrominoe is landed, generate new current one
        if (mCurrentTetrominoe.isLanded()) {
            mTopUnemptyRow = topUnemptyRow();
            if (isGameOver()) {
                mStarted = false;
                return false;
            }

            // new tetrominoe
            generateNewTetrominoe(false);

            // cleared row count by this action
            mLastClearedRowCount = action.getClearedRowCount();

            // update scene
            mView.refresh();
            mView.updateScore(mLastClearedRowCount, mScore);
        }

        return true;
    }

    private void generateNewTetrominoe(boolean isResume) {
        if (!isResume) {
            stopFalling();
            if (mNextTetrominoe == null) {
                mNextTetrominoe = new Tetrominoe(COL_COUNT);
            }
            mCurrentTetrominoe = new Tetrominoe(mNextTetrominoe);
            mNextTetrominoe = new Tetrominoe(COL_COUNT);
            mView.drawNextTetrominoe();
        }
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

    private void startGame(boolean isResume) {
        mPlaying = true;
        mActionsThread = new Thread(this);
        mActionsThread.start();

        generateNewTetrominoe(isResume);

        mView.updateMenu();
    }

    private void stopGame() {
        stopFalling();
        mPlaying = false;
        mView.updateMenu();
    }

    @Override
    public void attachView(TetrisContract.View view) {
        mView = view;
    }

    @Override
    public void stop() {
        stopGame();
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
    public int getLevel() {
        return mLevel;
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
    public boolean isPlaying() {
        return mPlaying;
    }

    @Override
    public boolean isStarted() {
        return mStarted;
    }

    @Override
    public boolean isGameOver() {
        return mTopUnemptyRow == 0;
    }

    @Override
    public void newGame(int level) {
        init(level);
        mStarted = true;
        startGame(false);
        mView.drawLevel(mLevel);
    }

    @Override
    public void control() {
        if (mPlaying) {
            pause();
        } else {
            resume();
        }
    }

    @Override
    public void pause() {
        stopGame();
        mView.saveTempScoreRecords(mScore);
    }

    @Override
    public void resume() {
        startGame(true);
    }

    @Override
    public void recoverState() {
        mView.refresh();
        mView.drawLevel(mLevel);
        mView.drawNextTetrominoe();
        mView.updateScore(mLastClearedRowCount, mScore);
    }

    @Override
    public void onLeftClick() {
        if (!mPlaying) {
            return;
        }
        try {
            mActionQueue.put(new Action(Action.ACTION_MOVE_LEFT, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRightClick() {
        if (!mPlaying) {
            return;
        }
        try {
            mActionQueue.put(new Action(Action.ACTION_MOVE_RIGHT, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFallToLandClick() {
        if (!mPlaying) {
            return;
        }
        try {
            mActionQueue.put(new Action(Action.ACTION_FALL_UNTIL_LANDED, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRotateClockwiseClick() {
        if (!mPlaying) {
            return;
        }
        try {
            mActionQueue.put(new Action(Action.ACTION_ROTATE_CLOCKWISE, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRotateAnticlockwiseClick() {
        if (!mPlaying) {
            return;
        }
        try {
            mActionQueue.put(new Action(Action.ACTION_ROTATE_ANTICLOCKWISE, mField));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
