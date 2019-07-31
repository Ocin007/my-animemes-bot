package de.ocin007.commands;

import de.ocin007.Bot;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.bot.entities.ApplicationInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractProtectedCommand extends AbstractCommand {

    AbstractProtectedCommand(Prefix cmdPrefix, Cmd cmdStr) {
        super(cmdPrefix, cmdStr);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        String[] literals = msg.split(" ");
        if(literals.length < 2) {
            return;
        }
        if(literals[0].equals(this.cmdPrefix) && literals[1].equals(this.cmdStr)) {
            String[] args = Arrays.copyOfRange(literals, 2, literals.length);
            if(this.isAuthorized(event)) {
                this.execIfArgsValid(event, args);
            } else {
                event.getTextChannel().sendMessage(
                        Msg.UNAUTHORIZED.literal()+" "+TextFace.IDK
                ).queue();
            }
        }
    }

    boolean isBotOwner(MessageReceivedEvent event) {
        ApplicationInfo info = Bot.getShardManager().getApplicationInfo().complete();
        return info.getOwner().equals(event.getAuthor());
    }

    boolean isGuildAdmin(MessageReceivedEvent event) {
        Member member = event.getGuild().getMember(event.getAuthor());
        for (Permission permission : member.getPermissions()) {
            if (permission.compareTo(Permission.ADMINISTRATOR) == 0 && permission.isGuild()) {
                return true;
            }
        }
        return false;
    }

    protected abstract boolean isAuthorized(MessageReceivedEvent event);
}
