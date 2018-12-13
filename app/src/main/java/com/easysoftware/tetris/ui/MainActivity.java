package com.easysoftware.tetris.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easysoftware.tetris.R;
import com.easysoftware.tetris.app.TetrisApp;
import com.easysoftware.tetris.base.BaseActivity;

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

        // init game view
        mTetrisView = findViewById(R.id.tetrisView);
        // set the presenter, which was injected
        mTetrisView.initialize(mTetrisPresenter);
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
        if (mTetrisPresenter != null) {
            mTetrisPresenter.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTetrisPresenter != null) {
            mTetrisPresenter.stop();
        }
        ((TetrisApp) getApplication()).releaseActivityComponent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem pause = menu.findItem(R.id.pause);
        Resources res = getResources();
        String text = mTetrisPresenter.isPlaying() ?
                res.getString(R.string.pause) : res.getString(R.string.resume);
        pause.setTitle(text);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                mTetrisView.newGame(true);
                return true;
            case R.id.pause:
                mTetrisPresenter.control();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
