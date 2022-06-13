package org.unibl.etf.models.card;

import javafx.scene.image.Image;

public class NormalCard extends Card{

    public NormalCard(Image image, int number) {
        super(image, number);
    }

    @Override
    public String toString() {
        return "Obična karta - figura prelazi " + number + " polja";
    }
}
