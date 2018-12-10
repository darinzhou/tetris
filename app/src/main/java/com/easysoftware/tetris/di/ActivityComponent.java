package com.easysoftware.tetris.di;

import com.easysoftware.tetris.ui.MainActivity;
import com.easysoftware.tetris.ui.TetrisView;

import dagger.Subcomponent;

@PerActivity
@Subcomponent (modules={ActivityModule.class})
public interface ActivityComponent {

    void inject(MainActivity activity);

}
