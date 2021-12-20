package in.arjsna.androidswipecardsview2;


import ohos.agp.components.BaseItemProvider;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;
import ohos.agp.components.Text;
import ohos.app.Context;
import java.util.ArrayList;

/**
 * Created by arjun on 4/25/16.
 */
public class CardsAdapter extends BaseItemProvider {
    private final ArrayList<Card> cards;
    private final LayoutScatter layoutScatter;

    /**
     * CardsAdapter.
     *
     * @param context application context
     * @param cards array of cards
     */
    public CardsAdapter(Context context, ArrayList<Card> cards) {
        this.cards = cards;
        this.layoutScatter = LayoutScatter.getInstance(context);
    }

    @Override
    public Component getComponent(int position, Component component, ComponentContainer componentContainer) {
        Card card = cards.get(position);
        Component view = layoutScatter.parse(ResourceTable.Layout_item, null, false);
        ((Image) view.findComponentById(ResourceTable.Id_card_image)).setImageAndDecodeBounds(card.getImageId());
        ((Text) view.findComponentById(ResourceTable.Id_helloText)).setText(card.getName());
        return view;
    }

    @Override
    public Card getItem(int position) {
        return cards.get(position);
    }

    @Override
    public int getCount() {
        return cards.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

}
