package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Cmd implements ConfigEnum {
    GET_AUTH_URL("cmdGetAuthUrl"),
    AUTHORIZE("cmdAuthorize"),
    ADD_SUBREDDIT("cmdAddSubReddit"),
    REMOVE_SUBREDDIT("cmdRemoveSubReddit"),
    EDIT_SUBREDDIT("cmdEditSubReddit"),
    SHOW("cmdShowWatchList"),
    RANDOM("cmdRandomPost"),
    WATCH("cmdWatch"),
    SHUTDOWN("cmdShutdown"),
    HELP("cmdHelp");

    private String name;
    private Config config;

    Cmd(String name) {
        this.name = name;
        this.config = Config.getInstance();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String literal() {
        return this.config.getCmd(this.name);
    }
}
