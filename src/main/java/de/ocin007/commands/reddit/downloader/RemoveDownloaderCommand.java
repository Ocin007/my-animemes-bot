package de.ocin007.commands.reddit.downloader;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveDownloaderCommand extends AbstractCommand {

    public RemoveDownloaderCommand() {
        super(Prefix.DOWNLOADER, Cmd.REMOVE_SUBREDDIT);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.DOWNLOADER.literal()+" "+Cmd.REMOVE_SUBREDDIT.literal()+" <r/subreddit>";
    }

    @Override
    public String getCmdDescription() {
        return "removes an existing subreddit\n" +
                "**<r/*subreddit*>** an existing subreddit, has to start with 'r/'";
    }

    @Override
    protected boolean argsValid(String[] args) {
        if(args.length != 1) {
            return false;
        }
        return args[0].startsWith("r/");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        if(config.getDownloader(args[0]) == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal()+" "+TextFace.IDK
            ).queue();
        } else {
            config.removeDownloader(args[0]);
            event.getTextChannel().sendMessage(
                    Msg.SUCCESS.literal()+" "+TextFace.HAPPY
            ).queue();
        }
    }
}
