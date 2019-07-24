package de.ocin007.commands.reddit;

import de.ocin007.commands.AbstractCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatchCommand extends AbstractCommand {

    private Config config;
    private HashMap<String, ScheduledExecutorService> watchMap;

    public WatchCommand() {
        super(Prefix.GENERAL, Cmd.WATCH);
        this.watchMap = new HashMap<>();
        this.config = Config.getInstance();
    }

    @Override
    public String getCmdSignature() {
        return Prefix.GENERAL.literal()+" "+Cmd.WATCH.literal()+" <'start'|'stop'> <r/rubreddit|'all'>";
    }

    @Override
    public String getCmdDescription() {
        return "starts/stops watching in subreddit for posts\n" +
                "**<'start'|'stop'>** starts/stops watching\n" +
                "**<r/*subreddit*|'all'>** an existing subreddit, has to start with 'r/', or just 'all'";
    }

    @Override
    protected boolean argsValid(String[] args) {
        if(args.length != 2) {
            return false;
        }
        if(!args[0].equals("start") && !args[0].equals("stop")) {
            return false;
        }
        return args[1].equals("all") || args[1].startsWith("r/");
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if(args[1].equals("all")) {
            if(args[0].equals("start")) {
                this.startAllWatchers(event);
            } else {
                this.stopAllWatchers(event);
            }
            return;
        }
        JSONObject subJson = this.config.getSubReddit(args[1]);
        if (subJson == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal() + " " + TextFace.IDK
            ).queue();
            return;
        }
        if(args[0].equals("start")) {
            this.startWatcher(event, new SubRedditType(subJson));
        } else {
            this.stopWatcher(event, new SubRedditType(subJson));
        }
    }

    private void startWatcher(MessageReceivedEvent event, SubRedditType sub) {
        if(sub.getCurrentlyWatched()) {
            event.getTextChannel().sendMessage(
                    Msg.ALR_WATCHING.literal() + " " + TextFace.WATCHING
            ).queue();
            return;
        }
        this.addWatcher(sub);
        event.getTextChannel().sendMessage(
                Msg.START_WATCHING.literal() + " " + TextFace.WATCHING
        ).queue();
    }

    private void stopWatcher(MessageReceivedEvent event, SubRedditType sub) {
        if(!sub.getCurrentlyWatched()) {
            event.getTextChannel().sendMessage(
                    Msg.NOT_WATCHING.literal() + " " + TextFace.SERIOUS
            ).queue();
            return;
        }
        this.removeWatcher(sub);
        event.getTextChannel().sendMessage(
                Msg.STOP_WATCHING.literal() + " " + TextFace.SERIOUS
        ).queue();
    }

    private void startAllWatchers(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllSubReddits();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(!sub.getCurrentlyWatched()) {
                count++;
                this.addWatcher(sub);
            }
        }
        if(count == 0) {
            event.getTextChannel().sendMessage(
                    Msg.ALR_WATCHING_ALL.literal() + " " + TextFace.WATCHING
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                "**(+"+count+")** "+Msg.START_WATCHING_ALL.literal() + " " + TextFace.WATCHING
        ).queue();
    }

    private void stopAllWatchers(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllSubReddits();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(sub.getCurrentlyWatched()) {
                count++;
                this.removeWatcher(sub);
            }
        }
        if(count == 0) {
            event.getTextChannel().sendMessage(
                    Msg.NOT_WATCHING_ALL.literal() + " " + TextFace.SHAME
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                "**(-"+count+")** "+Msg.STOP_WATCHING_ALL.literal() + " " + TextFace.SERIOUS
        ).queue();
    }

    private void addWatcher(SubRedditType sub) {
        sub.setCurrentlyWatched(true);
        this.config.setSubReddit(sub.getSubreddit(), sub);
        ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor();
        exService.scheduleAtFixedRate(
                this.getWatcherFunction(sub.getSubreddit()), 0, 10, TimeUnit.MINUTES
        );
        this.watchMap.put(sub.getSubreddit(), exService);
    }

    private void removeWatcher(SubRedditType sub) {
        this.watchMap.get(sub.getSubreddit()).shutdown();
        this.watchMap.remove(sub.getSubreddit());
        sub.setCurrentlyWatched(false);
        this.config.setSubReddit(sub.getSubreddit(), sub);
    }

    private Runnable getWatcherFunction(String subName) {
        return () -> {
            System.out.println(subName);
        };
    }
}
