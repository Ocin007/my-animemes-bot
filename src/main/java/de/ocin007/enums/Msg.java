package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Msg implements ConfigEnum {
    CMD_NOT_VALID("msgCmdNotValid"),
    UNAUTHORIZED("msgUnauthorized"),
    SUCCESS("msgSuccess"),
    ERROR("msgError"),
    SUB_ALR_EXIST("msgSubAlreadyExist"),
    SUB_NOT_EXIST("msgSubNotExist"),
    WATCHLIST_EMPTY("msgWatchListEmpty"),
    ALR_WATCHING("msgAlrWatching"),
    NOT_WATCHING("msgNotWatching"),
    START_WATCHING_ALL("msgStartWatchingAll"),
    STOP_WATCHING_ALL("msgStopWatchingAll"),
    START_WATCHING("msgStartWatching"),
    STOP_WATCHING("msgStopWatching"),
    ALR_WATCHING_ALL("msgAlrWatchingAll"),
    NOT_WATCHING_ALL("msgNotWatchingAny"),
    INVALID_ARGS("msgInvalidArgs"),
    SHUTDOWN("msgShutdown"),
    HELLO("msgHello"),
    RESTART_SERVICES("msgServicesStarted"),
    PAUSED_WATCHING_ALL("msgPausedWatchingAll"),
    RESTART_WATCHING("msgRestartWatching");

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
