package de.ocin007.commands;

import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public abstract class AbstractVipCommand extends AbstractProtectedCommand {

    public AbstractVipCommand(Prefix cmdPrefix, Cmd cmdStr) {
        super(cmdPrefix, cmdStr);
    }

    @Override
    protected boolean isAuthorized(MessageReceivedEvent event) {
        if(this.isBotOwner(event) || this.isGuildAdmin(event)) {
            return true;
        }
        Config config = Config.getInstance();
        if(config.getAllVipRoles().isEmpty()) {
            return true;
        }
        List<Role> roles = event.getMember().getRoles();
        for (Role role : roles) {
            if(config.isVipRole(role.getId())) {
                return true;
            }
        }
        return false;
    }
}
