package de.ocin007.commands;

import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class AbstractAdminCommand extends AbstractProtectedCommand {

    public AbstractAdminCommand(Prefix cmdPrefix, Cmd cmdStr) {
        super(cmdPrefix, cmdStr);
    }

    @Override
    protected boolean isAuthorized(MessageReceivedEvent event) {
        return this.isBotOwner(event) || this.isGuildAdmin(event);
    }
}
