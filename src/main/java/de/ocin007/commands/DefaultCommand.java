package de.ocin007.commands;

import de.ocin007.config.Config;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;


public class DefaultCommand extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        String[] literals = msg.split(" ");
        if(literals.length == 0) {
            return;
        }
        if(!this.isPrefix(literals[0])) {
            return;
        }
        if(literals.length > 1) {
            for (Cmd cmd : Cmd.values()) {
                if(literals[1].equals(cmd.literal())) {
                    return;
                }
            }
        }
        event.getTextChannel().sendMessage(
                Msg.CMD_NOT_VALID.literal()+" "+TextFace.WHAT+"\n" +
                        Config.getInstance().getRandomGif()
        ).queue();
    }

    private boolean isPrefix(String literal) {
        for (Prefix pre : Prefix.values()) {
            if(literal.equals(pre.literal())) {
                return true;
            }
        }
        return false;
    }
}
