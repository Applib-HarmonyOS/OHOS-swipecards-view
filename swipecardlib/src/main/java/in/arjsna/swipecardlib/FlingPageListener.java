package in.arjsna.swipecardlib;

import static in.arjsna.swipecardlib.Utils.rectContains;
import ohos.agp.animation.Animator;
import ohos.agp.components.Component;
import ohos.agp.utils.Rect;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;

/**
 * Created by arjun on 4/25/16.
 */
public class FlingPageListener implements Component.TouchEventListener {
    private static final String TAG = "FlingPageListener";
    private static final int INVALID_POINTER_ID = -1;
    private final Rect rectBottom;
    private final Rect rectTop;
    private final Rect rectRight;
    private final Rect rectLeft;
    private final Component frame;
    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final float halfWidth;
    private final float halfHeight;
    private final Object dataObject;
    private final int parentWidth;
    private final int parentHeight;
    private final FlingListener mFlingListener;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float downTouchX;
    private float downTouchY;
    private float posX;
    private float posY;
    private boolean isAnimationRunning = false;

    /**
     * FlingPageListener.
     *
     * @param frame          frame
     * @param itemAtPosition itemAtPosition
     * @param flingListener  flingListener
     */
    public FlingPageListener(Component frame, Object itemAtPosition, FlingListener flingListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getContentPositionX();
        this.objectY = frame.getContentPositionX();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.halfHeight = objectH / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((Component) frame.getComponentParent()).getWidth();
        this.parentHeight = ((Component) frame.getComponentParent()).getHeight();
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

    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }

    public float bottomBorder() {
        return 3 * parentHeight / 4.f;
    }

    public float topBorder() {
        return parentHeight / 4.f;
    }


    @Override
    public boolean onTouchEvent(Component view, TouchEvent event) {
        Utils.entry_log();

        switch (event.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN:
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
                    // to prevent an initial jump of the magnifier, aposX and aPosY must
                    // have the values from the magnifier frame
                    if (posX == 0) {
                        posX = frame.getContentPositionX();
                    }
                    if (posY == 0) {
                        posY = frame.getContentPositionY();
                    }

                    // if (y < objectH / 2) {
                    //     touchPosition = TOUCH_ABOVE;
                    // } else {
                    //     touchPosition = TOUCH_BELOW;
                    // }
                }
                break;

            case TouchEvent.PRIMARY_POINT_UP: //other
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
                break;

            case TouchEvent.OTHER_POINT_UP:
                final int pointerIndex = event.getAction();
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    downTouchX = event.getPointerPosition(newPointerIndex).getX();
                    downTouchY = event.getPointerPosition(newPointerIndex).getY();
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            case TouchEvent.POINT_MOVE:
                final float xMove = event.getPointerPosition(mActivePointerId).getX();
                final float yMove = event.getPointerPosition(mActivePointerId).getY();
                final float dx = xMove - downTouchX;
                final float dy = yMove - downTouchY;

                posX += dx;
                posY += dy;
                frame.setContentPositionY(posY);
                mFlingListener.onScroll(getScrollProgressPercent());
                break;

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
        if (movedBeyondTopBorder()) {
            return -1f;
        } else if (movedBeyondBottomBorder()) {
            return 1f;
        } else {
            float zeroToOneValue = (posY + halfHeight - topBorder()) / (bottomBorder() - topBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    private void resetCardViewOnStack() {
        Utils.entry_log();
        if (movedBeyondTopBorder()) {
            onSelectedY(true, 100);
            mFlingListener.onScroll(-1.0f);
        } else if (movedBeyondBottomBorder()) {
            onSelectedY(false, 100);
            mFlingListener.onScroll(1.0f);
        } else {
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
                mFlingListener.onClick(dataObject);
            }
        }
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
        Utils.entry_log();
        int centerX = (int) (frame.getContentPositionX() + halfWidth);
        int centerY = (int) (frame.getContentPositionY() + halfHeight);
        return (rectContains(rectTop, centerX, centerY)
                || (centerY < rectTop.top
                && rectContains(rectTop, centerX, 0)));
    }

    private void onSelectedY(final boolean isTop, int duration) {
        Utils.entry_log();
        isAnimationRunning = true;
        float exitY;
        if (isTop) {
            exitY = -objectH;
        } else {
            exitY = parentHeight;
        }

        this.frame.createAnimatorProperty()
                .setDuration(duration)
                .setCurveType(Animator.CurveType.ACCELERATE)
                .moveToY(exitY)
                .setStateChangedListener(new Animator.StateChangedListener() {
                    //#region unused
                    @Override
                    public void onStart(Animator animator) {
                        Utils.entry_log();
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
                    //#endregion unused

                    @Override
                    public void onEnd(Animator animator) {
                        Utils.entry_log();
                        if (isTop) {
                            mFlingListener.onCardExited();
                            mFlingListener.topExit(dataObject);
                        } else {
                            mFlingListener.onCardExited();
                            mFlingListener.bottomExit(dataObject);
                        }
                        isAnimationRunning = false;
                    }

                }).start();
    }

    private float getExitPointX(int exitPointY) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = posX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = posY;

        LinearRegression regression = new LinearRegression(y, x);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitPointY + (float) regression.intercept();
    }

    public boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint() {
        return new PointF(this.posX, this.posY);
    }

    /**
     * FlingListener.
     */
    protected interface FlingListener {
        void onCardExited();

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);

        void topExit(Object dataObject);

        void bottomExit(Object dataObject);
    }
}
