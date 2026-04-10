package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class SkinUtil {

    public static TextButton.TextButtonStyle getToggleButtonStyle(Skin baseSkin) {
        TextButton.TextButtonStyle defaultStyle = baseSkin.get("default", TextButton.TextButtonStyle.class);
        TextButton.TextButtonStyle toggleStyle = new TextButton.TextButtonStyle(defaultStyle);
        toggleStyle.checked = baseSkin.getDrawable("button-checked");
        return toggleStyle;
    }
}
