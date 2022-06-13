package org.unibl.etf.models.card;

import javafx.scene.image.Image;

public abstract class Card {
    protected Image image;
    protected int number;

    protected Card(Image image, int number){
        this.image = image;
        this.number = number;
    }

    public Image getImage() {
        return image;
    }

    public int getNumber() {
        return number;
    }

}
