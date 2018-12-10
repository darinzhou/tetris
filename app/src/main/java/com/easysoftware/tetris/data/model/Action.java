package com.easysoftware.tetris.data.model;

public class Action {

    public static final int ACTION_MOVE_LEFT = 0;
    public static final int ACTION_MOVE_RIGHT = 1;
    public static final int ACTION_ROTATE_CLOCKWISE = 2;
    public static final int ACTION_ROTATE_ANTICLOCKWISE = 3;
    public static final int ACTION_FALL_ONE_ROW = 4;
    public static final int ACTION_FALL_UNTIL_LANDED = 5;

    private int mType;
    private int[][] mField;

    public Action(int type, int[][] field) {
        mType = type;
        if (mType < ACTION_MOVE_LEFT || mType > ACTION_FALL_UNTIL_LANDED) {
            mType = -1;
        }
        mField = field;
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

        if (mType == ACTION_FALL_UNTIL_LANDED) {
            //
        }

        Tetrominoe temp = new Tetrominoe(tetrominoe);
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

        int[][] image = temp.getImage();
        boolean actionSucceed = true;
        boolean isLanded = false;
        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    int row = r+temp.getTopLeftRow();
                    int col = c+temp.getTopLeftCol();

                    // check if reached side wall
                    if (col < 0 || col >= mField[0].length) {
                        actionSucceed = false;
                        break;
                    }

                    // check if reached landed blocks
                    if (row >=0 && row < mField.length && mField[row][col] != 0) {
                        actionSucceed = false;
                        break;
                    }

                    // check if below field
                    if (row >= mField.length) {
                        actionSucceed = false;
                        break;
                    }

                    // check if on field bottom
                    if (row == mField.length - 1) {
                        isLanded = true;
                        break;
                    }

                    // check if landed on non empty block
                    if (row + 1 >=0 && mField[row+1][col] != 0) {
                        isLanded = true;
                        break;
                    }

                }

                if (!actionSucceed || isLanded) {
                    break;
                }
            }
        }

        // if succeed, apply the action
        if (actionSucceed) {
            // apply
            temp.setLanded(isLanded);
            tetrominoe.copy(temp);
            image = tetrominoe.getImage();

            // if landed, updated the field
            if (isLanded) {
                for (int r = 0; r < image.length; ++r) {
                    for (int c = 0; c < image[r].length; ++c) {
                        if (image[r][c] != 0) {
                            int row = r+tetrominoe.getTopLeftRow();
                            int col = c+tetrominoe.getTopLeftCol();
                            if (row >=0 && row < mField.length && col >=0 && col < mField[0].length) {
                                mField[row][col] = tetrominoe.getColorIndex();
                            } else {
                                int i=0;
                            }
                        }
                    }
                }
            }
        }

        return actionSucceed;
    }

}
