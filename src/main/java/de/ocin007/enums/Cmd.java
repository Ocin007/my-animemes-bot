package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Cmd implements ConfigEnum {
    GET_AUTH_URL("cmdGetAuthUrl"),
    AUTHORIZE("cmdAuthorize"),
    ADD_WATCHER("cmdAddWatcher"),
    REMOVE_WATCHER("cmdRemoveWatcher"),
    EDIT_WATCHER("cmdEditWatcher"),
    ADD_DOWNLOADER("cmdAddDownloader"),
    REMOVE_DOWNLOADER("cmdRemoveDownloader"),
    EDIT_DOWNLOADER("cmdEditDownloader"),
    SHOW_WATCHER("cmdShowWatchList"),
    SHOW_DOWNLOADER("cmdShowDownloaderList"),
    RANDOM("cmdRandomPost"),
    WATCH_DOWNLOADER("cmdWatchDownloader"),
    WATCH_WATCHER("cmdWatchWatcher"),
    SHUTDOWN("cmdShutdown"),
    ADD_VIP_ROLE("cmdAddVipRole"),
    REMOVE_VIP_ROLE("cmdRemoveVipRole"),
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
