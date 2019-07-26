package de.ocin007.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface ServiceCommand {
    void shutdownService(MessageReceivedEvent event);
    void restartService();
}
