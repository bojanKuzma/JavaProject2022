package org.unibl.etf.models.pawn;

public enum Color {
    RED("#ff1d00"), YELLOW("#e4db33"), GREEN("#46bc25"), BLUE("#4492ff");

    private final String colorCode;

    public String getColorCode()
    {
        return this.colorCode;
    }

    Color(String colorCode) {
        this.colorCode = colorCode;
    }
}
