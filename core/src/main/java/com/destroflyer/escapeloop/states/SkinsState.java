package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;

public class SkinsState extends State {

    private static final String SETTING_KEY_SKIN_PLAYER = "skinPlayer";
    private static final String SETTING_KEY_SKIN_ENEMY = "skinEnemy";

    public Skin getSelectedPlayerSkin() {
        return getSelectedSkin(Skins.PLAYER, SETTING_KEY_SKIN_PLAYER);
    }

    public Skin getSelectedEnemySkin() {
        return getSelectedSkin(Skins.ENEMY, SETTING_KEY_SKIN_ENEMY);
    }

    private Skin getSelectedSkin(Skin[] allSkins, String settingKey) {
        String selectedSkinName = main.getSettingsState().getPreferences().getString(settingKey, "default");
        return Skins.get(allSkins, selectedSkinName);
    }

    public void selectPlayerSkin(Skin skin) {
        selectSkin(SETTING_KEY_SKIN_PLAYER, skin);
    }

    public void selectEnemySkin(Skin skin) {
        selectSkin(SETTING_KEY_SKIN_ENEMY, skin);
    }

    private void selectSkin(String settingKey, Skin skin) {
        Preferences preferences = main.getSettingsState().getPreferences();
        preferences.putString(settingKey, skin.getName());
        preferences.flush();
    }
}
