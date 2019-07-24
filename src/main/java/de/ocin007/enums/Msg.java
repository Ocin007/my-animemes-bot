package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Msg implements ConfigEnum {
    UNAUTHORIZED("msgUnauthorized"),
    SUCCESS("msgSuccess"),
    ERROR("msgError"),
    SUB_ALR_EXIST("msgSubAlreadyExist"),
    SUB_NOT_EXIST("msgSubNotExist"),
    WATCHLIST_EMPTY("msgWatchListEmpty"),
    INVALID_ARGS("msgInvalidArgs"),
    SHUTDOWN("msgShutdown");

    private String name;
    private Config config;

    Msg(String name) {
        this.name = name;
        this.config = Config.getInstance();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String literal() {
        return this.config.getMsg(this.name);
    }
}
