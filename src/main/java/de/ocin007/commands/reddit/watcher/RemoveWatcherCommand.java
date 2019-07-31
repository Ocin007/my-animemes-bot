package de.ocin007.commands.reddit.watcher;

import de.ocin007.commands.AbstractAdminCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveWatcherCommand extends AbstractAdminCommand {

    public RemoveWatcherCommand() {
        super(Prefix.ADMIN, Cmd.REMOVE_WATCHER);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal()+" "+Cmd.REMOVE_WATCHER.literal()+" <r/subreddit>";
    }

    @Override
    public String getCmdDescription() {
        return "removes an existing subreddit\n" +
                "**<r/*subreddit*>** an existing subreddit, has to start with 'r/'";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if(args.length != 1) {
            return false;
        }
        return args[0].startsWith("r/");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        if(config.getWatcher(args[0]) == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal()+" "+TextFace.IDK
            ).queue();
        } else {
            config.removeWatcher(args[0]);
            event.getTextChannel().sendMessage(
                    Msg.SUCCESS.literal()+" "+TextFace.HAPPY
            ).queue();
        }
    }
}
