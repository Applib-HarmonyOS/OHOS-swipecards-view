package in.arjsna.androidswipecardsview2;

import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.app.Context;
import java.util.ArrayList;

/**
 * Created by arjun on 4/26/16.
 */
public class PageAdapter extends BaseItemProvider {
    private final ArrayList<Card> cards;
    private final LayoutScatter layoutInflater;


    /**
     * PageAdapter.
     *
     * @param context application context
     * @param cards array of cards
     */
    public PageAdapter(Context context, ArrayList<Card> cards) {
        super();
        this.cards = cards;
        this.layoutInflater = LayoutScatter.getInstance(context);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Card getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Card card = cards.get(position);
        Component view = layoutInflater.parse(ResourceTable.Layout_page_item, null, false);
        ((Image) view.findComponentById(ResourceTable.Id_card_image)).setPixelMap(card.getImageId());
        return view;
    }

}
