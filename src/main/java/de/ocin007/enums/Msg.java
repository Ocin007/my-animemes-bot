package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Msg implements ConfigEnum {
    CMD_NOT_VALID("msgCmdNotValid"),
    UNAUTHORIZED("msgUnauthorized"),
    SUCCESS("msgSuccess"),
    ERROR("msgError"),
    USE_PRIVATE("msgUsePrivate"),
    SUB_ALR_EXIST("msgSubAlreadyExist"),
    SUB_NOT_EXIST("msgSubNotExist"),
    WATCHLIST_EMPTY("msgWatchListEmpty"),
    DOWNLOADLIST_EMPTY("msgDownloadListEmpty"),
    ALR_WATCHING("msgAlrWatching"),
    ALR_DOWNLOADING("msgAlrDownloading"),
    NOT_WATCHING("msgNotWatching"),
    NOT_DOWNLOADING("msgNotDownloading"),
    START_WATCHING_ALL("msgStartWatchingAll"),
    START_DOWNLOADING_ALL("msgStartDownloadingAll"),
    STOP_WATCHING_ALL("msgStopWatchingAll"),
    STOP_DOWNLOADING_ALL("msgStopDownloadingAll"),
    START_WATCHING("msgStartWatching"),
    START_DOWNLOADING("msgStartDownloading"),
    STOP_WATCHING("msgStopWatching"),
    STOP_DOWNLOADING("msgStopDownloading"),
    ALR_WATCHING_ALL("msgAlrWatchingAll"),
    ALR_DOWNLOADING_ALL("msgAlrDownloadingAll"),
    NOT_WATCHING_ALL("msgNotWatchingAny"),
    NOT_DOWNLOADING_ALL("msgNotDownloadingAny"),
    INVALID_ARGS("msgInvalidArgs"),
    SHUTDOWN("msgShutdown"),
    HELLO("msgHello"),
    RESTART_SERVICES("msgServicesStarted"),
    PAUSED_WATCHING_ALL("msgPausedWatchingAll"),
    PAUSED_DOWNLOADING_ALL("msgPausedDownloadingAll"),
    RESTART_WATCHING("msgRestartWatching"),
    RESTART_DOWNLOADING("msgRestartDownloading");

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
