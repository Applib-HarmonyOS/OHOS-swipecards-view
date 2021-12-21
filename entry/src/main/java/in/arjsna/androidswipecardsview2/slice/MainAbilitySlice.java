package in.arjsna.androidswipecardsview2.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import in.arjsna.androidswipecardsview2.Card;
import in.arjsna.androidswipecardsview2.CardsAdapter;
import in.arjsna.androidswipecardsview2.ResourceTable;
import in.arjsna.swipecardlib.SwipeCardView;
import java.util.ArrayList;

/**
 * MainAbilitySlice.
 */
public class MainAbilitySlice extends AbilitySlice {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main);
        ArrayList<Card> arrayList = new ArrayList<>();
        getDummyData(arrayList);

        CardsAdapter arrayAdapter = new CardsAdapter(this, arrayList);
        SwipeCardView swipeCardView = (SwipeCardView) findComponentById(ResourceTable.Id_card_stack_view);

        swipeCardView.setAdapter(arrayAdapter);
        swipeCardView.setFlingListener(new SwipeCardView.OnCardFlingListener() {
            @Override
            public void onCardExitLeft(Object dataObject) {
                makeToast("Left !");
            }

            @Override
            public void onCardExitRight(Object dataObject) {
                makeToast("Right !");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // pass
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                // pass
            }

            @Override
            public void onCardExitTop(Object dataObject) {
                makeToast("Top !");
            }

            @Override
            public void onCardExitBottom(Object dataObject) {
                makeToast("Bottom !");
            }
        });

        swipeCardView.setOnItemClickListener((itemPosition, dataObject) ->
                makeToast("itemPosition: " + itemPosition)
        );

        findComponentById(ResourceTable.Id_left).setClickedListener(c -> swipeCardView.throwLeft());
        findComponentById(ResourceTable.Id_right).setClickedListener(c -> swipeCardView.throwRight());
        findComponentById(ResourceTable.Id_top).setClickedListener(c -> swipeCardView.throwTop());
        findComponentById(ResourceTable.Id_bottom).setClickedListener(c -> swipeCardView.throwBottom());
        findComponentById(ResourceTable.Id_restart).setClickedListener(c -> swipeCardView.restart());
        findComponentById(ResourceTable.Id_position).setClickedListener(c -> {
            int pos = swipeCardView.getCurrentPosition();
            makeToast("Pos: " + pos);
        });


    }


    private void getDummyData(ArrayList<Card> al) {
        Card card = new Card("Card : 1", ResourceTable.Media_faces1);
        Card card2 = new Card("Card : 2", ResourceTable.Media_faces2);
        Card card3 = new Card("Card : 3", ResourceTable.Media_faces3);
        Card card4 = new Card("Card : 4", ResourceTable.Media_faces4);
        al.add(card);
        al.add(card2);
        al.add(card3);
        al.add(card4);
    }

    void makeToast(String msg) {
        new ToastDialog(getContext()).setText(msg).setDuration(1000).show();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
