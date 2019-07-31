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
import org.json.simple.JSONObject;


public class AddWatcherCommand extends AbstractAdminCommand {

    public AddWatcherCommand() {
        super(Prefix.ADMIN, Cmd.ADD_WATCHER);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal()+" "+Cmd.ADD_WATCHER.literal()+" <r/subreddit> <'hot'|'new'|'rising'> [textChannel ID]";
    }

    @Override
    public String getCmdDescription() {
        return "adds the subreddit to the watchlist. does nothing if subreddit already exists.\n" +
                "**<r/*subreddit*>**  an existing subreddit, has to start with 'r/'\n" +
                "**<'hot'|'new'|'rising'>** sort subreddit by hot|new|rising\n" +
                "**[textChannel ID]** *optional: the ID of the textChannel where the bot prints out the reddit stuff. " +
                "If not set, the channel where the command got executed will be used*";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if(args.length != 3 && args.length != 2) {
            return false;
        }
        if(!SubRedditType.nameIsValid(args[0])) {
            return false;
        }
        if(!args[1].equals(SubRedditType.SORT_BY_HOT) &&
                !args[1].equals(SubRedditType.SORT_BY_NEW) &&
                !args[1].equals(SubRedditType.SORT_BY_RISING)) {
            return false;
        }
        if(args.length == 3) {
            TextChannel channel = Bot.getShardManager().getTextChannelById(args[2]);
            return channel != null;
        }
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String channel;
        if(args.length == 2) {
            channel = event.getTextChannel().getId();
        } else {
            channel = args[2];
        }
        SubRedditType sub = new SubRedditType(
                args[0],
                args[1],
                null,
                null,
                false,
                channel
        );
        Config config = Config.getInstance();
        JSONObject existingSub = config.getWatcher(sub.getSubreddit());
        if(existingSub == null) {
            config.setWatcher(sub.getSubreddit(), sub);
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
