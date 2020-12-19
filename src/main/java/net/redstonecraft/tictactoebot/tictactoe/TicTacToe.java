package net.redstonecraft.tictactoebot.tictactoe;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.redstonecraft.tictactoebot.tictactoe.enums.SetResponse;
import net.redstonecraft.tictactoebot.tictactoe.enums.Tile;

import java.awt.*;
import java.util.Arrays;

public class TicTacToe {

    public final Tile[][] game;
    public final Player blue;
    public final Player red;
    private Player currentPlayer;
    public final TextChannel channel;
    public final Message message;

    public TicTacToe(Player player1, Player player2, TextChannel channel) {
        blue = player1;
        red = player2;
        currentPlayer = player1;
        this.channel = channel;
        game = new Tile[][]{
                {Tile.UNSET, Tile.UNSET, Tile.UNSET},
                {Tile.UNSET, Tile.UNSET, Tile.UNSET},
                {Tile.UNSET, Tile.UNSET, Tile.UNSET}
        };
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#fefefe"));
        eb.setTitle("TicTacToe");
        message = channel.sendMessage(renderGame(eb).build()).complete();
    }

    private EmbedBuilder renderGame(EmbedBuilder eb) {
        Tile[][] copy = Arrays.stream(game).map(Tile[]::clone).toArray(Tile[][]::new);
        int[][] blueWon = hasWon(Tile.BLUE);
        int[][] redWon = hasWon(Tile.RED);
        String suffix = "";
        if (blueWon != null) {
            for (int[] i : blueWon) {
                copy[i[0]][i[1]] = Tile.WONBLUE;
            }
            suffix = "\n" + ((DiscordPlayer) blue).getMember().getAsMention() + " hat gewonnen.";
        } else if (redWon != null) {
            for (int[] i : redWon) {
                copy[i[0]][i[1]] = Tile.WONRED;
            }
            if (red instanceof DiscordPlayer) {
                suffix = "\n" + ((DiscordPlayer) red).getMember().getAsMention() + " hat gewonnen.";
            } else {
                suffix = "\nDer Computer hat gewonnen.";
            }
        } else if (gameCount(Tile.UNSET) == 0) {
            suffix = "\nUnentschieden.";
        }
        eb.setDescription(":black_large_square::one::two::three:\n" +
                ":regional_indicator_a:" + copy[0][0].rendered + copy[0][1].rendered + copy[0][2].rendered + "\n" +
                ":regional_indicator_b:" + copy[1][0].rendered + copy[1][1].rendered + copy[1][2].rendered + "\n" +
                ":regional_indicator_c:" + copy[2][0].rendered + copy[2][1].rendered + copy[2][2].rendered + suffix);
        return eb;
    }

    public SetResponse set(Player player, int[] pos) {
        if (player.equalsPlayer(currentPlayer)) {
            if (game[pos[0]][pos[1]].equals(Tile.UNSET)) {
                Tile t = getTileByPlayer(player);
                if (t != null) {
                    game[pos[0]][pos[1]] = t;
                } else {
                    return SetResponse.ALREADYSET;
                }
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(Color.decode("#fefefe"));
                eb.setTitle("TicTacToe");
                message.editMessage(renderGame(eb).build()).queue();
            } else {
                return SetResponse.ALREADYSET;
            }
            if (player.equalsPlayer(blue)) {
                currentPlayer = red;
            } else if (player.equalsPlayer(red)) {
                currentPlayer = blue;
            }
            return (hasWon(Tile.RED) != null || hasWon(Tile.BLUE) != null || gameCount(Tile.UNSET) == 0) ? SetResponse.EXIT : SetResponse.OK;
        } else {
            return SetResponse.OTHERSPLAYERTURN;
        }
    }

    public Tile getTileByPlayer(Player player) {
        if (player.equalsPlayer(blue)) {
            return Tile.BLUE;
        } else if (player.equalsPlayer(red)) {
            return Tile.RED;
        }
        return null;
    }

    public int[][] hasWon(Tile player) {
        for (int i = 0; i < game.length; i++) {
            if (game[i][0].equals(player) && game[i][1].equals(player) && game[i][2].equals(player)) {
                return new int[][]{{i, 0}, {i, 1}, {i, 2}};
            }
        }
        for (int i = 0; i < game.length; i++) {
            if (game[0][i].equals(player) && game[1][i].equals(player) && game[2][i].equals(player)) {
                return new int[][]{{0, i}, {1, i}, {2, i}};
            }
        }
        if (game[0][0].equals(player) && game[1][1].equals(player) && game[2][2].equals(player)) {
            return new int[][]{{0, 0}, {1, 1}, {2, 2}};
        }
        if (game[0][2].equals(player) && game[1][1].equals(player) && game[2][0].equals(player)) {
            return new int[][]{{0, 2}, {1, 1}, {2, 0}};
        }
        return null;
    }

    private int gameCount(Tile tile) {
        int c = 0;
        for (Tile[] tiles : game) {
            for (Tile j : tiles) {
                if (j.equals(tile)) {
                    c++;
                }
            }
        }
        return c;
    }

}
