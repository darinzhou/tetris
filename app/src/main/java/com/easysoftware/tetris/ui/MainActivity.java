package com.easysoftware.tetris.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.easysoftware.tetris.R;
import com.easysoftware.tetris.app.TetrisApp;
import com.easysoftware.tetris.base.BaseActivity;
import com.easysoftware.tetris.ui.util.SingleChoiceDlgFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    TetrisPresenter mTetrisPresenter;

    private TetrisView mTetrisView;
    private TextView mTvClearedRowCount;
    private TextView mTvTotalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DI injection
        ((TetrisApp) getApplication()).createActivityComponent().inject(this);

        // set game view
        mTetrisView = findViewById(R.id.tetrisView);

        // init control buttons
        findViewById(R.id.tvLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisPresenter.onLeftClick();
            }
        });
        findViewById(R.id.tvRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisPresenter.onRightClick();
            }
        });
        findViewById(R.id.tvRotateLeft).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisPresenter.onRotateAnticlockwiseClick();
            }
        });
        findViewById(R.id.tvRotateRight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisPresenter.onRotateClockwiseClick();
            }
        });
        findViewById(R.id.tvDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTetrisPresenter.onFallToLandClick();
            }
        });

        // score views
        mTvTotalScore = findViewById(R.id.tvTotalScore);
        mTvClearedRowCount = findViewById(R.id.tvClearedRowCount);

        // set level and start game
        setLevel();
    }

    private void setLevel() {
        Resources res = getResources();
        SingleChoiceDlgFragment dlg = SingleChoiceDlgFragment.newInstance(
                res.getString(R.string.level_title), res.getStringArray(R.array.levels_array),
                new SingleChoiceDlgFragment.OnChooseListener() {
                    @Override
                    public void onChoose(int which) {
                        // presenter, was injected
                        mTetrisView.init(mTetrisPresenter);
                    }

                    @Override
                    public void onCancel() {
                        MainActivity.this.finish();
                    }
                });
        dlg.setCancelable(false);
        dlg.show(getSupportFragmentManager(), "level_dlg");

    }

    public void displayScore(final int clearedRowCount, final long totalScore) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String clearedRowCountString = "" + clearedRowCount;
                String totalScoreString = "" + totalScore;
                if (clearedRowCount > 0) {
                    mTvClearedRowCount.setText(clearedRowCountString);
                }
                mTvTotalScore.setText(totalScoreString);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTetrisPresenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTetrisPresenter != null) {
            mTetrisPresenter.stop();
        }
        ((TetrisApp) getApplication()).releaseActivityComponent();
    }

}
