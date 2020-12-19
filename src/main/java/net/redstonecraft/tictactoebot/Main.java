package net.redstonecraft.tictactoebot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.redstonecraft.tictactoebot.listeners.MessageListener;

import javax.security.auth.login.LoginException;

public class Main {

    private final static String botToken = "";
    private JDA jda;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        JDABuilder builder = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES);
        builder.addEventListeners(new MessageListener());
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("TicTacToe"));
        try {
            jda = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public JDA getJda() {
        return jda;
    }
}
