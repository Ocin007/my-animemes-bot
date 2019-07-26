package de.ocin007.builder.reddit;

import de.ocin007.config.types.SubRedditType;
import de.ocin007.http.reddit.RedditApi;
import org.json.simple.JSONObject;

public class SubRedditPost {

    private JSONObject post;
    private SubRedditType sub;

    public SubRedditPost(JSONObject post, SubRedditType sub) {
        this.post = post;
        this.sub = sub;
    }

    public SubRedditPost(JSONObject post) {
        this.post = post;
        this.sub = new SubRedditType(
                (String) post.get("subreddit_name_prefixed"),
                "random",
                null,
                null,
                null,
                null
        );
    }

    @Override
    public String toString() {
        String type = this.getPostType(this.post);
        Long diff = (System.currentTimeMillis()/1000) - new Double(this.post.get("created_utc").toString()).longValue();
        String sortBy = this.getSortByEmote(this.sub.getSortBy());
        return  type+" **"+this.post.get("title")+"**  **|**  " +
                sortBy+" "+this.sub.getSubreddit()+"/"+this.sub.getSortBy()+"  **|**  " +
                ":arrow_up: "+this.post.get("ups")+"  **|**  " +
                ":speech_balloon: "+this.post.get("num_comments")+"\n" +
                ":clock1: *posted "+this.timestampDiffToStr(diff)+" ago*\n\n" +
                RedditApi.getApiBaseUrl()+this.post.get("permalink");
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
                case "rich:video": type = ":film_frames:"; break;
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
