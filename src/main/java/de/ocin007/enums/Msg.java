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
    ROLE_ADDED_TO_VIP("msgRoleAddedToVip"),
    ROLE_ALR_ADDED_TO_VIP("msgRoleAlrAddedToVip"),
    ROLE_RM_FROM_VIP("msgRoleRmFromVip"),
    ROLE_ALR_RM_FROM_VIP("msgRoleAlrRmFromVip"),
    ROLE_RM_ALL_FROM_VIP("msgRoleRmAllFromVip"),
    ROLE_ALR_RM_ALL_FROM_VIP("msgRoleAlrRmAllFromVip"),
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
