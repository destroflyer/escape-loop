package com.destroflyer.escapeloop.game;

import com.badlogic.gdx.Gdx;

public class Skins {

    private static final String LOG_TAG = "SKINS";

    public static final Skin[] PLAYER = new Skin[] {
        new Skin("default", "W-13"),
        new Skin("black", "W-25"),
        new Skin("rainbow", "W-66"),
    };

    public static final Skin[] ENEMY = new Skin[] {
        new Skin("default", "G-05"),
        new Skin("black", "G-97"),
    };

    public static Skin get(Skin[] allSkins, String skinName) {
        if (skinName != null) {
            for (Skin skin : allSkins) {
                if (skin.getName().equals(skinName)) {
                    return skin;
                }
            }
            Gdx.app.error(LOG_TAG, "Invalid skin name: " + skinName);
        }
        return allSkins[0];
    }
}
