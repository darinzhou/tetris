package com.easysoftware.tetris.model;

public class Action {
    public static final int SCORE_FOR_CLEAR_ONE_ROW = 100;

    public static final int ACTION_MOVE_LEFT = 0;
    public static final int ACTION_MOVE_RIGHT = 1;
    public static final int ACTION_ROTATE_CLOCKWISE = 2;
    public static final int ACTION_ROTATE_ANTICLOCKWISE = 3;
    public static final int ACTION_FALL_ONE_ROW = 4;
    public static final int ACTION_FALL_UNTIL_LANDED = 5;

    private int mType;
    private int mFieldRowCount;
    private int mFieldColCount;
    private int[][] mField;
    private long mScore;
    private int mClearedRowCount;

    public Action(int type, int[][] field) {
        mType = type;
        if (mType < ACTION_MOVE_LEFT || mType > ACTION_FALL_UNTIL_LANDED) {
            mType = -1;
        }
        mField = field;
        mFieldRowCount = mField.length;
        mFieldColCount = mField[0].length;
    }

    public int getType() {
        return mType;
    }

    public boolean isValid() {
        return mType != -1;
    }

    public int[][] getField() {
        return mField;
    }

    public boolean applyTo(Tetrominoe tetrominoe) {
        if (!isValid()) {
            return false;
        }

        Tetrominoe temp = new Tetrominoe(tetrominoe);
        int[][] image = temp.getImage();
        boolean isLanded = false;
        boolean actionSucceed = true;

        if (mType == ACTION_FALL_UNTIL_LANDED) {
            // fall til landed
            while (!isLanded) {
                // fall one row
                temp.fallOne();
                // check if landed
                for (int r = 0; r < image.length; ++r) {
                    for (int c = 0; c < image[r].length; ++c) {
                        if (image[r][c] != 0) {
                            int row = r + temp.getTopLeftRow();
                            int col = c + temp.getTopLeftCol();

                            // check if on field bottom
                            if (row == mFieldRowCount - 1) {
                                isLanded = true;
                                break;
                            }

                            // check if landed on non empty block
                            if (row + 1 >= 0 && mField[row + 1][col] != 0) {
                                isLanded = true;
                                break;
                            }
                        }

                        if (isLanded) {
                            break;
                        }
                    }
                }
            }

        } else {

            switch (mType) {
                case ACTION_MOVE_LEFT:
                    temp.moveLeft();
                    break;
                case ACTION_MOVE_RIGHT:
                    temp.moveRight();
                    break;
                case ACTION_ROTATE_CLOCKWISE:
                    temp.rotateClockwise();
                    break;
                case ACTION_ROTATE_ANTICLOCKWISE:
                    temp.rotateAntiClockwise();
                    break;
                case ACTION_FALL_ONE_ROW:
                default:
                    temp.fallOne();
                    break;
            }

            for (int r = 0; r < image.length; ++r) {
                for (int c = 0; c < image[r].length; ++c) {
                    if (image[r][c] != 0) {
                        int row = r + temp.getTopLeftRow();
                        int col = c + temp.getTopLeftCol();

                        // check if reached side wall
                        if (col < 0 || col >= mFieldColCount) {
                            actionSucceed = false;
                            break;
                        }

                        // check if reached landed blocks
                        if (row >= 0 && row < mFieldRowCount && mField[row][col] != 0) {
                            actionSucceed = false;
                            break;
                        }

                        // check if below field
                        if (row >= mFieldRowCount) {
                            actionSucceed = false;
                            break;
                        }

                        // check if on field bottom
                        if (row == mFieldRowCount - 1) {
                            isLanded = true;
                            break;
                        }

                        // check if landed on non empty block
                        if (row + 1 >= 0 && mField[row + 1][col] != 0) {
                            isLanded = true;
                            break;
                        }

                    }

                    if (!actionSucceed || isLanded) {
                        break;
                    }
                }
            }
        }

        // if succeed, apply the action
        if (actionSucceed) {
            // apply
            temp.setLanded(isLanded);
            tetrominoe.copy(temp);

            // if landed, updated the field
            if (isLanded) {
                landTetrominoe(tetrominoe);
                clearFilledRows();
                mScore = mClearedRowCount * SCORE_FOR_CLEAR_ONE_ROW;
            }
        }

        return actionSucceed;
    }

    private void landTetrominoe(Tetrominoe tetrominoe) {
        int[][] image = tetrominoe.getImage();

        // update field
        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    int row = r + tetrominoe.getTopLeftRow();
                    int col = c + tetrominoe.getTopLeftCol();
                    if (row >= 0 && row < mFieldRowCount && col >= 0 && col < mFieldColCount) {
                        mField[row][col] = tetrominoe.getColorId();
                    }
                }
            }
        }
    }

    private void clearFilledRows() {
        for (int r = mFieldRowCount - 1; r >= 0; r--) {
            int emptyBlocks = 0;
            for (int c = 0; c < mFieldColCount; c++) {
                if (mField[r][c] == 0) {
                    emptyBlocks++;
                }
            }

            // if emptyBlocks is 0 then current row is filled
            if (emptyBlocks == 0) {
                // clear the filled row and update the field
                clearFilledRow(r);
                mClearedRowCount++;
            }

            // if emptyBlocks is mFieldColCount then stop check
            // as there should be no empty row between remain blocks
            if (emptyBlocks == mFieldColCount) {
                break;
            }
        }
    }

    private void clearFilledRow(int row) {
        // remove row (row)
        // shift down one row from row (row -1) to row 0
        for (int r = row - 1; r >= 0; r--) {
            for (int c = 0; c < mFieldColCount; c++) {
                mField[r + 1][c] = mField[r][c];
            }
        }

        // set row 0 as empty
        for (int c = 0; c < mFieldColCount; c++) {
            mField[0][c] = 0;
        }
    }

    public long getScore() {
        return mScore;
    }

    public int getClearedRowCount() {
        return mClearedRowCount;
    }

    // test
    public void test_clearFilledRows() {
        clearFilledRows();
    }
}
