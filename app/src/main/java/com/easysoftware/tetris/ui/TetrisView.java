package com.easysoftware.tetris.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.easysoftware.tetris.R;
import com.easysoftware.tetris.model.Tetrominoe;
import com.easysoftware.tetris.ui.util.Utils;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback, TetrisContract.View {
    public static final int BLOCK_INTERVAL_DP = 1;
    public static final int BLOCK_COUNT_FOR_PREVIEW = 4;

    private MainActivity mParentActivity;
    private TetrisContract.Presenter mPresenter;
    private int mWidth;
    private int mHeight;

    private int mBlockWidth;
    private int mBlockInterval;

    private Rect mRedrawRect;
    private Rect mFieldRect;
    private Rect mPreviewRect;
    private Rect mLevelRect;
    private Rect mNextRect;

    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;

    public TetrisView(Context context) {
        super(context);
        init(context);
    }

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mParentActivity = (MainActivity)context;
        getHolder().addCallback(this);
    }

    // connect presenter
    public void initialize(TetrisContract.Presenter presenter) {
        mPresenter = presenter;
        if (mSurfaceHolder != null) {
            initView();
            startGame();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        if (mPresenter != null) {
            initView();
            startGame();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String error) {

    }

    @Override
    public void updateMenu() {
        if (mParentActivity != null) {
            mParentActivity.invalidateOptionsMenu();
        }
    }

    @Override
    public void updateScore(int clearedRowCount, long totalScore) {
        // display score
        if (mParentActivity != null) {
            mParentActivity.displayScore(clearedRowCount, totalScore);
        }
    }

    @Override
    public void refresh() {
        Canvas canvas = mSurfaceHolder.lockCanvas(mRedrawRect);

        // draw preview area
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mPreviewRect, mPaint);

        // draw field area
        for (int r = 0; r<mPresenter.getRowCount(); ++r) {
            for (int c = 0; c < mPresenter.getColCount(); ++c) {
                int color = mPresenter.getColorIdAt(r, c);
                drawBlock(canvas, r, c, colorId2Color(color));
            }
        }

        // draw current tetrominoe
        Tetrominoe current = mPresenter.getCurrentTetrominoe();
        if (current != null && !current.isLanded()) {
            drawTetrominoe(canvas, current);
        }

        // draw wall at last step
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mBlockInterval);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mFieldRect, mPaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void displayGameOverMessage(final long score) {
        mParentActivity.displayGameOverMessage(score);
    }

    @Override
    public void saveTempScoreRecords(final long score) {
        mParentActivity.saveTempScoreRecords(score);
    }

    @Override
    public void drawLevel(int level) {
        int resId = R.drawable.basic;
        switch (level) {
            case TetrisPresenter.LEVEL_ADVANCED:
                resId = R.drawable.advanced;
                break;
            case TetrisPresenter.LEVEL_INTERMEDIATE:
                resId = R.drawable.intermediate;
                break;
            case TetrisPresenter.LEVEL_BASIC:
            default:
                resId = R.drawable.basic;
                break;

        }
        Drawable drawable = mParentActivity.getResources().getDrawable(resId);
        drawable.setBounds(mLevelRect);
        Canvas canvas = mSurfaceHolder.lockCanvas(mLevelRect);
        drawable.draw(canvas);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void drawNextTetrominoe() {
        Tetrominoe tetrominoe = mPresenter.getNextTetrominoe();
        if (tetrominoe == null) {
            return;
        }

        int[][] image = tetrominoe.getImage();
        int rowCount = image.length;
        int blockWidth = (Math.min(mNextRect.width(), mNextRect.height()) - rowCount + 1)/rowCount;
        int color = colorId2Color(tetrominoe.getColorId());

        Canvas canvas = mSurfaceHolder.lockCanvas(mNextRect);
        canvas.drawColor(Color.BLACK);

        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    drawBlockForNext(canvas, r, c, color, blockWidth);
                }
            }
        }

        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    public void drawBackground() {
        // set black as background
        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    private void initView() {
        mPaint = new Paint();
        mWidth = getWidth();
        mHeight = getHeight();

        int bw1 = mWidth / mPresenter.getColCount();
        int bw2 = mHeight / (mPresenter.getRowCount() + BLOCK_COUNT_FOR_PREVIEW);
        mBlockWidth = Math.min(bw1, bw2);

        mBlockInterval = (int) Utils.dp2px(mParentActivity, BLOCK_INTERVAL_DP);

        int fieldWidth = mBlockWidth * mPresenter.getColCount() + (mPresenter.getColCount() + 3) * mBlockInterval;
        int fieldHeight = mBlockWidth * mPresenter.getRowCount() + (mPresenter.getRowCount() + 3) * mBlockInterval;
        int x1 = (mWidth - fieldWidth) / 2;
        int x2 = x1 + fieldWidth;
        int y1 = mBlockWidth * (BLOCK_COUNT_FOR_PREVIEW - 1) + mBlockInterval * (BLOCK_COUNT_FOR_PREVIEW - 2);
        int y2 = y1 + fieldHeight;
        mFieldRect = new Rect(x1, y1, x2, y2);
        mRedrawRect = new Rect(x1, 0, x2, y2);
        mPreviewRect = new Rect(x1, 0, x2, y2);

        int lw = Math.min(x1, y1);
        int lx1 = (x1 - lw)/2;
        int ly1 = (y1 - lw)/2;
        int lx2 = lx1 + lw;
        int ly2 = ly1 + lw;
        mLevelRect = new Rect(lx1,ly1,lx2,ly2);
        mNextRect = new Rect(x2 + lx1,ly1,mWidth-lx1,y1-ly1);

        // black background
        drawBackground();

        // start the presenter
        mPresenter.attachView(this);
    }

    private void startGame() {
        if (mPresenter.isStarted()) {
            mPresenter.recoverState();
        } else {
            mParentActivity.showNewGameDlg(false);
        }
    }

    private void drawTetrominoe(Canvas canvas, Tetrominoe tetrominoe) {
        int[][] image = tetrominoe.getImage();
        int color = colorId2Color(tetrominoe.getColorId());
        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    drawBlock(canvas, r+tetrominoe.getTopLeftRow(), c+tetrominoe.getTopLeftCol(), color);
                }
            }
        }
    }

    private void drawBlock(Canvas canvas, int row, int col, int colorFace) {
        int x = col2x(col);
        int y = row2y(row);
        int stroke = 1;

        mPaint.setColor(colorFace);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + stroke, y + stroke, x + mBlockWidth - 2*stroke, y + mBlockWidth - 2*stroke, mPaint);
    }

    private int col2x(int col) {
        return mFieldRect.left + (col + 2) * mBlockInterval + col * mBlockWidth;
    }

    private int row2y(int row) {
        return mFieldRect.top + (row + 2) * mBlockInterval + row * mBlockWidth;
    }

    private int colorId2Color(int colorIndex) {
        switch (colorIndex) {
            case 0:
                return Color.GRAY;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.MAGENTA;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.YELLOW;
            case 6:
            default:
                return Color.RED;
        }
    }

    private void drawBlockForNext(Canvas canvas, int row, int col, int colorFace, int blockWidth) {
        int x = mNextRect.left + (col + 2) * 1 + col * blockWidth;;
        int y = mNextRect.top + (row + 2) * 1 + row * blockWidth;
        int stroke = 1;

        mPaint.setColor(colorFace);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x + stroke, y + stroke, x + blockWidth - 2*stroke, y + blockWidth - 2*stroke, mPaint);
    }

}
