package in.arjsna.androidswipecardsview2.slice;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import in.arjsna.androidswipecardsview2.Card;
import in.arjsna.androidswipecardsview2.PageAdapter;
import in.arjsna.androidswipecardsview2.ResourceTable;
import in.arjsna.swipecardlib.SwipePageView;
import java.util.ArrayList;

/**
 * PageSwipeAbilitySlice.
 */
public class PageSwipeAbilitySlice extends AbilitySlice {

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_activity_page_swipe);

        SwipePageView flingContainer = (SwipePageView) findComponentById(ResourceTable.Id_page_swipe_view);
        ArrayList<Card> al = new ArrayList<>();
        getDummyData(al);

        PageAdapter arrayAdapter = new PageAdapter(this, al);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipePageView.OnPageFlingListener() {
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                makeToast("onAdapterAboutToEmpty");
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                // pass
            }

            @Override
            public void onTopCardExit(Object dataObject) {
                makeToast("onTopCardExit");
            }

            @Override
            public void onBottomCardExit(Object dataObject) {
                makeToast("onBottomCardExit");
            }
        });

        flingContainer.setOnItemClickListener((itemPosition, dataObject) ->
                makeToast("Pos: " + itemPosition + " Card: " + dataObject)
        );


        flingContainer.setOnItemClickListener((itemPosition, dataObject) ->
                makeToast("itemPosition: " + itemPosition)
        );

    }

    void makeToast(String msg) {
        new ToastDialog(getContext()).setText(msg).setDuration(1000).show();
    }

    private void getDummyData(ArrayList<Card> al) {
        Card page1 = new Card("John", ResourceTable.Media_faces1);
        Card page2 = new Card("Mike", ResourceTable.Media_faces2);
        Card page3 = new Card("Ronoldo", ResourceTable.Media_faces3);
        Card page4 = new Card("Messi", ResourceTable.Media_faces4);
        Card page5 = new Card("Sachin", ResourceTable.Media_faces5);
        Card page6 = new Card("Dhoni", ResourceTable.Media_faces6);
        Card page7 = new Card("Kohli", ResourceTable.Media_faces7);
        Card page8 = new Card("Pandya", ResourceTable.Media_faces8);
        Card page9 = new Card("Nehra", ResourceTable.Media_faces9);
        Card page10 = new Card("Bumra", ResourceTable.Media_faces10);
        Card page11 = new Card("Rohit", ResourceTable.Media_faces11);
        al.add(page1);
        al.add(page2);
        al.add(page3);
        al.add(page4);
        al.add(page5);
        al.add(page6);
        al.add(page7);
        al.add(page8);
        al.add(page9);
        al.add(page10);
        al.add(page11);
    }
}
