package net.redstonecraft.tictactoebot.tictactoe;

import net.dv8tion.jda.api.entities.Member;

public class DiscordPlayer extends Player {

    private final Member member;

    public DiscordPlayer(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equalsPlayer(Player player) {
        if (player instanceof DiscordPlayer) {
            return member.equals(((DiscordPlayer) player).member);
        } else {
            return false;
        }
    }
}
