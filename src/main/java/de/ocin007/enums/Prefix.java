package de.ocin007.enums;

import de.ocin007.config.Config;

public enum Prefix implements ConfigEnum {
    OWNER("cmdOwnerPrefix"),
    ADMIN("cmdAdminPrefix"),
    VIP("cmdVipPrefix"),
    GENERAL("cmdGeneralPrefix");

    private String name;
    private Config config;

    Prefix(String name) {
        this.name = name;
        this.config = Config.getInstance();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String literal() {
        return this.config.getPrefix(this.name);
    }
}
