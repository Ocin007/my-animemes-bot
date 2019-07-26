package de.ocin007.commands.general;

import de.ocin007.Bot;
import de.ocin007.commands.AbstractCommand;
import de.ocin007.commands.ServiceCommand;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;

public class ShutdownCommand extends AbstractCommand {

    private Collection<AbstractCommand> cmdList;

    public ShutdownCommand(Collection<AbstractCommand> cmdList) {
        super(Prefix.GENERAL, Cmd.SHUTDOWN);
        this.cmdList = cmdList;
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.SHUTDOWN.literal();
    }

    @Override
    public String getCmdDescription() {
        return "shuts down the bot and all running services";
    }

    @Override
    protected boolean argsValid(String[] args) {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        this.stopServices(event);
        ShardManager shard = Bot.getShardManager();
        event.getTextChannel().sendMessage(
                Msg.SHUTDOWN.literal()+" "+TextFace.WAVE
        ).queue((msg) -> {
            shard.setStatus(OnlineStatus.OFFLINE);
            shard.shutdown();
            System.exit(0);
        });
    }

    private void stopServices(MessageReceivedEvent event) {
        this.cmdList.forEach((cmd) -> {
            if(cmd instanceof ServiceCommand) {
                ((ServiceCommand) cmd).shutdownService(event);
            }
        });
    }
}
