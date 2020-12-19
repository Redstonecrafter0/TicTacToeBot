package net.redstonecraft.tictactoebot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.redstonecraft.tictactoebot.tictactoe.DiscordPlayer;
import net.redstonecraft.tictactoebot.tictactoe.TicTacToe;
import net.redstonecraft.tictactoebot.tictactoe.enums.SetResponse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageListener extends ListenerAdapter {

    private final List<Request> requests = new ArrayList<>();
    private final HashMap<Member, TicTacToe> games = new HashMap<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        processRequests();
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.decode("#fefefe"));
        eb.setTitle("TicTacToe");
        if (event.getMessage().getContentRaw().startsWith("!ttt invite")) {
            if (!games.containsKey(event.getMember())) {
                if (event.getMessage().getMentionedMembers().size() == 1) {
                    if (!games.containsKey(event.getMessage().getMentionedMembers().get(0))) {
                        if (!requestsContainsInvited(event.getMember()) && !requestsContainsInviter(event.getMember())
                        && !requestsContainsInvited(event.getMessage().getMentionedMembers().get(0)) && !requestsContainsInviter(event.getMessage().getMentionedMembers().get(0))) {
                            requests.add(new Request(event.getMember(), event.getMessage().getMentionedMembers().get(0), event.getChannel(), (System.currentTimeMillis() / 1000L) + 60));
                            eb.setDescription("Du hast " + event.getMessage().getMentionedMembers().get(0).getAsMention() + " zu einem Spiel eingeladen.\nEr hat 60 Sekunden Zeit, die Einladung anzunehmen.");
                            event.getChannel().sendMessage(eb.build()).queue();
                        } else {
                            eb.setDescription("Es läuft schon ein Spiel.");
                            event.getChannel().sendMessage(eb.build()).queue();
                        }
                    } else {
                        eb.setDescription("Dieser Spieler spielt bereits.");
                        event.getChannel().sendMessage(eb.build()).queue();
                    }
                } else {
                    eb.setDescription("Wen willst du einladen?");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
            } else {
                eb.setDescription("Du spielst bereits ein Spiel.");
                event.getChannel().sendMessage(eb.build()).queue();
            }
        } else if (event.getMessage().getContentRaw().startsWith("!ttt accept")) {
            if (!games.containsKey(event.getMember())) {
                if (event.getMessage().getMentionedMembers().size() == 1) {
                    if (!games.containsKey(event.getMessage().getMentionedMembers().get(0))) {
                        Request r = requestsContains(event.getMessage().getMentionedMembers().get(0), event.getMember());
                        if (r != null) {
                            eb.setDescription("Das Spiel beginnt.\nViel Glück.");
                            event.getChannel().sendMessage(eb.build()).complete();
                            TicTacToe ttt = new TicTacToe(new DiscordPlayer(r.inviter), new DiscordPlayer(r.invited), r.channel);
                            games.put(r.inviter, ttt);
                            games.put(r.invited, ttt);
                            requests.remove(r);
                        } else {
                            eb.setDescription("Dieser Spieler spielt bereits.");
                            event.getChannel().sendMessage(eb.build()).queue();
                        }
                    } else {
                        eb.setDescription("Dieser Spieler spielt bereits.");
                        event.getChannel().sendMessage(eb.build()).queue();
                    }
                } else {
                    eb.setDescription("Wessen Einladung willst du annehmen?");
                    event.getChannel().sendMessage(eb.build()).queue();
                }
            } else {
                eb.setDescription("Du spielst bereits ein Spiel.");
                event.getChannel().sendMessage(eb.build()).queue();
            }
        } else if (Arrays.asList(new String[]{"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3"}).contains(event.getMessage().getContentRaw())) {
            if (games.containsKey(event.getMember())) {
                TicTacToe ttt = games.get(event.getMember());
                SetResponse response = ttt.set(new DiscordPlayer(event.getMember()), getPosByInput(event.getMessage().getContentRaw()));
                eb.setColor(Color.decode("#ff0000"));
                switch (response) {
                    case OTHERSPLAYERTURN:
                        eb.setDescription("Der andere Spieler ist gerade dran.");
                        ttt.channel.sendMessage(eb.build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
                        break;
                    case ALREADYSET:
                        eb.setDescription("Dieses Feld wurde schon belegt.");
                        ttt.channel.sendMessage(eb.build()).complete().delete().queueAfter(3, TimeUnit.SECONDS);
                        break;
                    case EXIT:
                        games.remove(((DiscordPlayer) ttt.blue).getMember());
                        if (ttt.red instanceof DiscordPlayer) {
                            games.remove(((DiscordPlayer) ttt.red).getMember());
                        }
                        break;
                }
                event.getMessage().delete().queue();
            }
        }
    }

    private static int[] getPosByInput(String input) {
        char columnChar = input.charAt(0);
        int column;
        int row = Integer.parseInt(String.valueOf(input.charAt(1))) - 1;
        switch (columnChar) {
            case 'A':
                column = 0;
                break;
            case 'B':
                column = 1;
                break;
            case 'C':
                column = 2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + columnChar);
        }
        return new int[]{column, row};
    }

    private Request requestsContains(Member inviter, Member invited) {
        for (Request i : requests) {
            if (i.inviter.equals(inviter)  && i.invited.equals(invited)) {
                return i;
            }
        }
        return null;
    }

    private boolean requestsContainsInvited(Member member) {
        for (Request i : requests) {
            if (i.invited.equals(member)) {
                return true;
            }
        }
        return false;
    }

    private boolean requestsContainsInviter(Member member) {
        for (Request i : requests) {
            if (i.inviter.equals(member)) {
                return true;
            }
        }
        return false;
    }

    private void processRequests() {
        requests.removeIf(i -> i.timeout < (System.currentTimeMillis() / 1000L));
    }

}

class Request {

    public final Member inviter;
    public final Member invited;
    public final TextChannel channel;
    public final long timeout;

    public Request(Member inviter, Member invited, TextChannel channel, long timeout) {
        this.inviter = inviter;
        this.invited = invited;
        this.channel = channel;
        this.timeout = timeout;
    }

}
