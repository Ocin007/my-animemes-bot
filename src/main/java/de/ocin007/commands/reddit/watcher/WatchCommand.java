package de.ocin007.commands.reddit.watcher;

import de.ocin007.Bot;
import de.ocin007.builder.reddit.SubRedditPost;
import de.ocin007.commands.AbstractVipCommand;
import de.ocin007.commands.ServiceCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import de.ocin007.http.reddit.RedditApi;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WatchCommand extends AbstractVipCommand implements ServiceCommand {

    private Config config;
    private HashMap<String, ScheduledExecutorService> watchMap;

    public WatchCommand() {
        super(Prefix.VIP, Cmd.WATCH_WATCHER);
        this.watchMap = new HashMap<>();
        this.config = Config.getInstance();
    }

    @Override
    public String getCmdSignature() {
        return Prefix.VIP.literal()+" "+Cmd.WATCH_WATCHER.literal()+" <'start'|'stop'> <r/rubreddit|'all'>\n" +
                Prefix.VIP.literal()+" "+Cmd.WATCH_WATCHER.literal()+" <'sync'>";
    }

    @Override
    public String getCmdDescription() {
        return "starts/stops watching in subreddit for posts\n" +
                "**<'start'|'stop'>** starts/stops watching\n" +
                "**<r/*subreddit*|'all'>** an existing subreddit, has to start with 'r/', or just 'all'\n" +
                "**<'sync'>** stops and restarts every active watcher";
    }

    @Override
    protected boolean argsValid(MessageReceivedEvent event, String[] args) {
        if(args.length == 1 && args[0].equals("sync")) {
            return true;
        }
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
        if(args[0].equals("sync")) {
            this.syncAllWatchers(event);
            return;
        }
        if(args[1].equals("all")) {
            if(args[0].equals("start")) {
                this.startAllWatchers(event);
            } else {
                this.stopAllWatchers(event);
            }
            return;
        }
        JSONObject subJson = this.config.getWatcher(args[1]);
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

    @Override
    public void shutdownService(MessageReceivedEvent event) {
        this.watchMap.forEach((key, watcher) -> watcher.shutdown());
        event.getTextChannel().sendMessage(Msg.PAUSED_WATCHING_ALL.literal()).queue();
        this.watchMap = new HashMap<>();
    }

    @Override
    public void restartService() {
        this.syncAllWatchers(null);
    }

    private void syncAllWatchers(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllWatchers();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(sub.getCurrentlyWatched()) {
                if(this.watchMap.get(sub.getSubreddit()) != null) {
                    this.removeWatcher(sub);
                }
                this.addWatcher(sub, count);
                count++;
            }
        }
        if(count == 0) {
            this.sendMsg(event, Msg.NOT_WATCHING_ALL.literal() + " " + TextFace.SHAME);
            return;
        }
        this.sendMsg(event, "**(+"+count+")** "+Msg.RESTART_WATCHING.literal() + " " + TextFace.WATCHING);
    }

    private void sendMsg(MessageReceivedEvent event, String msg) {
        if(event == null) {
            System.out.println(msg);
        } else {
            event.getTextChannel().sendMessage(msg).queue();
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
        JSONArray list = this.config.getAllWatchers();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(!sub.getCurrentlyWatched()) {
                this.addWatcher(sub, count);
                count++;
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
        JSONArray list = this.config.getAllWatchers();
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

    private void addWatcher(SubRedditType sub, Integer initDelay) {
        sub.setCurrentlyWatched(true);
        this.config.setWatcher(sub.getSubreddit(), sub);
        ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor();
        exService.scheduleAtFixedRate(
                this.getWatcherFunction(sub.getSubreddit()), initDelay, 15, TimeUnit.MINUTES
        );
        this.watchMap.put(sub.getSubreddit(), exService);
    }

    private void addWatcher(SubRedditType sub) {
        this.addWatcher(sub, 0);
    }

    private void removeWatcher(SubRedditType sub) {
        this.watchMap.get(sub.getSubreddit()).shutdown();
        this.watchMap.remove(sub.getSubreddit());
        sub.setCurrentlyWatched(false);
        this.config.setWatcher(sub.getSubreddit(), sub);
    }

    private Runnable getWatcherFunction(String subName) {
        return () -> {
            Config config = Config.getInstance();
            SubRedditType sub = new SubRedditType(config.getWatcher(subName));
            TextChannel channel = Bot.getShardManager().getTextChannelById(sub.getTextChannel());
            try {
                RedditApi api = new RedditApi();
                JSONArray posts;
                try {
                    posts = api.getPosts(sub, 100);
                    if(posts == null) {
                        channel.sendMessage(
                                "@here "+Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                        ).queue();
                        return;
                    }
                    System.out.println("Watch    ["+new Date()+"] "+subName+": "+posts.size());
                } catch (Exception e) {
                    channel.sendMessage(
                            "@here "+Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                    ).queue();
                    e.printStackTrace();
                    return;
                }
                int count = 0;
                JSONArray fallback = sub.getFallback();
                for (int i = posts.size() - 1; i >= 0; i--) {
                    JSONObject o = (JSONObject) posts.get(i);
                    JSONObject post = (JSONObject) o.get("data");
                    if(count == 5) {
                        break;
                    } else if(!(boolean)post.get("stickied")) {
                        channel.sendMessage(new SubRedditPost(post, sub).toString()).queue();
                        if(fallback.size() > 3) {
                            fallback.remove(0);
                        }
                        fallback.add(sub.getLastPostId());
                        sub.setLastPostId((String) post.get("name"));
                        sub.setTimestamp(
                                new Double(post.get("created_utc").toString()).longValue()
                        );
                        count++;
                    }
                }
                sub.setFallback(fallback);
                config.setWatcher(subName, sub);
            } catch (Exception e) {
                channel.sendMessage(
                        "@here **"+subName+"**: "+Msg.STOP_WATCHING.literal()+"\n" +
                                Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                ).queue();
                System.out.println(subName+": Watcher stopped");
                e.printStackTrace();
                this.removeWatcher(sub);
            }
        };
    }
}
