package in.arjsna.swipecardlib;


import ohos.agp.components.AttrSet;
import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.StackLayout;
import ohos.agp.database.DataSetSubscriber;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * Created by arjun on 4/26/16.
 */
public class SwipePageView extends BaseFlingAdapterView {
    private int minAdapterStack = 6;
    private int maxVisible = 2;
    private BaseItemProvider mAdapter;
    private int lastObjectInStack = 0;
    private Component mActiveCard;
    private FlingPageListener flingPageListener;
    private PointF mLastTouchPoint;
    private float currentScaleVal;
    private final double scaleOffset = 0.2;
    private OnPageFlingListener flingListener;
    private OnItemClickListener onItemClickListener;
    private AdapterDataSetObserver dataSetObserver;
    private int startStackFrom = 0;
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(HiLog.LOG_APP, 0x00201, "-MainAbility-");

    public SwipePageView(Context context) {
        super(context);
        init(null);
    }

    public SwipePageView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        init(attrSet);
    }

    public SwipePageView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        init(attrSet);
    }

    /**
     * SwipePageViewAttrs.
     */
    public static class SwipePageViewAttrs {
        public static final String MIN_ADAPTER_STACK_PAGE = "min_adapter_stack_page";
    }

    void init(AttrSet attrSet) {
        setLayoutRefreshedListener(this);
        AttrUtils attrUtils = new AttrUtils(attrSet);
        maxVisible = attrUtils.getDimensionFromAttr(SwipeCardView.SwipeCardViewAttrs.MAX_VISIBLE, maxVisible);
        minAdapterStack = attrUtils.getDimensionFromAttr(SwipePageViewAttrs.MIN_ADAPTER_STACK_PAGE, minAdapterStack);
    }

    @Override
    public void onRefreshed(Component component) {
        Utils.entry_log();
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            Utils.entry_log();
            return;
        }

        boolean inLayout = true;
        final int adapterCount = mAdapter.getCount();

        if (adapterCount == 0) {
            Utils.entry_log();
            removeAllComponents();
        } else {
            Component topCard = getComponentAt(lastObjectInStack);
            if (mActiveCard != null && topCard != null && topCard == mActiveCard) {
                Utils.entry_log();
                if (this.flingPageListener.isTouching()) {
                    Utils.entry_log();
                    PointF lastPoint = this.flingPageListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        Utils.entry_log();
                        this.mLastTouchPoint = lastPoint;
                        for (int i = 0; i < lastObjectInStack; i++) {
                            removeComponentAt(i);
                        }
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

        if (adapterCount <= minAdapterStack) {
            flingListener.onAdapterAboutToEmpty(adapterCount);
        }
    }

    private void layoutChildren(int startingIndex, int adapterCount) {
        resetOffsets();
        if (adapterCount < maxVisible) {
            maxVisible = adapterCount;
        }
        int viewStack = 0;
        while (startingIndex < startStackFrom + maxVisible && startingIndex < adapterCount) {
            Utils.entry_log();
            Component newUnderChild = mAdapter.getComponent(startingIndex, null, this);
            if (newUnderChild.getVisibility() != HIDE) {
                Utils.entry_log();
                makeAndAddView(newUnderChild);
                lastObjectInStack = viewStack;
            }
            startingIndex++;
            viewStack++;
        }
    }

    private void resetOffsets() {
        Utils.entry_log();
        currentScaleVal = 0;
    }


    private void makeAndAddView(Component child) {
        Utils.entry_log();

        final LayoutConfig lp = (LayoutConfig) child.getLayoutConfig();
        child.setScaleX(child.getScaleX() - currentScaleVal);
        child.setScaleY(child.getScaleY() - currentScaleVal);
        currentScaleVal += scaleOffset;
        addComponent(child, 0, lp);


        int w = child.getEstimatedWidth();
        int h = child.getEstimatedHeight();

        int childLeft = getPaddingLeft() + lp.getMarginLeft();
        int childTop = getPaddingTop() + lp.getMarginTop();
        child.arrange(childLeft, childTop, childLeft + w, childTop + h);

    }

    private void setTopView() {
        Utils.entry_log();
        if (getChildCount() > 0) {

            mActiveCard = getComponentAt(lastObjectInStack);
            if (mActiveCard != null) {
                Utils.entry_log();

                flingPageListener = new FlingPageListener(mActiveCard, mAdapter.getItem(0),
                        new FlingPageListener.FlingListener() {
                            @Override
                            public void onCardExited() {
                                Utils.entry_log();
                                mActiveCard = null;
                                startStackFrom++;
                                postLayout();
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
                                    while (childCount > 0) {
                                        Utils.entry_log(childCount);
                                        reLayoutChild(
                                                getComponentAt(childCount - 1),
                                                Math.abs(scrollProgressPercent),
                                                childCount
                                        );
                                        childCount--;
                                    }
                                } else {
                                    while (childCount > 1) {
                                        Utils.entry_log(childCount);
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
                                flingListener.onTopCardExit(dataObject);
                            }

                            @Override
                            public void bottomExit(Object dataObject) {
                                Utils.entry_log();
                                flingListener.onBottomCardExit(dataObject);
                            }
                        });

                mActiveCard.setTouchEventListener(flingPageListener);
            }
        }
    }

    /**
     * reLayoutChild.
     *
     * @param child child
     * @param scrollDis scrollDis
     * @param childcount childcount
     */
    public void reLayoutChild(Component child, float scrollDis, int childcount) {
        Utils.entry_log();
        float absScrollDis = scrollDis > 1 ? 1 : scrollDis;
        float newScale = (float) (1 - scaleOffset * (maxVisible - childcount) + absScrollDis * scaleOffset);
        child.setScaleX(newScale);
        child.setScaleY(newScale);
    }

    @Override
    public void onComponentBoundToWindow(Component component) {

    }

    @Override
    public void onComponentUnboundFromWindow(Component component) {

    }


    /**
     * OnItemClickListener.
     */
    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public BaseItemProvider getAdapter() {
        Utils.entry_log();
        return null;
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

        if (mAdapter != null && dataSetObserver == null) {
            Utils.entry_log();
            dataSetObserver = new AdapterDataSetObserver();
            mAdapter.addDataSubscriber(dataSetObserver);
        }
    }

    public Component getSelectedView() {
        Utils.entry_log();
        return mActiveCard;
    }

    public void setFlingListener(OnPageFlingListener onPageFlingListener) {
        Utils.entry_log();
        this.flingListener = onPageFlingListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * OnPageFlingListener.
     */
    public interface OnPageFlingListener {
        void onAdapterAboutToEmpty(int itemsInAdapter);

        void onScroll(float scrollProgressPercent);

        void onTopCardExit(Object dataObject);

        void onBottomCardExit(Object dataObject);
    }

    private class AdapterDataSetObserver extends DataSetSubscriber {
        @Override
        public void onChanged() {
            Utils.entry_log();
            postLayout();
        }

        @Override
        public void onInvalidated() {
            Utils.entry_log();
            postLayout();
        }

    }

    @Override
    public LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return new StackLayout.LayoutConfig(getContext(), attrSet);
    }
}
