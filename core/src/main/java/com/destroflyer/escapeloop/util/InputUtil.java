package com.destroflyer.escapeloop.util;

import com.badlogic.gdx.Input;

public class InputUtil {

    public static String getKeyName(int keyCode) {
        switch (keyCode) {
            case Input.Keys.DEL: return "Backspace";
            case Input.Keys.FORWARD_DEL: return "Delete";
            default: return Input.Keys.toString(keyCode);
        }
    }
}
