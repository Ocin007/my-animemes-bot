package de.ocin007.commands;

import de.ocin007.config.Config;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;


public class DefaultCommand extends ListenerAdapter {

    private LinkedList<AbstractCommand> cmdList;

    public DefaultCommand(LinkedList<AbstractCommand> cmdList) {
        this.cmdList = cmdList;
    }

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
            for (AbstractCommand cmd : this.cmdList) {
                if(literals[0].equals(cmd.getCmdPrefix()) &&
                        literals[1].equals(cmd.getCmdStr())) {
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
