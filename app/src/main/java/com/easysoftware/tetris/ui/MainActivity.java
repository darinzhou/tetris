package com.easysoftware.tetris.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import com.easysoftware.tetris.R;
import com.easysoftware.tetris.app.TetrisApp;
import com.easysoftware.tetris.base.BaseActivity;
import com.easysoftware.tetris.ui.util.SingleChoiceDlgFragment;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    @Inject
    TetrisPresenter mTetrisPresenter;

    private TetrisView mTetrisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // DI injection
        ((TetrisApp) getApplication()).createActivityComponent().inject(this);

        // get screen size
        //Getting display object
        Display display = getWindowManager().getDefaultDisplay();
        //Getting the screen resolution into point object
        Point size = new Point();
        display.getSize(size);

        // set game view
        mTetrisView = findViewById(R.id.tetrisView);//new TetrisView(this, size.x, size.y, mTetrisPresenter);

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
//                        mTetrisPresenter.newTetrominoe();
                    }

                    @Override
                    public void onCancel() {
                        MainActivity.this.finish();
                    }
                });
        dlg.setCancelable(false);
        dlg.show(getSupportFragmentManager(), "level_dlg");

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
