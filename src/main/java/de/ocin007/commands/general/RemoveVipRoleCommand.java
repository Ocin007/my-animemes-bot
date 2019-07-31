package de.ocin007.commands.general;

import de.ocin007.commands.AbstractAdminCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RemoveVipRoleCommand extends AbstractAdminCommand {

    public RemoveVipRoleCommand() {
        super(Prefix.ADMIN, Cmd.REMOVE_VIP_ROLE);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal()+" "+Cmd.REMOVE_VIP_ROLE.literal()+" <role ID>";
    }

    @Override
    public String getCmdDescription() {
        return "removes a role from the vip-roles. these roles can execute all *"+Prefix.VIP.literal()+"* commands." +
                "**<role ID>** a role";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        return args.length == 1;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        String roleMention = event.getGuild().getRoleById(args[0]).getAsMention();
        if(config.removeVipRole(args[0])) {
            event.getTextChannel().sendMessage(
                    roleMention+": "+Msg.ROLE_RM_FROM_VIP.literal()+" "+TextFace.SERIOUS
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                Msg.ROLE_ALR_RM_FROM_VIP.literal() + " " + TextFace.IDK
        ).queue();
    }
}
