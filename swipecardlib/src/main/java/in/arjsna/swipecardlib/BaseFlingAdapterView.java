package in.arjsna.swipecardlib;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.app.Context;

abstract class BaseFlingAdapterView extends ComponentContainer
        implements Component.LayoutRefreshedListener, Component.BindStateChangedListener,
        Component.EstimateSizeListener {

    private int heightMeasureSpec;
    private int widthMeasureSpec;

    public BaseFlingAdapterView(Context context) {
        super(context);
        this.setEstimateSizeListener(this);
    }

    public BaseFlingAdapterView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        this.setEstimateSizeListener(this);
    }

    public BaseFlingAdapterView(Context context, AttrSet attrSet, String styleName) {
        super(context, attrSet, styleName);
        this.setEstimateSizeListener(this);
    }

    @Override
    public boolean onEstimateSize(int widthEstimateConfig,
                                  int heightEstimateConfig) {
        this.widthMeasureSpec = widthEstimateConfig;
        this.heightMeasureSpec = heightEstimateConfig;

        return false;
    }

    public int getWidthMeasureSpec() {
        return widthMeasureSpec;
    }

    public int getHeightMeasureSpec() {
        return heightMeasureSpec;
    }


}
