package de.ocin007.commands.reddit.downloader;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONObject;

public class AddDownloaderCommand extends AbstractCommand {

    public AddDownloaderCommand() {
        super(Prefix.DOWNLOADER, Cmd.ADD_SUBREDDIT);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.DOWNLOADER.literal()+" "+Cmd.ADD_SUBREDDIT.literal()+" <r/subreddit> <'hot'|'new'|'rising'>";
    }

    @Override
    public String getCmdDescription() {
        return "adds the subreddit to the downloader-list. does nothing if subreddit already exists.\n" +
                "**<r/*subreddit*>**  an existing subreddit, has to start with 'r/'\n" +
                "**<'hot'|'new'|'rising'>** sort subreddit by hot|new|rising";
    }

    @Override
    protected boolean argsValid(String[] args) {
        if(args.length != 2) {
            return false;
        }
        if(!SubRedditType.nameIsValid(args[0])) {
            return false;
        }
        return args[1].equals(SubRedditType.SORT_BY_HOT) ||
                args[1].equals(SubRedditType.SORT_BY_NEW) ||
                args[1].equals(SubRedditType.SORT_BY_RISING);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        SubRedditType sub = new SubRedditType(
                args[0],
                args[1],
                null,
                null,
                false,
                null
        );
        Config config = Config.getInstance();
        JSONObject existingSub = config.getDownloader(sub.getSubreddit());
        if(existingSub == null) {
            config.setDownloader(sub.getSubreddit(), sub);
            event.getTextChannel().sendMessage(
                    Msg.SUCCESS.literal()+" "+TextFace.HAPPY
            ).queue();
        } else {
            event.getTextChannel().sendMessage(
                    Msg.SUB_ALR_EXIST.literal()+" "+TextFace.IDK
            ).queue();
        }
    }
}
