package com.easysoftware.tetris.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.easysoftware.tetris.model.Tetrominoe;
import com.easysoftware.tetris.ui.util.Utils;

public class TetrisView extends SurfaceView implements SurfaceHolder.Callback, TetrisContract.View {
    public static final int BLOCK_INTERVAL_DP = 1;

    private Context mContext;
    private TetrisContract.Presenter mPresenter;
    private int mWidth;
    private int mHeight;

    private int mBlockWidth;
    private int mBlockInterval;

    private Rect mRedrawRect;
    private Rect mFieldRect;
    private Rect mPreviewRect;

    private Paint mPaint;
    private SurfaceHolder mSurfaceHolder;

    public TetrisView(Context context) {
        super(context);
        mContext = context;
        getHolder().addCallback(this);
    }

    public TetrisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getHolder().addCallback(this);
    }

    public TetrisView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        if (mPresenter != null) {
            init();
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

    // connect presenter
    public void init(TetrisContract.Presenter presenter) {
        mPresenter = presenter;
        if (mSurfaceHolder != null) {
            init();
        }
    }

    @Override
    public void refresh(int clearedRowCount, long totalScore) {
        Canvas canvas = mSurfaceHolder.lockCanvas(mRedrawRect);

        // draw preview area
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mPreviewRect, mPaint);
        Tetrominoe next = mPresenter.getNextTetrominoe();
        if (next != null) {
            drawTetrominoe(canvas, next, true);
        }

        // draw field area
        for (int r = 0; r<mPresenter.getRowCount(); ++r) {
            for (int c = 0; c < mPresenter.getColCount(); ++c) {
                int color = mPresenter.getColorIdAt(r, c);
                drawBlock(canvas, r, c, colorId2Color(color), false);
            }
        }

        // draw current tetrominoe
        Tetrominoe current = mPresenter.getCurrentTetrominoe();
        if (current != null && !current.isLanded()) {
            drawTetrominoe(canvas, current, false);
        }

        // draw wall at last step
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mBlockInterval);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mFieldRect, mPaint);

        mSurfaceHolder.unlockCanvasAndPost(canvas);

        // display score
        MainActivity parent = (MainActivity)mContext;
        if (parent != null) {
            parent.displayScore(clearedRowCount, totalScore);
        }
    }

    private void init() {
        mWidth = getWidth();
        mHeight = getHeight();

        int bw1 = mWidth / mPresenter.getColCount();
        int bw2 = mHeight / (mPresenter.getRowCount() + 5);
        mBlockWidth = Math.min(bw1, bw2);

        mBlockInterval = (int) Utils.dp2px(mContext, BLOCK_INTERVAL_DP);

        int fieldWidth = mBlockWidth * mPresenter.getColCount() + (mPresenter.getColCount() + 3) * mBlockInterval;
        int fieldHeight = mBlockWidth * mPresenter.getRowCount() + (mPresenter.getRowCount() + 3) * mBlockInterval;
        int x1 = (mWidth - fieldWidth) / 2;
        int x2 = x1 + fieldWidth;
        int y1 = mBlockWidth * 4 + mBlockInterval * 5;
        int y2 = y1 + fieldHeight;
        mFieldRect = new Rect(x1, y1, x2, y2);
        mRedrawRect = new Rect(x1, 0, x2, y2);
        mPreviewRect = new Rect(x1, 0, x2, y2);


        mPaint = new Paint();

        Canvas canvas = mSurfaceHolder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        mSurfaceHolder.unlockCanvasAndPost(canvas);

        // start the presenter
        mPresenter.start(this);
    }

    private void drawTetrominoe(Canvas canvas, Tetrominoe tetrominoe, boolean preview) {
        int[][] image = tetrominoe.getImage();
        int color = colorId2Color(tetrominoe.getColorId());
        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    drawBlock(canvas, r+tetrominoe.getTopLeftRow(), c+tetrominoe.getTopLeftCol(), color, preview);
                }
            }
        }
    }

    private void drawBlock(Canvas canvas, int row, int col, int colorFace, boolean preview) {
        int x = col2x(col);
        int y = row2y(row) - (preview ? mBlockInterval * 2: 0);
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

}
