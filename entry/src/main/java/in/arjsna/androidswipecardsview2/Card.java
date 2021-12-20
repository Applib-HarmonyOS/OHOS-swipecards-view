package in.arjsna.androidswipecardsview2;

/**
 * Created by arjun on 4/25/16.
 */
public class Card {
    private String name;
    private int imageId;

    public Card(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    @Override
    public String toString() {
        return "Card { name: " + name + ", imageId: " + imageId + " }";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
