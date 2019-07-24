package de.ocin007.commands;

import de.ocin007.Bot;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import net.dv8tion.jda.bot.entities.ApplicationInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class AbstractOwnerCommand extends AbstractProtectedCommand {

    public AbstractOwnerCommand(Prefix cmdPrefix, Cmd cmdStr) {
        super(cmdPrefix, cmdStr);
    }

    @Override
    protected boolean isAuthorized(MessageReceivedEvent event) {
        ApplicationInfo info = Bot.getShardManager().getApplicationInfo().complete();
        return info.getOwner().equals(event.getAuthor());
    }
}
