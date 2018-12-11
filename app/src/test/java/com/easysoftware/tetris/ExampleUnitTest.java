package com.easysoftware.tetris;

import com.easysoftware.tetris.model.Action;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_clearLine() {
        int[][] field = new int[][] {
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
                {0,0,0,0,0,0,0,0,1,0},
                {1,1,1,1,1,1,1,1,1,1},
                {0,0,0,0,0,0,0,0,1,1},
                {1,1,1,1,1,1,1,1,1,1},
                {0,0,1,0,0,0,0,0,0,1},
                {0,1,0,0,0,0,0,0,1,0}
        };

        Action action = new Action(Action.ACTION_FALL_ONE_ROW, field);
        action.test_clearFilledRows();
        int a = 0;
    }
}