package com.destroflyer.escapeloop.util;

import com.destroflyer.escapeloop.Main;

public class TimeUtil {

    public static String formatFrames(int frames) {
        long millis = (long) (convertFramesToSeconds(frames) * 1000);
        long hours = millis / (1000 * 60 * 60);
        long minutes = (millis / (1000 * 60)) % 60;
        long seconds = (millis / 1000) % 60;
        long milliseconds = millis % 1000;
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds);
    }

    public static float convertFramesToSeconds(int frames) {
        return ((float) frames) / Main.FPS;
    }
}
