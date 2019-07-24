package de.ocin007.commands.general;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Prefix;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collection;

public class HelpCommand extends AbstractCommand {

    private String msg;
    private Collection<AbstractCommand> cmdList;

    public HelpCommand(Collection<AbstractCommand> cmdList) {
        super(Prefix.GENERAL, Cmd.HELP);
        this.cmdList = cmdList;
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.HELP.literal();
    }

    @Override
    public String getCmdDescription() {
        return "prints out all available commands";
    }

    @Override
    protected boolean argsValid(String[] args) {
        return true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        this.msg = "All available commands:\n\n";
        this.cmdList.forEach(cmd -> this.msg += "```"+
                cmd.getCmdSignature()+"```" +
                cmd.getCmdDescription()+"\n\n"
        );
        event.getTextChannel().sendMessage(this.msg).queue();
    }
}
