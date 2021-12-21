package in.arjsna.swipecardlib;


import ohos.agp.components.AttrSet;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.StackLayout;
import ohos.agp.database.DataSetSubscriber;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * SwipeCardView.
 */
public class SwipeCardView extends BaseFlingAdapterView implements ComponentContainer.ArrangeListener {

    //#region properties
    private static final double SCALE_OFFSET = 0.04;

    private static final float TRANS_OFFSET = 45;

    protected boolean detectBottomSwipe;

    protected boolean detectTopSwipe;

    protected boolean detectRightSwipe;

    protected boolean detectLeftSwipe;

    private float currentTransyVal = 0;

    private float currentScaleVal = 0;

    private int initialMaxVisible = 3;

    private int maxVisible = 3;

    private int minAdapterStack = 6;

    private float rotationDegrees = 15.f;

    private int currentAdapterCount = 0;

    private BaseItemProvider mAdapter;

    private int lastObjectInStack = 0;

    private OnCardFlingListener flingListener;

    private AdapterDataSetObserver dataSetObserver;

    private boolean inLayout = false;

    private Component activeCard = null;

    private OnItemClickListener onItemClickListener;

    private FlingCardListener flingCardListener;

    private PointF lastTouchPoint;

    private int startStackFrom = 0;

    private int adapterCount = 0;

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    //#endregion properties

    /**
     * SwipeCardViewAttrs.
     */
    public static final class SwipeCardViewAttrs {
        static final String ROTATION_DEGREES = "rotation_degrees";
        static final String MIN_ADAPTER_STACK = "min_adapter_stack";
        static final String MAX_VISIBLE = "max_visible";
        static final String LEFT_SWIPE_DETECT = "left_swipe_detect";
        static final String RIGHT_SWIPE_DETECT = "right_swipe_detect";
        static final String TOP_SWIPE_DETECT = "top_swipe_detect";
        static final String BOTTOM_SWIPE_DETECT = "bottom_swipe_detect";
    }

    public SwipeCardView(Context context) {
        super(context);
        initConst(null);
    }

    /**
     * SwipeCardView.
     *
     * @param context context
     * @param attrSet attrSet
     */
    public SwipeCardView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        initConst(attrSet);
        Utils.entry_log();
    }

    /**
     * SwipeCardView.
     *
     * @param context   context
     * @param attrSet   attrSet
     * @param styleName styleName
     */
    public SwipeCardView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        initConst(attrSet);
        Utils.entry_log();
    }

    /**
     * initConst.
     *
     * @param attrSet attrSet
     */
    public void initConst(AttrSet attrSet) {
        AttrUtils attrUtils = new AttrUtils(attrSet);
        maxVisible = attrUtils.getIntFromAttr(SwipeCardViewAttrs.MAX_VISIBLE, maxVisible);
        minAdapterStack = attrUtils.getIntFromAttr(SwipeCardViewAttrs.MIN_ADAPTER_STACK, minAdapterStack);
        rotationDegrees = attrUtils.getFloatFromAttr(SwipeCardViewAttrs.ROTATION_DEGREES, rotationDegrees);
        detectLeftSwipe = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.LEFT_SWIPE_DETECT, true);
        detectRightSwipe = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.RIGHT_SWIPE_DETECT, true);
        detectBottomSwipe = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.BOTTOM_SWIPE_DETECT, true);
        detectTopSwipe = attrUtils.getBooleanFromAttr(SwipeCardViewAttrs.TOP_SWIPE_DETECT, true);
        initialMaxVisible = maxVisible;
        setLayoutRefreshedListener(this);
        setArrangeListener(this);
        Utils.entry_log();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context The activity context which extends OnCardFlingListener, OnItemClickListener or both
     * @param adapter The adapter you have to set.
     */
    public void init(final Context context, BaseItemProvider adapter) {
        Utils.entry_log();
        Utils.entry_log();
        if (context instanceof OnCardFlingListener) {
            Utils.entry_log();
            flingListener = (OnCardFlingListener) context;
        } else {
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.OnCardFlingListener");
        }
        if (context instanceof OnItemClickListener) {
            Utils.entry_log();
            onItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(adapter);
    }


    public Component getSelectedView() {
        Utils.entry_log();
        return activeCard;
    }

    public int getCurrentPosition() {
        Utils.entry_log();
        return startStackFrom;
    }

    public Object getCurrentItem() {
        Utils.entry_log();
        return mAdapter.getItem(startStackFrom);
    }

    /**
     * requestLayout.
     */
    public void requestLayout() {
        Utils.entry_log("mInLayout " + inLayout);
        if (!inLayout) {
            Utils.entry_log();
            postLayout();
        }
    }

    @Override
    public boolean onArrange(int left,
                             int top,
                             int width,
                             int height) {
        return false;
    }


    @Override
    public void onRefreshed(Component component) {
        try {
            Utils.entry_log();
            // if we don't have an adapter, we don't need to do anything
            if (mAdapter == null) {
                Utils.entry_log();
                return;
            }
            inLayout = true;

            if (adapterCount == 0) {
                Utils.entry_log();

                removeAllComponents();
            } else {
                Component topCard = getComponentAt(lastObjectInStack);
                if (activeCard != null && topCard != null && topCard == activeCard) {
                    Utils.entry_log();
                    if (this.flingCardListener.isTouching()) {
                        Utils.entry_log();
                        PointF lastPoint = this.flingCardListener.getLastPoint();
                        if (this.lastTouchPoint == null || !this.lastTouchPoint.equals(lastPoint)) {
                            Utils.entry_log();
                            this.lastTouchPoint = lastPoint;
                            for (int i = 0; i < lastObjectInStack; i++) {
                                removeComponentAt(i);
                            }
                            Utils.entry_log("here");
                            layoutChildren(1, adapterCount);
                        }
                    }
                } else {
                    // Reset the UI and set top view listener
                    removeAllComponents();
                    layoutChildren(startStackFrom, adapterCount);
                    setTopView();
                }
            }

            inLayout = false;

            if (currentAdapterCount <= minAdapterStack) {
                flingListener.onAdapterAboutToEmpty(currentAdapterCount);
            }
        } catch (Exception ex) {
            HiLog.debug(LABEL_LOG, "Exception" + ex);
            for (StackTraceElement st : ex.getStackTrace()) {
                HiLog.debug(LABEL_LOG, "" + st);

            }
        }

    }


    private void layoutChildren(int startingIndex, int adapterCount) {
        Utils.entry_log();
        resetOffsets();
        if (adapterCount - startingIndex < maxVisible) {
            Utils.entry_log();
            maxVisible = adapterCount - startingIndex;
        }
        int viewStack = 0;
        while (startingIndex < startStackFrom + maxVisible && startingIndex < adapterCount) {
            Utils.entry_log();
            Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
            if (newUnderChild.getVisibility() != HIDE) {
                Utils.entry_log();
                makeAndAddView(newUnderChild, false);
            }
            startingIndex++;
            viewStack++;
        }

        //
        //
        // this.
        if (startingIndex >= adapterCount) {
            Utils.entry_log();
            lastObjectInStack = --viewStack;
            return;
        }
        Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
        if (newUnderChild != null && newUnderChild.getVisibility() != HIDE) {
            Utils.entry_log();
            makeAndAddView(newUnderChild, true);
            lastObjectInStack = viewStack;
        }
    }

    private void resetOffsets() {
        Utils.entry_log();
        currentTransyVal = 0;
        currentScaleVal = 0;
    }

    int maxWidth = -1;
    int maxHeight = -1;

    private void makeAndAddView(Component child, boolean isBase) {

        final LayoutConfig lp = child.getLayoutConfig();

        Utils.entry_log();
        if (isBase) {
            Utils.entry_log();
            child.setScaleX((float) (child.getScaleX() - (currentScaleVal - SCALE_OFFSET)));
            child.setScaleY((float) (child.getScaleY() - (currentScaleVal - SCALE_OFFSET)));
            child.setTranslationY(child.getTranslationY() + currentTransyVal - TRANS_OFFSET);
        } else {
            child.setScaleX(child.getScaleX() - currentScaleVal);
            child.setScaleY(child.getScaleY() - currentScaleVal);
            child.setTranslationY(child.getTranslationY() + currentTransyVal);
        }

        currentScaleVal += SCALE_OFFSET;
        currentTransyVal += TRANS_OFFSET;

        addComponent(child, 0);

        int childWidthSpec = EstimateSpec.getChildSizeWithMode(lp.width, getWidthMeasureSpec(), EstimateSpec.PRECISE);
        int childHeightSpec = EstimateSpec.getChildSizeWithMode(lp.height, getEstimatedHeight(), EstimateSpec.PRECISE);

        child.estimateSize(childWidthSpec, childHeightSpec);

        int w = child.getEstimatedWidth();
        int h = child.getEstimatedHeight();
        int childLeft = getPaddingLeft() + lp.getMarginLeft();
        int childTop = getPaddingTop() + lp.getMarginTop();

        child.arrange(childLeft, childTop, childLeft + w, childTop + h);

        maxWidth = Math.max(maxWidth, child.getWidth());
        maxHeight = Math.max(maxHeight, child.getHeight());

    }

    /**
     * reLayoutChild.
     *
     * @param child      child
     * @param scrollDis  scrollDis
     * @param childcount childcount
     */
    public void reLayoutChild(Component child, float scrollDis, int childcount) {
        Utils.entry_log();
        float absScrollDis = scrollDis > 1 ? 1 : scrollDis;
        float newScale = (float) (1 - SCALE_OFFSET * (maxVisible - childcount) + absScrollDis * SCALE_OFFSET);
        child.setScaleX(newScale);
        child.setScaleY(newScale);
        child.setTranslationY(TRANS_OFFSET * (maxVisible - childcount) - absScrollDis * TRANS_OFFSET);
    }

    /**
     * Set the top view and add the fling listener.
     */
    private void setTopView() {
        Utils.entry_log();
        if (getChildCount() > 0) {
            Utils.entry_log();
            activeCard = getComponentAt(lastObjectInStack);
            if (activeCard != null) {
                Utils.entry_log();
                flingCardListener = new FlingCardListener(
                        this,
                        activeCard,
                        mAdapter.getItem(startStackFrom),
                        rotationDegrees,
                        new FlingCardListener.FlingListener() {
                            @Override
                            public void onCardExited() {
                                Utils.entry_log();
                                activeCard = null;
                                startStackFrom++;
                                currentAdapterCount--;
                                requestLayout();
                            }

                            @Override
                            public void leftExit(Object dataObject) {
                                Utils.entry_log();
                                flingListener.onCardExitLeft(dataObject);
                            }

                            @Override
                            public void rightExit(Object dataObject) {
                                Utils.entry_log();
                                flingListener.onCardExitRight(dataObject);
                            }

                            @Override
                            public void onClick(Object dataObject) {
                                Utils.entry_log();
                                if (onItemClickListener != null) {
                                    onItemClickListener.onItemClicked(0, dataObject);
                                }

                            }

                            @Override
                            public void onScroll(float scrollProgressPercent) {
                                Utils.entry_log();
                                flingListener.onScroll(scrollProgressPercent);
                                int childCount = getChildCount() - 1;
                                if (childCount < maxVisible) {
                                    Utils.entry_log();
                                    while (childCount > 0) {
                                        Utils.entry_log();
                                        reLayoutChild(
                                                getComponentAt(childCount - 1),
                                                Math.abs(scrollProgressPercent),
                                                childCount
                                        );
                                        childCount--;
                                    }
                                } else {
                                    while (childCount > 1) {
                                        Utils.entry_log();
                                        reLayoutChild(
                                                getComponentAt(childCount - 1),
                                                Math.abs(scrollProgressPercent),
                                                childCount - 1
                                        );
                                        childCount--;
                                    }
                                }
                            }

                            @Override
                            public void topExit(Object dataObject) {
                                Utils.entry_log();
                                flingListener.onCardExitTop(dataObject);
                            }

                            @Override
                            public void bottomExit(Object dataObject) {
                                Utils.entry_log();
                                flingListener.onCardExitBottom(dataObject);
                            }
                        });

                activeCard.setTouchEventListener(flingCardListener);
            }
        }
    }

    /**
     * restart.
     */
    public void restart() {
        Utils.entry_log();
        currentAdapterCount = mAdapter.getCount();
        adapterCount = currentAdapterCount;
        startStackFrom = 0;
        lastObjectInStack = 0;
        maxVisible = initialMaxVisible;
        layoutChildren(0, currentAdapterCount);
        requestLayout();
    }

    /**
     * getTopCardListener.
     *
     * @return FlingCardListener
     * @throws NullPointerException if there is no method throws NullPointerException
     */
    public FlingCardListener getTopCardListener() throws NullPointerException {
        if (flingCardListener == null) {
            Utils.entry_log();
            throw new NullPointerException();
        }
        return flingCardListener;
    }

    public void setMaxVisible(int maxVisible) {
        Utils.entry_log();
        this.maxVisible = maxVisible;
    }

    public void setMinStackInAdapter(int minAdapterStack) {
        Utils.entry_log();
        this.minAdapterStack = minAdapterStack;
    }


    public BaseItemProvider getAdapter() {
        Utils.entry_log();
        return mAdapter;
    }


    /**
     * setAdapter.
     *
     * @param adapter adapter
     */
    public void setAdapter(BaseItemProvider adapter) {
        Utils.entry_log();
        if (mAdapter != null && dataSetObserver != null) {
            Utils.entry_log();
            mAdapter.removeDataSubscriber(dataSetObserver);
            dataSetObserver = null;
        }

        mAdapter = adapter;
        currentAdapterCount = adapter.getCount();
        adapterCount = mAdapter.getCount();


        if (mAdapter != null && dataSetObserver == null) {
            Utils.entry_log();
            dataSetObserver = new AdapterDataSetObserver();
            mAdapter.addDataSubscriber(dataSetObserver);
        }
    }

    public void setFlingListener(OnCardFlingListener onCardFlingListener) {
        Utils.entry_log();
        this.flingListener = onCardFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        Utils.entry_log();
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public StackLayout.LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return new StackLayout.LayoutConfig(getContext(), attrSet);
    }

    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {

    }


    private class AdapterDataSetObserver extends DataSetSubscriber {
        @Override
        public void onChanged() {
            Utils.entry_log();
            int newAdapterCount = mAdapter.getCount();
            currentAdapterCount += newAdapterCount - adapterCount;
            adapterCount = newAdapterCount;
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            Utils.entry_log();
            requestLayout();
        }

    }

    /**
     * OnItemClickListener.
     */
    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    /**
     * OnCardFlingListener.
     */
    public interface OnCardFlingListener {
        void onCardExitLeft(Object dataObject);

        void onCardExitRight(Object dataObject);

        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent);

        void onCardExitTop(Object dataObject);

        void onCardExitBottom(Object dataObject);
    }

    public void throwLeft() {
        Utils.entry_log(flingCardListener);
        flingCardListener.selectLeft();
    }

    public void throwRight() {
        Utils.entry_log();
        flingCardListener.selectRight();
    }

    public void throwTop() {
        Utils.entry_log();
        flingCardListener.selectTop();
    }

    public void throwBottom() {
        Utils.entry_log();
        flingCardListener.selectBottom();
    }

}
