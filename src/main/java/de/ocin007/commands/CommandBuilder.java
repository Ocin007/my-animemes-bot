package de.ocin007.commands;

import de.ocin007.commands.general.*;
import de.ocin007.commands.reddit.*;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

import java.util.Collection;
import java.util.LinkedList;

public class CommandBuilder {

    private DefaultShardManagerBuilder builder;
    private Collection<Object> listeners;
    private Collection<AbstractCommand> listenersToPrint;

    public CommandBuilder(DefaultShardManagerBuilder builder) {
        this.builder = builder;
        this.listeners = new LinkedList<>();
        this.listenersToPrint = new LinkedList<>();
        this.appendAllListeners();
        this.listeners.addAll(this.listenersToPrint);
    }

    private void appendAllListeners() {
        this.listenersToPrint.add(new ShutdownCommand());
        this.listenersToPrint.add(new HelpCommand(this.listenersToPrint));
        this.listenersToPrint.add(new AuthUrlCommand());
        this.listenersToPrint.add(new AuthorizeCommand());
        this.listenersToPrint.add(new AddSubRedditCommand());
        this.listenersToPrint.add(new RemoveSubRedditCommand());
        this.listenersToPrint.add(new EditSubRedditCommand());
        this.listenersToPrint.add(new ShowWatchListCommand());
    }

    public void build() {
        this.builder.addEventListeners(this.listeners);
    }
}
