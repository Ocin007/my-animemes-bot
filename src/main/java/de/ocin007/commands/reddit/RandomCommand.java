package de.ocin007.commands.reddit;

import de.ocin007.builder.reddit.SubRedditPost;
import de.ocin007.commands.AbstractCommand;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import de.ocin007.http.reddit.RedditApi;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RandomCommand extends AbstractCommand {

    public RandomCommand() {
        super(Prefix.GENERAL, Cmd.RANDOM);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.RANDOM.literal();
    }

    @Override
    public String getCmdDescription() {
        return "gets a random post from a random subreddit";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws Exception {
        event.getTextChannel().sendMessage(
                new SubRedditPost(new RedditApi().getRandomPost()).toString()
        ).queue();
    }
}
