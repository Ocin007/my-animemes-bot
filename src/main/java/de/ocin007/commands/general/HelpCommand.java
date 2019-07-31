package de.ocin007.commands.general;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.LinkedList;

public class HelpCommand extends AbstractCommand {

    private LinkedList<AbstractCommand> cmdList;
    private String msg;

    public HelpCommand(LinkedList<AbstractCommand> cmdList) {
        super(Prefix.GENERAL, Cmd.HELP);
        this.cmdList = cmdList;
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.HELP.literal()+" [command]";
    }

    @Override
    public String getCmdDescription() {
        return "**[command]** *optional: prints out the specific help for the given command. " +
                "If none is given, an overview with all available commands will be printed*";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if(args.length == 0) {
            return true;
        }
        if(args.length > 1) {
            return false;
        }
        for (AbstractCommand cmd : this.cmdList) {
            if (cmd.getCmdStr().equals(args[0])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if(args.length == 0) {
            this.printCmd(event);
        } else {
            this.printCmd(event, args[0]);
        }
    }

    private void printCmd(MessageReceivedEvent event, String arg) {
        this.cmdList.forEach(cmd -> {
            if(!cmd.getCmdStr().equals(arg)) {
                return;
            }
            String msg = "```" +
                    cmd.getCmdSignature() + "```" +
                    cmd.getCmdDescription() + "\n\n";
            event.getTextChannel().sendMessage(msg).queue();
        });
    }

    private void printCmd(MessageReceivedEvent event) {
        this.msg = "```All available commands ("+this.cmdList.size()+"):\n\n" +
                "prefix command <args...|'const values'...> [optional args]\n\n";
        this.cmdList.forEach(cmd -> this.msg += cmd.getCmdSignature()+"\n");
        this.msg += "```";
        event.getTextChannel().sendMessage(this.msg).queue();
    }
}
