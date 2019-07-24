package de.ocin007.http.reddit;


import de.ocin007.config.Config;
import de.ocin007.enums.routes.reddit.Route;
import de.ocin007.http.HttpHandler;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

import java.util.UUID;

public class RedditApi {

    private final static String API_BASE_URL = "https://www.reddit.com";
    private final static String API_OAUTH_BASE_URL = "https://oauth.reddit.com";
    private final static String REDIRECT_URI = "http://localhost";
    private final static Long REFRESH_TIME_SPAN = 3500000L;

    public String getAuthorisationUrl() {
        HttpHandler http = new HttpHandler(API_BASE_URL + Route.AUTHORIZE);
        http.addParam("client_id", Config.getInstance().getConfig("clientId"));
        http.addParam("response_type", "code");
        http.addParam("state", UUID.randomUUID().toString());
        http.addParam("redirect_uri", REDIRECT_URI);
        http.addParam("duration", "permanent");
        http.addParam("scope", "read");
        return http.getQueryURL();
    }

    public boolean authorize(String code) {
        JSONObject obj = this.getAccessToken(code);
        Config config = Config.getInstance();
        if(obj.get("access_token") != null && obj.get("refresh_token") != null) {
            config.setConfig("redditToken", (String)obj.get("access_token"));
            config.setConfig("redditRefreshToken", (String)obj.get("refresh_token"));
            config.setConfig("redditRefreshTimestamp", String.valueOf(System.currentTimeMillis()));
            return true;
        }
        return false;
    }

    public boolean subredditExists(String subreddit) throws Exception {
        HttpHandler http = new HttpHandler(API_OAUTH_BASE_URL+"/"+subreddit+Route.ABOUT);
        this.addBearer(http);
        JSONObject res = http.get();
        if(res.get("kind") != null) {
            String kind = (String)res.get("kind");
            return kind.equals("t5");
        }
        return false;
    }

    private JSONObject getAccessToken(String code) {
        HttpHandler http = new HttpHandler(API_BASE_URL + Route.ACCESS_TOKEN);
        http.addParam("grant_type", "authorization_code");
        http.addParam("code", code);
        http.addParam("redirect_uri", REDIRECT_URI);
        this.addAuthHeaders(http);
        return http.post();
    }

    private JSONObject refreshAccessToken() {
        HttpHandler http = new HttpHandler(API_BASE_URL + Route.ACCESS_TOKEN);
        http.addParam("grant_type", "refresh_token");
        http.addParam("refresh_token", Config.getInstance().getConfig("redditRefreshToken"));
        this.addAuthHeaders(http);
        return http.post();
    }

    private void addAuthHeaders(HttpHandler http) {
        http.addHeader("User-Agent", "windows:discord-bot.my-animemes-bot:1.0-SNAPSHOT (by /u/Ocin007)");
        String credentials = Config.getInstance().getConfig("clientId") + ":" + Config.getInstance().getConfig("clientSecret");
        String encodedCredentials = Base64.encodeBase64String(credentials.getBytes());
        http.addHeader("Authorization", "Basic "+encodedCredentials);
    }

    private void addBearer(HttpHandler http) throws Exception {
        Config config = Config.getInstance();
        Long now = System.currentTimeMillis();
        Long lastRefreshed = Long.parseLong(config.getConfig("redditRefreshTimestamp"));
        String token;
        if(now - lastRefreshed > REFRESH_TIME_SPAN) {
            JSONObject res = this.refreshAccessToken();
            if(res.get("access_token") == null) {
                throw new Exception(res.toJSONString());
            }
            token = res.get("access_token").toString();
            config.setConfig("redditToken", token);
            config.setConfig("redditRefreshTimestamp", String.valueOf(System.currentTimeMillis()));
        } else {
            token = config.getConfig("redditToken");
        }
        http.addHeader("Authorization", "Bearer "+token);
    }
}
