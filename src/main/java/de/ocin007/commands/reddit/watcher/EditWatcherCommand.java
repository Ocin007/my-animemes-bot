package de.ocin007.commands.reddit.watcher;

import de.ocin007.Bot;
import de.ocin007.commands.AbstractAdminCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EditWatcherCommand extends AbstractAdminCommand {

    private static final String REMOVE_LAST_ID_FLAG = "rmID";

    public EditWatcherCommand() {
        super(Prefix.ADMIN, Cmd.EDIT_WATCHER);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal() + " " + Cmd.EDIT_WATCHER.literal() + " " +
                "<r/subreddit> <'hot'|'new'|'rising'|'-'> <textChannel ID|'-'> <'" + REMOVE_LAST_ID_FLAG + "'|'-'>";
    }

    @Override
    public String getCmdDescription() {
        return "edits an existing subreddit in watchlist. use '-' if you dont want to change a parameter\n" +
                "**<r/*subreddit*>** an existing subreddit, has to start with 'r/'\n" +
                "**<'hot'|'new'|'rising'|'-'>** sorts subreddit by hot|new|rising\n" +
                "**<textChannel ID|'-'>** sets a new channel where the reddit stuff gets printed\n" +
                "**<'" + REMOVE_LAST_ID_FLAG + "'|->** when '" + REMOVE_LAST_ID_FLAG + "' is set, " +
                "the bot starts by the 100th post from now instead of the last printed one";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if (args.length != 4) {
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
        if (!args[2].equals("-")) {
            TextChannel channel = Bot.getShardManager().getTextChannelById(args[2]);
            if (channel == null) {
                return false;
            }
        }
        return args[3].equals(REMOVE_LAST_ID_FLAG) || args[3].equals("-");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        JSONObject subJson = config.getWatcher(args[0]);
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
            sub.setTextChannel(args[2]);
        }
        if (!args[3].equals("-")) {
            sub.setLastPostId(null);
            sub.setTimestamp(null);
            sub.setFallback(new JSONArray());
        }
        config.setWatcher(sub.getSubreddit(), sub);
        event.getTextChannel().sendMessage(
                Msg.SUCCESS.literal() + " " + TextFace.HAPPY
        ).queue();
    }
}
