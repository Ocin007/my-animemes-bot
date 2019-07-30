package de.ocin007.config;

import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.config.ConfigKeys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

public class Config {

    private final static String PATH_TO_CONFIG_PROPERTIES = "src/main/resources/config.properties";
    private final static String PATH_TO_CMD_PROPERTIES = "src/main/resources/commands.properties";
    private final static String PATH_TO_MSG_PROPERTIES = "src/main/resources/messages.properties";
    private final static String PATH_TO_PREFIX_PROPERTIES = "src/main/resources/prefixes.properties";
    private final static String PATH_TO_WATCHERS = "src/main/resources/json/currentlyWatchedSubReddits.json";
    private final static String PATH_TO_DOWNLOADERS = "src/main/resources/json/memeDownloader.json";
    private final static String PATH_TO_DEFAULT_CMD_GIFS = "src/main/resources/json/defaultCommandGifs.json";

    private static Config ourInstance;
    private HashMap<String, Properties> propMap;
    private JSONObject watcherObj;
    private JSONObject downloaderObj;
    private JSONArray gifJsonList;

    public static Config getInstance() {
        if(ourInstance == null) {
            ourInstance = new Config();
        }
        return ourInstance;
    }

    private Config() {
        try {
            this.propMap = new HashMap<>();
            this.watcherObj = new JSONObject();
            this.downloaderObj = new JSONObject();
            this.appendProperty(ConfigKeys.TOKEN.toString(), PATH_TO_CONFIG_PROPERTIES);
            this.appendProperty(ConfigKeys.CMD.toString(), PATH_TO_CMD_PROPERTIES);
            this.appendProperty(ConfigKeys.MSG.toString(), PATH_TO_MSG_PROPERTIES);
            this.appendProperty(ConfigKeys.PREFIX.toString(), PATH_TO_PREFIX_PROPERTIES);
            this.getWatcherJson();
            this.getDownloaderJson();
            this.getDefaultCommandGifs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDefaultCommandGifs() throws Exception {
        this.gifJsonList = (JSONArray) this.getJSONFromPath(Config.PATH_TO_DEFAULT_CMD_GIFS);
    }

    private void getWatcherJson() throws Exception {
        this.watcherObj = (JSONObject) this.getJSONFromPath(Config.PATH_TO_WATCHERS);
    }

    private void getDownloaderJson() throws Exception {
        this.downloaderObj = (JSONObject) this.getJSONFromPath(Config.PATH_TO_DOWNLOADERS);
    }

    private Object getJSONFromPath(String path) throws Exception {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(path);
        return jsonParser.parse(reader);
    }

    private void appendProperty(String key, String path) throws IOException {
        InputStream input = new FileInputStream(path);
        Properties prop = new Properties();
        prop.load(input);
        input.close();
        this.propMap.put(key, prop);
    }

    public String getRandomGif() {
        return (String) this.gifJsonList.get(new Random().nextInt(this.gifJsonList.size()));
    }

    public JSONObject getWatcher(String name) {
        return this.getSubReddit(name, this.watcherObj);
    }

    public JSONObject getDownloader(String name) {
        return this.getSubReddit(name, this.downloaderObj);
    }

    private JSONObject getSubReddit(String name, JSONObject object) {
        JSONArray list = (JSONArray) object.get("subreddits");
        for (Object obj: list) {
            JSONObject subreddit = (JSONObject) obj;
            if((subreddit.get("subreddit")).equals(name)) {
                return subreddit;
            }
        }
        return null;
    }

    public JSONArray getAllWatchers() {
        return (JSONArray) this.watcherObj.get("subreddits");
    }

    public JSONArray getAllDownloaders() {
        return (JSONArray) this.downloaderObj.get("subreddits");
    }

    public void setWatcher(String name, SubRedditType value) {
        JSONArray list = (JSONArray) this.watcherObj.get("subreddits");
        this.setSubReddit(name, value, list);
        this.watcherObj.put("subreddits", list);
        this.updateSubRedditListFile(PATH_TO_WATCHERS, this.watcherObj);
    }

    public void setDownloader(String name, SubRedditType value) {
        JSONArray list = (JSONArray) this.downloaderObj.get("subreddits");
        this.setSubReddit(name, value, list);
        this.downloaderObj.put("subreddits", list);
        this.updateSubRedditListFile(PATH_TO_DOWNLOADERS, this.downloaderObj);
    }

    private void setSubReddit(String name, SubRedditType value, JSONArray list) {
        final Boolean[] inserted = {false};
        list.forEach(obj -> {
            JSONObject subreddit = (JSONObject) obj;
            if(subreddit.get("subreddit").equals(name)) {
                list.set(list.indexOf(obj), value.toJSONObject());
                inserted[0] = true;
            }
        });
        if(!inserted[0]) {
            list.add(value.toJSONObject());
        }
    }

    public void removeWatcher(String name) {
        JSONArray list = (JSONArray) this.watcherObj.get("subreddits");
        list.remove(this.getWatcher(name));
        this.watcherObj.put("subreddits", list);
        this.updateSubRedditListFile(PATH_TO_WATCHERS, this.watcherObj);
    }

    public void removeDownloader(String name) {
        JSONArray list = (JSONArray) this.downloaderObj.get("subreddits");
        list.remove(this.getDownloader(name));
        this.downloaderObj.put("subreddits", list);
        this.updateSubRedditListFile(PATH_TO_DOWNLOADERS, this.downloaderObj);
    }

    private void updateSubRedditListFile(String path, JSONObject value) {
        try {
            FileWriter file = new FileWriter(path);
            file.write(value.toJSONString());
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfig(String key) {
        return this.propMap.get(ConfigKeys.TOKEN.toString()).getProperty(key);
    }

    public void setConfig(String key, String value) {
        try {
            this.propMap.get(ConfigKeys.TOKEN.toString()).setProperty(key, value);
            OutputStream output = new FileOutputStream(PATH_TO_CONFIG_PROPERTIES);
            this.propMap.get(ConfigKeys.TOKEN.toString()).store(output, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCmd(String key) {
        return this.propMap.get(ConfigKeys.CMD.toString()).getProperty(key);
    }

    public String getMsg(String key) {
        return this.propMap.get(ConfigKeys.MSG.toString()).getProperty(key);
    }

    public String getPrefix(String key) {
        return this.propMap.get(ConfigKeys.PREFIX.toString()).getProperty(key);
    }
}
