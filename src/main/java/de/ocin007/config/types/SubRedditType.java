package de.ocin007.config.types;

import de.ocin007.Bot;
import org.json.simple.JSONObject;

public class SubRedditType {

    public static final String SORT_BY_NEW = "new";
    public static final String SORT_BY_HOT = "hot";
    public static final String SORT_BY_RISING = "rising";

    private String subreddit;//
    private String sortBy;//
    private String lastPrinted;
    private Long timestamp;
    private Boolean currentlyWatched;
    private String textChannel;//

    public SubRedditType(
            String subreddit,
            String sortBy,
            String lastPrinted,
            Long timestamp,
            Boolean currentlyWatched,
            String textChannel
    ) {

        this.subreddit = subreddit;
        this.sortBy = sortBy;
        this.lastPrinted = lastPrinted;
        this.timestamp = timestamp;
        this.currentlyWatched = currentlyWatched;
        this.textChannel = textChannel;
    }

    public SubRedditType(JSONObject obj) {
        this.subreddit = (String)obj.get("subreddit");
        this.sortBy = (String)obj.get("sortBy");
        this.lastPrinted = (String)obj.get("lastPrinted");
        this.timestamp = (Long)obj.get("timestamp");
        this.currentlyWatched = (Boolean)obj.get("currentlyWatched");
        this.textChannel = (String)obj.get("textChannel");
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getLastPrinted() {
        return lastPrinted;
    }

    public void setLastPrinted(String lastPrinted) {
        this.lastPrinted = lastPrinted;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getCurrentlyWatched() {
        return currentlyWatched;
    }

    public void setCurrentlyWatched(Boolean currentlyWatched) {
        this.currentlyWatched = currentlyWatched;
    }

    public String getTextChannel() {
        return textChannel;
    }

    public String getTextChannelMention() {
        return Bot.getShardManager().getTextChannelById(textChannel).getAsMention();
    }

    public void setTextChannel(String textChannel) {
        this.textChannel = textChannel;
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("subreddit", this.subreddit);
        obj.put("sortBy", this.sortBy);
        obj.put("lastPrinted", this.lastPrinted);
        obj.put("timestamp", this.timestamp);
        obj.put("currentlyWatched", this.currentlyWatched);
        obj.put("textChannel", this.textChannel);
        return obj;
    }
}
