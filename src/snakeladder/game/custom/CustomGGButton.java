package snakeladder.game.custom;

import ch.aplu.jgamegrid.GGButton;

public class CustomGGButton extends GGButton {
    private int tag;

    public CustomGGButton(int tag, String buttonImage) {
        super(buttonImage);
        setTag(tag);
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
