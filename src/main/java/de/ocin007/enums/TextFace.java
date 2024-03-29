package de.ocin007.enums;

public enum TextFace {
    WAVE("( ^_^)／"),
    IDK("¯\\_(ツ)_/¯"),
    HAPPY("\\ (•◡•) /"),
    CRY("༼ つ ಥ_ಥ ༽つ"),
    CONFOUNDED("(ᵕ﹏ᵕ)"),
    TABLE_FLIP("(ノಠ益ಠ)ノ┻━┻"),
    SHAME("(-    -  |||)"),
    REALLY("(；一ω一||)"),
    SERIOUS("(ー_ー )"),
    WATCHING("(⊍-⊍)"),
    WHAT("(⊙д⊙)");

    private String name;

    TextFace(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
