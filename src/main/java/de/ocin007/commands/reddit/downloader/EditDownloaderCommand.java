package de.ocin007.commands.reddit.downloader;

import de.ocin007.commands.AbstractAdminCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONObject;

public class EditDownloaderCommand extends AbstractAdminCommand {

    private static final String REMOVE_LAST_ID_FLAG = "rmID";

    public EditDownloaderCommand() {
        super(Prefix.ADMIN, Cmd.EDIT_DOWNLOADER);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal() + " " + Cmd.EDIT_DOWNLOADER.literal() + " " +
                "<r/subreddit> <'hot'|'new'|'rising'|'-'> <'" + REMOVE_LAST_ID_FLAG + "'|'-'>";
    }

    @Override
    public String getCmdDescription() {
        return "edits an existing subreddit in downloaderlist. use '-' if you dont want to change a parameter\n" +
                "**<r/*subreddit*>** an existing subreddit, has to start with 'r/'\n" +
                "**<'hot'|'new'|'rising'|'-'>** sorts subreddit by hot|new|rising\n" +
                "**<'" + REMOVE_LAST_ID_FLAG + "'|->** when '" + REMOVE_LAST_ID_FLAG + "' is set, " +
                "the bot starts by the 100th post from now instead of the last printed one";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if (args.length != 3) {
            return false;
        }
        if (!args[0].startsWith("r/")) {
            return false;
        }
        if (!args[1].equals(SubRedditType.SORT_BY_HOT) &&
                !args[1].equals(SubRedditType.SORT_BY_NEW) &&
                !args[1].equals(SubRedditType.SORT_BY_RISING) &&
                !args[1].equals("-")) {
            return false;
        }
        return args[2].equals(REMOVE_LAST_ID_FLAG) || args[2].equals("-");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        JSONObject subJson = config.getDownloader(args[0]);
        if (subJson == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal() + " " + TextFace.IDK
            ).queue();
            return;
        }
        SubRedditType sub = new SubRedditType(subJson);
        if (!args[1].equals("-")) {
            sub.setSortBy(args[1]);
        }
        if (!args[2].equals("-")) {
            sub.setLastPostId(null);
            sub.setTimestamp(null);
        }
        config.setDownloader(sub.getSubreddit(), sub);
        event.getTextChannel().sendMessage(
                Msg.SUCCESS.literal() + " " + TextFace.HAPPY
        ).queue();
    }
}
