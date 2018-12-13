package com.easysoftware.tetris.model;

import java.util.Random;

public class Tetrominoe {
    public static final int SHAPE_COUNT = 7;
    public static final int COLOR_COUNT = 6;
    public static final int ROTATION_COUNT = 4;

    public static final int[][][] I = {
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
            },
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
        for (int c = colCount - 1; c >= 0; --c) {
            boolean empty = true;
            for (int r = 0; r < rowCount; ++r) {
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


    private int mFieldColCount;
    private int mShape;
    private int mTopLeftRow;
    private int mRotation;
    private int mColorId;
    private int mTopLeftCol;
    private boolean mIsLanded;

    public Tetrominoe(int fieldColCount) {

        mFieldColCount = fieldColCount;

        Random rnd = new Random();

        mShape = rnd.nextInt(SHAPE_COUNT);
        mColorId = rnd.nextInt(COLOR_COUNT) + 1;
        mRotation = 0;

        int[][] image = getImage(mShape, mRotation);
        int rowCount = image.length;
        int colCount = image[0].length;
        int min = getEmptyColOnLeft(image);
        int max = fieldColCount - colCount + getEmptyColOnRight(image) + 1;

        mTopLeftRow = -rowCount;
        mTopLeftCol = rnd.nextInt(max - min) + min;

        mIsLanded = false;
    }

    public Tetrominoe(Tetrominoe other) {
        copy(other);
    }

    public void copy(Tetrominoe other) {
        mFieldColCount = other.mFieldColCount;
        mShape = other.mShape;
        mColorId = other.mColorId;
        mRotation = other.mRotation;
        mTopLeftCol = other.mTopLeftCol;
        mTopLeftRow = other.mTopLeftRow;
        mIsLanded = other.mIsLanded;
    }

    public boolean applyAction(Action action) {
        if (action == null) {
            return false;
        }
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

    public int getColorId() {
        return mColorId;
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
        adjustRotationPosition();
    }

    public void rotateAntiClockwise() {
        --mRotation;
        if (mRotation < 0) {
            mRotation = ROTATION_COUNT - 1;
        }
        adjustRotationPosition();
    }

    public void fallOne() {
        mTopLeftRow++;
    }

    private void adjustRotationPosition() {
        int[][] image = getImage();
        int minCol = mFieldColCount;
        int maxCol = -1;
        for (int r = 0; r < image.length; ++r) {
            for (int c = 0; c < image[r].length; ++c) {
                if (image[r][c] != 0) {
                    int col = c + getTopLeftCol();

                    if (col < minCol) {
                        minCol = col;
                    }
                    if (col > maxCol) {
                        maxCol = col;
                    }
                }
            }
        }

        if (minCol < 0) {
            mTopLeftCol -= minCol;
        }
        if (maxCol >= mFieldColCount) {
            mTopLeftCol -= maxCol - mFieldColCount + 1;
        }
    }

}
