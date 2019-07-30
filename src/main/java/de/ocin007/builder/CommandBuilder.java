package de.ocin007.builder;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.commands.DefaultCommand;
import de.ocin007.commands.general.*;
import de.ocin007.commands.reddit.*;
import de.ocin007.commands.reddit.downloader.AddDownloaderCommand;
import de.ocin007.commands.reddit.downloader.EditDownloaderCommand;
import de.ocin007.commands.reddit.downloader.RemoveDownloaderCommand;
import de.ocin007.commands.reddit.downloader.ShowDownloaderListCommand;
import de.ocin007.commands.reddit.watcher.AddWatcherCommand;
import de.ocin007.commands.reddit.watcher.EditWatcherCommand;
import de.ocin007.commands.reddit.watcher.RemoveWatcherCommand;
import de.ocin007.commands.reddit.watcher.ShowWatchListCommand;
import de.ocin007.events.ReadyEventHandler;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;

import java.util.Collection;
import java.util.LinkedList;

public class CommandBuilder {

    private DefaultShardManagerBuilder builder;
    private Collection<Object> listeners;
    private LinkedList<AbstractCommand> listenersToPrint;

    public CommandBuilder(DefaultShardManagerBuilder builder) {
        this.builder = builder;
        this.listeners = new LinkedList<>();
        this.listenersToPrint = new LinkedList<>();
        this.appendAllListeners();
        this.listeners.addAll(this.listenersToPrint);
        this.listeners.add(new DefaultCommand());
        this.listeners.add(new ReadyEventHandler(this.listenersToPrint));
    }

    private void appendAllListeners() {
        this.listenersToPrint.add(new ShutdownCommand(this.listenersToPrint));
        this.listenersToPrint.add(new HelpCommand(this.listenersToPrint));
        this.listenersToPrint.add(new AuthUrlCommand());
        this.listenersToPrint.add(new AuthorizeCommand());
        this.listenersToPrint.add(new AddWatcherCommand());
        this.listenersToPrint.add(new AddDownloaderCommand());
        this.listenersToPrint.add(new RemoveWatcherCommand());
        this.listenersToPrint.add(new RemoveDownloaderCommand());
        this.listenersToPrint.add(new EditWatcherCommand());
        this.listenersToPrint.add(new EditDownloaderCommand());
        this.listenersToPrint.add(new ShowWatchListCommand());
        this.listenersToPrint.add(new ShowDownloaderListCommand());
        this.listenersToPrint.add(new WatchCommand());
        this.listenersToPrint.add(new RandomCommand());
    }

    public void build() {
        this.builder.addEventListeners(this.listeners);
    }
}
