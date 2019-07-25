package de.ocin007.commands.reddit;

import de.ocin007.Bot;
import de.ocin007.commands.AbstractCommand;
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
            Config config = Config.getInstance();
            SubRedditType sub = new SubRedditType(config.getSubReddit(subName));
            TextChannel channel = Bot.getShardManager().getTextChannelById(sub.getTextChannel());
            try {
                RedditApi api = new RedditApi();
                JSONArray posts;
                try {
                    if(sub.getLastPostId() == null) {
                        posts = api.getPosts(sub, 100);
                    } else {
                        posts = api.getPosts(sub, 10);
                    }
                    if(posts == null) {
                        channel.sendMessage(
                                "@here "+Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                        ).queue();
                        return;
                    }
                } catch (Exception e) {
                    channel.sendMessage(
                            "@here "+Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                    ).queue();
                    e.printStackTrace();
                    return;
                }
                int count = 0;
                for (int i = posts.size() - 1; i >= 0; i--) {
                    JSONObject o = (JSONObject) posts.get(i);
                    JSONObject post = (JSONObject) o.get("data");
                    if(count == 5) {
                        break;
                    } else if(!(boolean)post.get("stickied")) {
                        channel.sendMessage(this.createPostStr(post, sub)).queue();
                        sub.setLastPostId((String) post.get("name"));
                        sub.setTimestamp(
                                new Double(post.get("created_utc").toString()).longValue()
                        );
                        count++;
                    }
                }
                config.setSubReddit(subName, sub);
            } catch (Exception e) {
                channel.sendMessage(
                        "@here **"+subName+"**: "+Msg.STOP_WATCHING.literal()+"\n" +
                                Msg.ERROR.literal()+" "+TextFace.TABLE_FLIP
                ).queue();
                e.printStackTrace();
                this.removeWatcher(sub);
            }
        };
    }

    private String createPostStr(JSONObject post, SubRedditType sub) {
        String type = this.getPostType(post);
        Long diff = (System.currentTimeMillis()/1000) - new Double(post.get("created_utc").toString()).longValue();
        String sortBy = this.getSortByEmote(sub.getSortBy());
        return  type+" **"+post.get("title")+"**  **|**  " +
                sortBy+" "+sub.getSubreddit()+"/"+sub.getSortBy()+"  **|**  " +
                ":arrow_up: "+post.get("ups")+"  **|**  " +
                ":speech_balloon: "+post.get("num_comments")+"\n" +
                ":clock1: *posted "+this.timestampDiffToStr(diff)+" ago*\n\n" +
                RedditApi.getApiBaseUrl()+post.get("permalink");
    }

    private String getSortByEmote(String sortBy) {
        String emote;
        switch (sortBy) {
            case "hot": emote = ":fire:"; break;
            case "new": emote = ":star2:"; break;
            case "rising": emote = ":chart_with_upwards_trend:"; break;
            default: emote = ":question:";
        }
        return emote;
    }

    private String getPostType(JSONObject post) {
        String type;
        if(post.get("post_hint") != null) {
            switch ((String)post.get("post_hint")) {
                case "image": type = ":camera:"; break;
                case "hosted:video": type = ":film_frames:"; break;
                case "self": type = ":page_facing_up:"; break;
                case "link": type = ":link:"; break;
                default: type = ":question:";
            }
        } else {
            if((boolean)post.get("is_video")) {
                type = ":film_frames:";
            } else if((boolean)post.get("is_self")) {
                type = ":page_facing_up:";
            } else {
                type = ":question:";
            }
        }
        return type;
    }

    private String timestampDiffToStr(Long diff) {
        Long days = diff / 86400;
        if(days > 1) {
            return days +" days";
        } else if(days == 1) {
            return days +" day";
        }
        Long hours = diff / 3600;
        if(hours > 1) {
            return hours +" hours";
        } else if(hours == 1) {
            return hours +" hour";
        }
        Long minutes = diff / 60;
        if(minutes > 1) {
            return minutes +" minutes";
        } else if(minutes == 1) {
            return minutes +" minute";
        }
        if(diff > 1) {
            return diff +" seconds";
        } else {
            return "1 second";
        }
    }
}