package de.ocin007.commands.reddit.downloader;

import de.ocin007.commands.AbstractVipCommand;
import de.ocin007.commands.ServiceCommand;
import de.ocin007.config.Config;
import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.Cmd;
import de.ocin007.enums.Msg;
import de.ocin007.enums.Prefix;
import de.ocin007.enums.TextFace;
import de.ocin007.http.reddit.RedditApi;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadCommand extends AbstractVipCommand implements ServiceCommand {

    private Config config;
    private HashMap<String, ScheduledExecutorService> downloadMap;
    private final static String PATH_TO_DOWNLOAD_FOLDER = "src\\main\\resources\\downloads";

    public DownloadCommand() {
        super(Prefix.VIP, Cmd.WATCH_DOWNLOADER);
        this.downloadMap = new HashMap<>();
        this.config = Config.getInstance();
    }

    @Override
    public String getCmdSignature() {
        return Prefix.VIP.literal()+" "+Cmd.WATCH_DOWNLOADER.literal()+" <'start'|'stop'> <r/rubreddit|'all'>\n" +
                Prefix.VIP.literal()+" "+Cmd.WATCH_DOWNLOADER.literal()+" <'sync'>";
    }

    @Override
    public String getCmdDescription() {
        return "starts/stops downloading images from subreddit\n" +
                "**<'start'|'stop'>** starts/stops downloading\n" +
                "**<r/*subreddit*|'all'>** an existing subreddit, has to start with 'r/', or just 'all'\n" +
                "**<'sync'>** stops and restarts every active downloader";
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
            this.syncAllDownloaders(event);
            return;
        }
        if(args[1].equals("all")) {
            if(args[0].equals("start")) {
                this.startAllDownloaders(event);
            } else {
                this.stopAllDownloaders(event);
            }
            return;
        }
        JSONObject subJson = this.config.getDownloader(args[1]);
        if (subJson == null) {
            event.getTextChannel().sendMessage(
                    Msg.SUB_NOT_EXIST.literal() + " " + TextFace.IDK
            ).queue();
            return;
        }
        if(args[0].equals("start")) {
            this.startDownloader(event, new SubRedditType(subJson));
        } else {
            this.stopDownloader(event, new SubRedditType(subJson));
        }
    }

    @Override
    public void shutdownService(MessageReceivedEvent event) {
        this.downloadMap.forEach((key, downloader) -> downloader.shutdown());
        event.getTextChannel().sendMessage(Msg.PAUSED_DOWNLOADING_ALL.literal()).queue();
        this.downloadMap = new HashMap<>();
    }

    @Override
    public void restartService() {
        this.syncAllDownloaders(null);
    }

    private void syncAllDownloaders(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllDownloaders();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(sub.getCurrentlyWatched()) {
                if(this.downloadMap.get(sub.getSubreddit()) != null) {
                    this.removeDownloader(sub);
                }
                this.addDownloader(sub, count);
                count++;
            }
        }
        if(count == 0) {
            this.sendMsg(event, Msg.NOT_DOWNLOADING_ALL.literal() + " " + TextFace.SHAME);
            return;
        }
        this.sendMsg(event, "**(+"+count+")** "+Msg.RESTART_DOWNLOADING.literal() + " " + TextFace.WATCHING);
    }

    private void sendMsg(MessageReceivedEvent event, String msg) {
        if(event == null) {
            System.out.println(msg);
        } else {
            event.getTextChannel().sendMessage(msg).queue();
        }
    }

    private void startDownloader(MessageReceivedEvent event, SubRedditType sub) {
        if(sub.getCurrentlyWatched()) {
            event.getTextChannel().sendMessage(
                    Msg.ALR_DOWNLOADING.literal() + " " + TextFace.WATCHING
            ).queue();
            return;
        }
        this.addDownloader(sub);
        event.getTextChannel().sendMessage(
                Msg.START_DOWNLOADING.literal() + " " + TextFace.WATCHING
        ).queue();
    }

    private void stopDownloader(MessageReceivedEvent event, SubRedditType sub) {
        if(!sub.getCurrentlyWatched()) {
            event.getTextChannel().sendMessage(
                    Msg.NOT_DOWNLOADING.literal() + " " + TextFace.SERIOUS
            ).queue();
            return;
        }
        this.removeDownloader(sub);
        event.getTextChannel().sendMessage(
                Msg.STOP_DOWNLOADING.literal() + " " + TextFace.SERIOUS
        ).queue();
    }

    private void startAllDownloaders(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllDownloaders();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(!sub.getCurrentlyWatched()) {
                this.addDownloader(sub, count);
                count++;
            }
        }
        if(count == 0) {
            event.getTextChannel().sendMessage(
                    Msg.ALR_DOWNLOADING_ALL.literal() + " " + TextFace.WATCHING
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                "**(+"+count+")** "+Msg.START_DOWNLOADING_ALL.literal() + " " + TextFace.WATCHING
        ).queue();
    }

    private void stopAllDownloaders(MessageReceivedEvent event) {
        JSONArray list = this.config.getAllDownloaders();
        Integer count = 0;
        for (Object o : list) {
            SubRedditType sub = new SubRedditType((JSONObject) o);
            if(sub.getCurrentlyWatched()) {
                count++;
                this.removeDownloader(sub);
            }
        }
        if(count == 0) {
            event.getTextChannel().sendMessage(
                    Msg.NOT_DOWNLOADING_ALL.literal() + " " + TextFace.SHAME
            ).queue();
            return;
        }
        event.getTextChannel().sendMessage(
                "**(-"+count+")** "+Msg.STOP_DOWNLOADING_ALL.literal() + " " + TextFace.SERIOUS
        ).queue();
    }

    private void addDownloader(SubRedditType sub, Integer initDelay) {
        sub.setCurrentlyWatched(true);
        this.config.setDownloader(sub.getSubreddit(), sub);
        ScheduledExecutorService exService = Executors.newSingleThreadScheduledExecutor();
        exService.scheduleAtFixedRate(
                this.getDownloaderFunction(sub.getSubreddit()), initDelay, 15, TimeUnit.MINUTES
        );
        this.downloadMap.put(sub.getSubreddit(), exService);
    }

    private void addDownloader(SubRedditType sub) {
        this.addDownloader(sub, 0);
    }

    private void removeDownloader(SubRedditType sub) {
        this.downloadMap.get(sub.getSubreddit()).shutdown();
        this.downloadMap.remove(sub.getSubreddit());
        sub.setCurrentlyWatched(false);
        this.config.setDownloader(sub.getSubreddit(), sub);
    }

    private Runnable getDownloaderFunction(String subName) {
        return () -> {
            Config config = Config.getInstance();
            SubRedditType sub = new SubRedditType(config.getDownloader(subName));
            try {
                RedditApi api = new RedditApi();
                JSONArray posts;
                try {
                    posts = api.getPosts(sub, 10);
                    if(posts == null) {
                        return;
                    }
                    System.out.println("Download ["+new Date()+"] "+subName+": "+posts.size());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                for (int i = posts.size() - 1; i >= 0; i--) {
                    JSONObject o = (JSONObject) posts.get(i);
                    JSONObject post = (JSONObject) o.get("data");
                    if(post.get("post_hint") != null && post.get("url") != null) {
                        if(!(boolean)post.get("stickied") && post.get("post_hint").equals("image")) {
                            sub.setLastPostId((String) post.get("name"));
                            sub.setTimestamp(
                                    new Double(post.get("created_utc").toString()).longValue()
                            );
                            this.downloadImage(post.get("url").toString(), subName, post.get("name").toString());
                        }
                    }
                }
                config.setDownloader(subName, sub);
            } catch (Exception e) {
                System.out.println(subName+": Downloader stopped");
                e.printStackTrace();
                this.removeDownloader(sub);
            }
        };
    }

    private void downloadImage(String url, String subName, String postId) {
        try {
            String[] urlPieces = url.split("[.]");
            String imgType = urlPieces[urlPieces.length-1];
            String dir = System.getProperty("user.dir")+"\\"+PATH_TO_DOWNLOAD_FOLDER+"\\"+subName.substring(2);
            boolean success = new File(dir).mkdirs();
            if(success) {
                System.out.println("created new dir for "+subName);
            }
            File file = new File(dir+"\\"+postId+"."+imgType);
            if(!file.createNewFile()) {
                return;
            }
            URL urlObj = new URL(url);
            BufferedImage image = ImageIO.read(urlObj);
            ImageIO.write(image, imgType,file);
        } catch(IOException e) {
            System.out.println(subName+": "+url);
            e.printStackTrace();
        }
    }
}
