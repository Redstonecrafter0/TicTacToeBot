package net.redstonecraft.tictactoebot.tictactoe;

public class ComputerPlayer extends Player {
    @Override
    public boolean equalsPlayer(Player player) {
        return player instanceof ComputerPlayer;
    }
}
