package net.redstonecraft.tictactoebot.tictactoe.enums;

public enum Tile {

    BLUE(":blue_square:"),
    RED(":red_square:"),
    WONBLUE(":regional_indicator_x:"),
    WONRED(":o2:"),
    UNSET(":white_large_square:");

    public final String rendered;

    Tile(String rendered) {
        this.rendered = rendered;
    }

}
