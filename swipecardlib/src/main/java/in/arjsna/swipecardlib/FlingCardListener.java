package in.arjsna.swipecardlib;

import ohos.agp.animation.Animator;
import ohos.agp.components.Component;
import ohos.agp.utils.Rect;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

import static in.arjsna.swipecardlib.Utils.rectContains;

/**
 * FlingCardListener.
 */
public class FlingCardListener implements Component.TouchEventListener {
    //#region parameters
    private static final int INVALID_POINTER_ID = -1;

    private final SwipeCardView parentView;

    private final Rect rectTop;

    private final Rect rectBottom;

    private final Rect rectLeft;

    private final Rect rectRight;

    private final float objectX;

    private final float objectY;

    private final int objectH;

    private final int objectW;

    private final int parentWidth;

    private final FlingListener mFlingListener;

    private final Object dataObject;

    private final float halfWidth;

    private final float halfHeight;

    private final int parentHeight;

    private float baseRotationDegrees;

    private float posX;

    private float posY;

    private float downTouchX;

    private float downTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private Component frame = null;

    private static final int TOUCH_ABOVE = 0;

    private static final int TOUCH_BELOW = 1;

    private int touchPosition;

    private boolean isAnimationRunning = false;

    private static final float MAX_COS = (float) Math.cos(Math.toRadians(45));

    //#endregion parameters

    public FlingCardListener(SwipeCardView parent,
                             Component frame,
                             Object itemAtPosition, FlingListener flingListener) {
        this(parent, frame, itemAtPosition, 15f, flingListener);
        Utils.entry_log();
    }

    /**
     * FlingCardListener.
     *
     * @param parent          parent
     * @param frame           frame
     * @param itemAtPosition  itemAtPosition
     * @param rotationDegrees rotationDegrees
     * @param flingListener   flingListener
     */
    public FlingCardListener(SwipeCardView parent,
                             Component frame,
                             Object itemAtPosition,
                             float rotationDegrees, FlingListener flingListener) {
        super();
        Utils.entry_log();
        this.parentView = parent;
        this.frame = frame;
        this.objectX = frame.getContentPositionX();
        this.objectY = frame.getContentPositionY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.halfHeight = objectH / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((Component) frame.getComponentParent()).getWidth();
        this.parentHeight = ((Component) frame.getComponentParent()).getHeight();
        this.baseRotationDegrees = rotationDegrees;
        this.mFlingListener = flingListener;
        this.rectTop = new Rect(
                (int) Math.max(frame.getLeft(), leftBorder()),
                0,
                (int) Math.min(frame.getRight(), rightBorder()),
                (int) topBorder()
        );
        this.rectBottom = new Rect(
                (int) Math.max(frame.getLeft(), leftBorder()),
                (int) bottomBorder(),
                (int) Math.min(frame.getRight(), rightBorder()),
                parentHeight
        );
        this.rectLeft = new Rect(
                0,
                (int) Math.max(frame.getTop(), topBorder()),
                (int) leftBorder(),
                (int) Math.min(frame.getBottom(), bottomBorder())
        );
        this.rectRight = new Rect(
                (int) rightBorder(),
                (int) Math.max(frame.getTop(), topBorder()),
                parentWidth,
                (int) Math.min(frame.getBottom(), bottomBorder())
        );

    }


    @Override
    public boolean onTouchEvent(Component view, TouchEvent event) {
        switch (event.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN: {
                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getPointerPosition(mActivePointerId).getX();
                    y = event.getPointerPosition(mActivePointerId).getY();
                    success = true;
                } catch (IllegalArgumentException e) {
                    // pass
                }
                if (success) {
                    // Remember where we started
                    downTouchX = x;
                    downTouchY = y;
                    //to prevent an initial jump of the magnifier, aposX and aPosY must
                    //have the values from the magnifier frame
                    if (posX == 0) {
                        posX = 0;
                    }
                    if (posY == 0) {
                        posY = 0;
                    }

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }
                }
                break;
            }

            case TouchEvent.PRIMARY_POINT_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
                break;
            }

            case TouchEvent.OTHER_POINT_DOWN:
                break;

            case TouchEvent.OTHER_POINT_UP: {
                final int pointerIndex = (event.getAction());
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    downTouchX = event.getPointerPosition(newPointerIndex).getX();
                    downTouchY = event.getPointerPosition(newPointerIndex).getY();
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }

            case TouchEvent.POINT_MOVE: {
                final float xMove = event.getPointerPosition(mActivePointerId).getX();
                final float yMove = event.getPointerPosition(mActivePointerId).getY();

                final float dx = xMove - downTouchX;
                final float dy = yMove - downTouchY;
                // BUG: it is only incrementing so it is getting out of bound
                posX += dx;
                posY += dy;

                // calculate the rotation degrees
                float distobjectX = posX - objectX;
                float rotation = baseRotationDegrees * 2.f * distobjectX / parentWidth;
                if (touchPosition == TOUCH_BELOW) {
                    Utils.entry_log();
                    rotation = -rotation;
                }
                frame.setContentPositionX(posX);
                frame.setContentPositionY(posY);
                frame.setRotation(rotation);
                mFlingListener.onScroll(getScrollProgressPercent());
                break;
            }

            case TouchEvent.CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
            default:
                break;
        }

        return true;
    }

    private float getScrollProgressPercent() {
        Utils.entry_log();
        if (movedBeyondLeftBorder()) {
            return -1f;
        } else if (movedBeyondRightBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (posX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private void resetCardViewOnStack() {
        Utils.entry_log();
        if (movedBeyondLeftBorder() && parentView.detectLeftSwipe) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondLeftBorder");
            // Left Swipe
            onSelectedX(true, getExitPoint(-objectW), 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondRightBorder() && parentView.detectRightSwipe) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondRightBorder");
            // Right Swipe
            onSelectedX(false, getExitPoint(parentWidth), 100);
            mFlingListener.onScroll(1.0f);
        } else if (movedBeyondTopBorder() && parentView.detectTopSwipe) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondTopBorder");
            onSelectedY(true, getExitPointX(-objectH), 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondBottomBorder() && parentView.detectBottomSwipe) {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: movedBeyondBottomBorder");
            onSelectedY(false, getExitPointX(parentHeight), 100);
            mFlingListener.onScroll(1.0f);
        } else {
            HiLog.debug(LABEL_LOG, "resetCardViewOnStack: else");
            final float abslMoveDistance = Math.abs(posX - objectX);
            posX = 0;
            posY = 0;
            downTouchX = 0;
            downTouchY = 0;
            frame.createAnimatorProperty()
                    .setDuration(200)
                    .setCurveType(Animator.CurveType.OVERSHOOT)
                    .moveToX(objectX)
                    .moveToY(objectY)
                    .rotate(0)
                    .start();
            mFlingListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0) {
                Utils.entry_log();
                mFlingListener.onClick(dataObject);
            }
        }
    }


    private boolean movedBeyondLeftBorder() {

        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (rectContains(rectLeft, centerX, centerY)
                || (centerX < rectLeft.left
                && rectContains(rectLeft, 0, centerY)));
    }

    private boolean movedBeyondRightBorder() {
        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (rectContains(rectRight, centerX, centerY)
                || (centerX > rectRight.right
                && rectContains(rectRight, rectRight.left, centerY)));
    }

    private boolean movedBeyondBottomBorder() {
        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (rectContains(rectBottom, centerX, centerY)
                || (centerY > rectBottom.bottom
                && rectContains(rectBottom, centerX, rectBottom.top)));
    }


    private boolean movedBeyondTopBorder() {
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (rectContains(rectTop, centerX, centerY)
                || (centerY < rectTop.top && rectContains(rectTop, centerX, 0)));
    }

    private float leftBorder() {
        Utils.entry_log();
        return parentWidth / 4.f;
    }

    private float rightBorder() {
        Utils.entry_log();
        return 3 * parentWidth / 4.f;
    }

    private float bottomBorder() {
        Utils.entry_log();
        return 3 * parentHeight / 4.f;
    }

    private float topBorder() {
        Utils.entry_log();
        return parentHeight / 4.f;
    }

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    private void onSelectedY(final boolean isTop, float exitX, int duration) {
        Utils.entry_log();
        isAnimationRunning = true;
        float exitY;
        if (isTop) {
            Utils.entry_log();
            exitY = -objectH - getRotationWidthOffset();
        } else {
            exitY = parentHeight + getRotationWidthOffset();
        }

        this.frame.createAnimatorProperty().setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveToX(exitX)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region not used

                    @Override
                    public void onStart(Animator animator) {

                    }

                    @Override
                    public void onStop(Animator animator) {

                    }

                    @Override
                    public void onCancel(Animator animator) {

                    }

                    @Override
                    public void onPause(Animator animator) {

                    }

                    @Override
                    public void onResume(Animator animator) {

                    }
                    //#endregion not used

                    @Override
                    public void onEnd(Animator animator) {
                        if (isTop) {
                            Utils.entry_log();
                            mFlingListener.onCardExited();
                            mFlingListener.topExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.bottomExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).rotate(getVerticalExitRotation(isTop)).start();

    }

    private void onSelectedX(final boolean isLeft,
                             float exitY, long duration) {
        Utils.entry_log();
        isAnimationRunning = true;
        float exitX;
        if (isLeft) {
            Utils.entry_log();
            exitX = -objectW - getRotationWidthOffset();
        } else {
            exitX = parentWidth + getRotationWidthOffset();
        }
        this.frame.createAnimatorProperty().setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveFromY(0)
                .moveFromY(0)
                .moveToX(exitX)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region not used
                    @Override
                    public void onStart(Animator animator) {

                    }

                    @Override
                    public void onStop(Animator animator) {

                    }

                    @Override
                    public void onCancel(Animator animator) {

                    }

                    @Override
                    public void onPause(Animator animator) {

                    }

                    @Override
                    public void onResume(Animator animator) {

                    }
                    //#endregion not used

                    @Override
                    public void onEnd(Animator animator) {
                        Utils.entry_log();
                        if (isLeft) {
                            Utils.entry_log();
                            mFlingListener.onCardExited();
                            mFlingListener.leftExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.rightExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).rotate(getHorizontalExitRotation(isLeft)).start();
    }

    void selectLeft() {
        Utils.entry_log();
        if (!isAnimationRunning) {
            onSelectedX(true, objectY, 200);
        }
    }

    void selectRight() {
        Utils.entry_log();
        if (!isAnimationRunning) {
            onSelectedX(false, objectY, 200);
        }
    }

    void selectTop() {
        Utils.entry_log();
        if (!isAnimationRunning) {
            onSelectedY(true, objectX, 200);
        }
    }

    void selectBottom() {
        Utils.entry_log();
        if (!isAnimationRunning) {
            onSelectedY(false, objectX, 200);
        }
    }

    private float getExitPoint(int exitPointX) {
        Utils.entry_log();
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = posX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = posY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitPointX + (float) regression.intercept();
    }

    private float getExitPointX(int exitPointY) {
        Utils.entry_log();
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = posX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = posY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical x = (y - b) / a linear regression
        return (float) ((exitPointY - (float) regression.intercept()) / regression.slope());
    }

    private float getHorizontalExitRotation(boolean isLeft) {
        Utils.entry_log();
        float rotation = baseRotationDegrees * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }

    private float getVerticalExitRotation(boolean isTop) {
        Utils.entry_log();
        float rotation = baseRotationDegrees * 2.f * (parentHeight - objectY) / parentHeight;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isTop) {
            rotation = -rotation;
        }
        return rotation;
    }

    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        Utils.entry_log();
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        Utils.entry_log();
        this.baseRotationDegrees = degrees;
    }

    boolean isTouching() {
        Utils.entry_log();
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    PointF getLastPoint() {
        Utils.entry_log();
        return new PointF(this.posX, this.posY);
    }

    interface FlingListener {
        void onCardExited();

        void leftExit(Object dataObject);

        void rightExit(Object dataObject);

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);

        void topExit(Object dataObject);

        void bottomExit(Object dataObject);
    }

}





