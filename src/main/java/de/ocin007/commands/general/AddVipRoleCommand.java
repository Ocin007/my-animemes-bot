package de.ocin007.commands.general;

import de.ocin007.commands.AbstractAdminCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AddVipRoleCommand extends AbstractAdminCommand {

    public AddVipRoleCommand() {
        super(Prefix.ADMIN, Cmd.ADD_VIP_ROLE);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.ADMIN.literal()+" "+Cmd.ADD_VIP_ROLE.literal()+" <role ID>";
    }

    @Override
    public String getCmdDescription() {
        return "adds a role to the vip-roles. these roles can execute all *"+Prefix.VIP.literal()+"* commands.\n" +
                "**<role ID>** a role";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if(args.length != 1) {
            return false;
        }
        return event.getGuild().getRoleById(args[0]) != null;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String roleMention = event.getGuild().getRoleById(args[0]).getAsMention();
        if(Config.getInstance().addVipRole(args[0])) {
            event.getTextChannel().sendMessage(
                    roleMention+": "+Msg.ROLE_ADDED_TO_VIP.literal()+" "+TextFace.HAPPY
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                Msg.ROLE_ALR_ADDED_TO_VIP.literal() + " " + TextFace.IDK
        ).queue();
    }
}
