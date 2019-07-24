package de.ocin007.commands;

import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
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

    protected abstract boolean isAuthorized(MessageReceivedEvent event);
}
