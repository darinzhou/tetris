package com.easysoftware.tetris.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.easysoftware.tetris.R;
import com.easysoftware.tetris.app.TetrisApp;
import com.easysoftware.tetris.base.BaseActivity;
import com.easysoftware.tetris.data.localstorage.LocalStorage;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    public static final String SCORE_RECORDS_BASIC = "score_records_basic";
    public static final String SCORE_RECORDS_INTERMEDIATE = "score_records_intermediate";
    public static final String SCORE_RECORDS_ADVANCED = "score_records_advanced";

    @Inject
    TetrisPresenter mTetrisPresenter;

    @Inject
    LocalStorage mLocalStorage;

    private CompositeDisposable mCompositeDisposable;

    private TetrisView mTetrisView;
    private TextView mTvClearedRowCount;
    private TextView mTvTotalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DI injection
        ((TetrisApp) getApplication()).createActivityComponent().inject(this);
        mCompositeDisposable = new CompositeDisposable();

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

    private String getRecordKey() {
        int level = mTetrisPresenter.getLevel();
        switch (level) {
            case TetrisPresenter.LEVEL_ADVANCED:
                return SCORE_RECORDS_ADVANCED;
            case TetrisPresenter.LEVEL_INTERMEDIATE:
                return SCORE_RECORDS_INTERMEDIATE;
            case TetrisPresenter.LEVEL_BASIC:
            default:
                return SCORE_RECORDS_BASIC;
        }
    }

    private String handleRecords(String records, long score) {
        String key = getRecordKey();
        String extraMessage = "";

        if (TextUtils.isEmpty(records)) {
            mLocalStorage.write(key, score + ",0,0");
            return extraMessage;
        }

        String[] parts = records.split(",");
        if (parts.length != 3) {
            if (score != 0) {
                extraMessage = getResources().getString(R.string.game_over_created_record);
            }
            mLocalStorage.write(key, score + ",0,0");
            return extraMessage;
        }

        int first = Integer.valueOf(parts[0]);
        int second = Integer.valueOf(parts[1]);
        int third = Integer.valueOf(parts[2]);

        if (score != 0) {
            if (score > first) {
                extraMessage = getResources().getString(R.string.game_over_created_record);
                mLocalStorage.write(key, score + "," + first + "," + second);
            } else if (score == first) {
                extraMessage = getResources().getString(R.string.game_over_leveled_record);
            } else if (score > second) {
                extraMessage = getResources().getString(R.string.game_over_second_highest);
                mLocalStorage.write(key, first + "," + score + "," + second);
            } else if (score == second) {
                extraMessage = getResources().getString(R.string.game_over_second_highest);
            } else if (score > third) {
                extraMessage = getResources().getString(R.string.game_over_third_highest);
                mLocalStorage.write(key, first + "," + second + "," + score);
            } else if (score == third) {
                extraMessage = getResources().getString(R.string.game_over_third_highest);
            }
        }

        return extraMessage;
    }

    public void displayGameOverMessage(final long score) {

        mCompositeDisposable.add(
                mLocalStorage.readObservable(getRecordKey(), null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                String extraMessage = handleRecords(s, score);
                                mTetrisView.showGameOverDlg(score, extraMessage);
                            }

                            @Override
                            public void onError(Throwable e) {
                                String extraMessage = handleRecords(null, score);
                                mTetrisView.showGameOverDlg(score, extraMessage);
                            }

                            @Override
                            public void onComplete() {}
                        })
        );
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
        mCompositeDisposable.dispose();
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
