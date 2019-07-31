package de.ocin007.builder;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.commands.DefaultCommand;
import de.ocin007.commands.general.*;
import de.ocin007.commands.reddit.*;
import de.ocin007.commands.reddit.downloader.*;
import de.ocin007.commands.reddit.watcher.*;
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
        this.listeners.add(new DefaultCommand(this.listenersToPrint));
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
        this.listenersToPrint.add(new DownloadCommand());
        this.listenersToPrint.add(new RandomCommand());
        this.listenersToPrint.add(new AddVipRoleCommand());
        this.listenersToPrint.add(new RemoveVipRoleCommand());
        this.listenersToPrint.add(new ShowVipRoleListCommand());
    }

    public void build() {
        this.builder.addEventListeners(this.listeners);
    }
}
