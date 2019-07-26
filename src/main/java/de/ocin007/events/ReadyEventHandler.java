package de.ocin007.events;

import de.ocin007.Bot;
import de.ocin007.commands.AbstractCommand;
import de.ocin007.commands.ServiceCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Msg;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ReadyEventHandler extends ListenerAdapter {

    private Collection<AbstractCommand> cmdList;

    public ReadyEventHandler(Collection<AbstractCommand> cmdList) {
        this.cmdList = cmdList;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        this.cmdList.forEach((cmd) -> {
            if(cmd instanceof ServiceCommand) {
                ((ServiceCommand) cmd).restartService();
            }
        });
        this.sendMsg("@here "+Msg.HELLO.literal()+" "+TextFace.WAVE);
        this.sendMsg(Msg.RESTART_SERVICES.literal());
    }

    private void sendMsg(String msg) {
        ShardManager bot = Bot.getShardManager();
        if(bot != null) {
            TextChannel channel = bot.getTextChannelById(
                    Config.getInstance().getConfig("defaultChannelId")
            );
            channel.sendMessage(msg).queue();
        } else {
            System.out.println(msg);
        }
    }
}
