package de.ocin007.commands.general;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;

public class ShowVipRoleListCommand extends AbstractCommand {

    public ShowVipRoleListCommand() {
        super(Prefix.GENERAL, Cmd.SHOW_VIP_ROLE);
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.SHOW_VIP_ROLE.literal();
    }

    @Override
    public String getCmdDescription() {
        return "shows all vip-roles";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        return args.length == 0;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        Config config = Config.getInstance();
        JSONArray roles = config.getAllVipRoles();
        if(roles.isEmpty()) {
            event.getTextChannel().sendMessage(
                    Msg.NO_VIP_ROLES_TO_SHOW.literal()+" "+TextFace.IDK
            ).queue();
            return;
        }
        StringBuilder msg = new StringBuilder("**These roles are authorized to use *" + Prefix.VIP.literal() + "* commands:** \n```");
        for (Object o : roles) {
            String roleId = (String) o;
            msg.append(event.getGuild().getRoleById(roleId).getName()).append("\n");
        }
        msg.append("```");
        event.getTextChannel().sendMessage(msg.toString()).queue();
    }
}
