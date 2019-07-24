package de.ocin007.config;

import de.ocin007.config.types.SubRedditType;
import de.ocin007.enums.config.ConfigKeys;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class Config {

    private final static String PATH_TO_TOKEN_PROPERTIES = "src/main/resources/config.properties";
    private final static String PATH_TO_CMD_PROPERTIES = "src/main/resources/commands.properties";
    private final static String PATH_TO_MSG_PROPERTIES = "src/main/resources/messages.properties";
    private final static String PATH_TO_PREFIX_PROPERTIES = "src/main/resources/prefixes.properties";
    private final static String PATH_TO_CURRENT_SUBREDDITS = "src/main/resources/json/currentlyWatchedSubReddits.json";

    private static Config ourInstance;
    private HashMap<String, Properties> propMap;
    private JSONObject jsonObj;

    public static Config getInstance() {
        if(ourInstance == null) {
            ourInstance = new Config();
        }
        return ourInstance;
    }

    private Config() {
        try {
            this.propMap = new HashMap<>();
            this.jsonObj = new JSONObject();
            this.appendProperty(ConfigKeys.TOKEN.toString(), PATH_TO_TOKEN_PROPERTIES);
            this.appendProperty(ConfigKeys.CMD.toString(), PATH_TO_CMD_PROPERTIES);
            this.appendProperty(ConfigKeys.MSG.toString(), PATH_TO_MSG_PROPERTIES);
            this.appendProperty(ConfigKeys.PREFIX.toString(), PATH_TO_PREFIX_PROPERTIES);
            this.getJson(PATH_TO_CURRENT_SUBREDDITS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getJson(String path) throws Exception {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(path);
        Object obj = jsonParser.parse(reader);
        this.jsonObj = (JSONObject) obj;
    }

    private void appendProperty(String key, String path) throws IOException {
        InputStream input = new FileInputStream(path);
        Properties prop = new Properties();
        prop.load(input);
        input.close();
        this.propMap.put(key, prop);
    }

    public JSONObject getSubReddit(String name) {
        JSONArray list = (JSONArray) this.jsonObj.get("subreddits");
        for (Object obj: list) {
            JSONObject subreddit = (JSONObject) obj;
            if((subreddit.get("subreddit")).equals(name)) {
                return subreddit;
            }
        }
        return null;
    }

    public JSONArray getAllSubReddits() {
        return (JSONArray) this.jsonObj.get("subreddits");
    }

    public void setSubReddit(String name, SubRedditType value) {
        JSONArray list = (JSONArray) this.jsonObj.get("subreddits");
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
        this.jsonObj.put("subreddits", list);
        this.updateSubRedditListFile();
    }

    public void removeSubReddit(String name) {
        JSONArray list = (JSONArray) this.jsonObj.get("subreddits");
        list.remove(this.getSubReddit(name));
        this.jsonObj.put("subreddits", list);
        this.updateSubRedditListFile();
    }

    private void updateSubRedditListFile() {
        try {
            FileWriter file = new FileWriter(PATH_TO_CURRENT_SUBREDDITS);
            file.write(this.jsonObj.toJSONString());
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
            OutputStream output = new FileOutputStream(PATH_TO_TOKEN_PROPERTIES);
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
