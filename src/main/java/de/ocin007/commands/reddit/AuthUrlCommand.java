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

public class AuthUrlCommand extends AbstractOwnerCommand {

    private RedditApi api;

    public AuthUrlCommand() {
        super(Prefix.OWNER, Cmd.GET_AUTH_URL);
        this.api = new RedditApi();
    }

    @Override
    public String getCmdSignature() {
        return Prefix.OWNER.literal()+" "+Cmd.GET_AUTH_URL.literal();
    }

    @Override
    public String getCmdDescription() {
        return "returns the url you need to get the " +
                "authorization code used as parameter for **" +
                Cmd.AUTHORIZE.literal() +
                "**";
    }

    @Override
    protected boolean argsValid(String[] args) {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if(!event.isFromType(ChannelType.PRIVATE)) {
            event.getTextChannel().sendMessage(
                    Msg.USE_PRIVATE.literal()+" "+TextFace.REALLY
            ).queue();
            return;
        }
        PrivateChannel channel = event.getAuthor().openPrivateChannel().complete();
        channel.sendMessage(this.api.getAuthorisationUrl()).queue();
    }
}
