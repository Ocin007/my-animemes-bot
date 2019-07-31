package de.ocin007.commands.reddit;

import de.ocin007.commands.AbstractOwnerCommand;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import de.ocin007.http.reddit.RedditApi;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AuthorizeCommand extends AbstractOwnerCommand {

    public AuthorizeCommand() {
        super(Prefix.OWNER, Cmd.AUTHORIZE);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.OWNER.literal()+" "+ Cmd.AUTHORIZE.literal()+ " <code>";
    }

    @Override
    public String getCmdDescription() {
        return "gets the access- and refresh-token from reddit api.\n" +
                "**<code>** from **" +
                Cmd.GET_AUTH_URL.literal() +
                "**";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        return args.length == 1;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if(!event.isFromType(ChannelType.PRIVATE)) {
            event.getTextChannel().sendMessage(
                    Msg.USE_PRIVATE.literal()+" "+TextFace.REALLY
            ).queue();
            return;
        }
        RedditApi api = new RedditApi();
        boolean success = api.authorize(args[0]);
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        if(success) {
            channel.sendMessage(
                    Msg.SUCCESS.literal()+" "+TextFace.HAPPY
            ).queue();
        } else {
            channel.sendMessage(
                    Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
            ).queue();
        }
    }
}
