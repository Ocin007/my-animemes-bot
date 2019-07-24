package de.ocin007.commands.reddit;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ShowWatchListCommand extends AbstractCommand {

    public ShowWatchListCommand() {
        super(Prefix.GENERAL, Cmd.SHOW);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.SHOW.literal()+" <r/subreddit|'all'>";
    }

    @Override
    public String getCmdDescription() {
        return "prints out all params of the given subreddit, or prints everything when 'all' is given\n" +
                "**<r/*subreddit*|'all'>** an existing subreddit, has to start with 'r/', or just 'all'";
    }

    @Override
    protected boolean argsValid(String[] args) {
        if (args.length != 1) {
            return false;
        }
        return args[0].equals("all") || args[0].startsWith("r/");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        if(args[0].equals("all")) {
            StringBuilder msg = new StringBuilder();
            JSONArray list = config.getAllSubReddits();
            if(list.isEmpty()) {
                event.getTextChannel().sendMessage(
                        Msg.WATCHLIST_EMPTY.literal() + " " + TextFace.CONFOUNDED
                ).queue();
                return;
            }
            for (Object o : list) {
                SubRedditType sub = new SubRedditType((JSONObject) o);
                msg.append(this.createMsgString(sub)).append("\n");
            }
            event.getTextChannel().sendMessage(msg.toString()).queue();
            return;
        }
        JSONObject subJson = config.getSubReddit(args[0]);
        if (subJson == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal() + " " + TextFace.IDK
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                this.createMsgString(new SubRedditType(subJson))
        ).queue();
    }

    private String createMsgString(SubRedditType sub) {
        String active = (sub.getCurrentlyWatched()) ? ":white_check_mark:" : ":x:";
        return "**"+sub.getSubreddit()+"**\n" +
                "sort by: **"+sub.getSortBy()+"**\n" +
                "channel: "+sub.getTextChannelMention()+"\n" +
                "active: "+active+"\n";
    }
}
