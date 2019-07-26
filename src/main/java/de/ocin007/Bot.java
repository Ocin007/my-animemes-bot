package de.ocin007;

import de.ocin007.builder.CommandBuilder;
import de.ocin007.config.Config;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;

import java.util.*;

public class Bot implements EventListener {

    private DefaultShardManagerBuilder builder;
    private static ShardManager shardManager;

    public static void main(String[] args) {
        new Bot();
    }

    private Bot() {
        try {
            Config config = Config.getInstance();
            this.builder = new DefaultShardManagerBuilder();

            this.builder.setToken(config.getConfig("botToken"));
            this.builder.setStatus(OnlineStatus.ONLINE);
            this.loadCommands();

            Bot.setShardManager(this.builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    private static void setShardManager(ShardManager shardManager) {
        Bot.shardManager = shardManager;
    }

    private void loadCommands() {
        CommandBuilder cmdBuilder = new CommandBuilder(this.builder);
        cmdBuilder.build();
    }
}
