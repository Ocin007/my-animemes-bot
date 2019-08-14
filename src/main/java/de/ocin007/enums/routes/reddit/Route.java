package de.ocin007.enums.routes.reddit;

public enum Route {
    AUTHORIZE("/api/v1/authorize"),
    ACCESS_TOKEN("/api/v1/access_token"),
    SINGLE_POST("/api/info"),
    ABOUT("/about"),
    RANDOM("/r/random");

    private String name;

    Route(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
