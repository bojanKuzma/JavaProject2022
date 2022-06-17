package org.unibl.etf.models.card;

import javafx.scene.image.Image;

public class SpecialCard extends Card implements Stoppable{
    public SpecialCard(Image image, int number) {
        super(image, number);
    }

    @Override
    public String toString() {
        return "Specijalna karta - pojaviće se rupe na " + number + " polja";
    }
}
