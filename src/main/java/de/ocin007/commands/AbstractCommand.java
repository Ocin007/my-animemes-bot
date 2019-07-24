package de.ocin007.commands;

import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public abstract class AbstractCommand extends ListenerAdapter {

    final String cmdPrefix;
    final String cmdStr;

    public AbstractCommand(Prefix cmdPrefix, Cmd cmdStr) {
        this.cmdPrefix = cmdPrefix.literal();
        this.cmdStr = cmdStr.literal();
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
            this.execIfArgsValid(event, args);
        }
    }

    void execIfArgsValid(MessageReceivedEvent event, String[] args) {
        if(this.argsValid(args)) {
            try {
                this.execute(event, args);
            } catch (Exception e) {
                e.printStackTrace();
                event.getTextChannel().sendMessage(
                        Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP+"\n" +
                                "||" + e.getMessage() + "||"
                ).queue();
            }
        } else {
            event.getTextChannel().sendMessage(
                    Msg.INVALID_ARGS.literal()+" "+TextFace.CRY
            ).queue();
        }
    }

    public abstract String getCmdSignature();

    public abstract String getCmdDescription();

    protected abstract boolean argsValid(String[] args);

    abstract public void execute(MessageReceivedEvent event, String[] args) throws Exception;
}
