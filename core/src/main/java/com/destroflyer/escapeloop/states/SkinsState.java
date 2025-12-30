package com.destroflyer.escapeloop.states;

import com.badlogic.gdx.Preferences;
import com.destroflyer.escapeloop.State;
import com.destroflyer.escapeloop.game.Skin;
import com.destroflyer.escapeloop.game.Skins;

public class SkinsState extends State {

    public Skin getSelectedPlayerSkin() {
        return getSelectedSkin(Skins.PLAYER, "skinPlayer");
    }

    public Skin getSelectedEnemySkin() {
        return getSelectedSkin(Skins.ENEMY, "skinEnemy");
    }

    private Skin getSelectedSkin(Skin[] allSkins, String settingKey) {
        String selectedSkinName = main.getSettingsState().getPreferences().getString(settingKey);
        return Skins.get(allSkins, selectedSkinName);
    }

    public void selectPlayerSkin(Skin skin) {
        selectSkin("skinPlayer", skin);
    }

    public void selectEnemySkin(Skin skin) {
        selectSkin("skinEnemy", skin);
    }

    private void selectSkin(String settingKey, Skin skin) {
        Preferences preferences = main.getSettingsState().getPreferences();
        preferences.putString(settingKey, skin.getName());
        preferences.flush();
    }
}
