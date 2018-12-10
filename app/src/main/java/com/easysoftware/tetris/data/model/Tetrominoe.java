package com.easysoftware.tetris.data.model;

import android.graphics.Color;

import java.util.Random;

import static com.easysoftware.tetris.ui.TetrisPresenter.COL_COUNT;

public class Tetrominoe {
    public static final int SHAPE_COUNT = 7;
    public static final int COLOR_COUNT = 6;
    public static final int ROTATION_COUNT = 4;

    public static final int[][][] I = {
            {
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
            },
            {
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
                    {0, 0, 1, 0},
            },
            {
                    {0, 0, 0, 0},
                    {0, 0, 0, 0},
                    {1, 1, 1, 1},
                    {0, 0, 0, 0},
            },
            {
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
                    {0, 1, 0, 0},
            }
    };

    public static final int[][][] J = {
            {
                    {1, 0, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 1},
                    {0, 1, 0},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 0, 1}
            },
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {1, 1, 0}
            }
    };

    public static final int[][][] L = {
            {
                    {0, 0, 1},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 0},
                    {0, 1, 1}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {1, 0, 0}
            },
            {
                    {1, 1, 0},
                    {0, 1, 0},
                    {0, 1, 0}
            }
    };

    public static final int[][][] O = {
            {
                    {0, 0, 0, 0},
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0},
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0},
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {0, 0, 0, 0}
            },
            {
                    {0, 0, 0, 0},
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {0, 0, 0, 0}
            }
    };

    public static final int[][][] S = {
            {
                    {0, 1, 1},
                    {1, 1, 0},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 0, 1}
            },
            {
                    {0, 0, 0},
                    {0, 1, 1},
                    {1, 1, 0}
            },
            {
                    {1, 0, 0},
                    {1, 1, 0},
                    {0, 1, 0}
            }
    };

    public static final int[][][] T = {
            {
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 1, 0},
                    {0, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 1, 0},
                    {1, 1, 0},
                    {0, 1, 0}
            }
    };

    public static final int[][][] Z = {
            {
                    {1, 1, 0},
                    {0, 1, 1},
                    {0, 0, 0}
            },
            {
                    {0, 0, 1},
                    {0, 1, 1},
                    {0, 1, 0}
            },
            {
                    {0, 0, 0},
                    {1, 1, 0},
                    {0, 1, 1}
            },
            {
                    {0, 1, 0},
                    {1, 1, 0},
                    {1, 0, 0}
            }
    };

    private static int[][] getImage(int shape, int rotation) {
        switch (shape) {
            case 0:
                return I[rotation];
            case 1:
                return J[rotation];
            case 2:
                return L[rotation];
            case 3:
                return O[rotation];
            case 4:
                return S[rotation];
            case 5:
                return T[rotation];
            case 6:
            default:
                return Z[rotation];
        }
    }

    public static int getColor(int colorIndex) {
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

    private static int getEmptyColOnLeft(int[][] image) {
        int rowCount = image.length;
        int colCount = image[0].length;

        int emptyColNum = 0;
        for (int c = 0; c < colCount; ++c) {
            boolean empty = true;
            for (int r = 0; r < rowCount; ++r) {
                if (image[r][c] != 0) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                emptyColNum = c;
                break;
            }
        }

        return emptyColNum;
    }

    private static int getEmptyColOnRight(int[][] image) {
        int rowCount = image.length;
        int colCount = image[0].length;

        int emptyColNum = 0;
        for (int c = colCount-1; c >=0; --c) {
            boolean empty = true;
            for (int r = 0; r<rowCount; ++r) {
                if (image[r][c] != 0) {
                    empty = false;
                    break;
                }
            }
            if (!empty) {
                break;
            }

            emptyColNum++;
        }

        return emptyColNum;
    }


    private int mShape;
    private int mTopLeftRow;
    private int mRotation;
    private int mColorIndex;
    private int mTopLeftCol;
    private boolean mIsLanded;

    public Tetrominoe() {
        Random rnd = new Random();

        mShape = rnd.nextInt(SHAPE_COUNT);
        mColorIndex = rnd.nextInt(COLOR_COUNT) + 1;
        mRotation = rnd.nextInt(ROTATION_COUNT);

        int[][] image = getImage(mShape, mRotation);
        int rowCount = image.length;
        int colCount = image[0].length;
        int min = getEmptyColOnLeft(image);
        int max = COL_COUNT - colCount + getEmptyColOnRight(image) + 1;

        mTopLeftRow = -rowCount;
        mTopLeftCol = rnd.nextInt(max-min) + min;

        mIsLanded = false;
    }

    public Tetrominoe(Tetrominoe other) {
        copy(other);
    }

    public void copy(Tetrominoe other) {
        mShape = other.mShape;
        mColorIndex = other.mColorIndex;
        mRotation = other.mRotation;;
        mTopLeftCol = other.mTopLeftCol;
        mTopLeftRow = other.mTopLeftRow;
        mIsLanded = other.mIsLanded;
    }

    public boolean applyAction(Action action) {
        return action.applyTo(this);
    }

    public boolean isLanded() {
        return mIsLanded;
    }

    public void setLanded(boolean isLanded) {
        mIsLanded = isLanded;
    }

    public int[][] getImage() {
        return getImage(mShape, mRotation);
    }

    public int getColorIndex() {
        return mColorIndex;
    }

    public int getColor() {
        return getColor(mColorIndex);
    }

    public int getTopLeftRow() {
        return mTopLeftRow;
    }

    public int getTopLeftCol() {
        return mTopLeftCol;
    }

    public void moveLeft() {
        --mTopLeftCol;
    }
    public void moveRight() {
        ++mTopLeftCol;
    }
    public void rotateClockwise() {
        ++mRotation;
        if (mRotation >= ROTATION_COUNT) {
            mRotation = 0;
        }
    }
    public void rotateAntiClockwise() {
        --mRotation;
        if (mRotation < 0) {
            mRotation = ROTATION_COUNT - 1;
        }
    }
    public void fallOne() {
        mTopLeftRow++;
    }


}
