package com.destroflyer.escapeloop.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.destroflyer.escapeloop.Main;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        // This handles macOS support and helps on Windows
        if (StartupHelper.startNewJvmIfRequired()) {
            return;
        }
        String authToken = args[0];
        if (authToken == null) {
            throw new RuntimeException("No destrostudios auth token provided");
        }
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Escape Loop");
        configuration.setWindowIcon("./icon/128.png", "./icon/64.png", "./icon/32.png", "./icon/16.png");
        configuration.setWindowedMode(Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        configuration.useVsync(false);
        configuration.setForegroundFPS(Main.FPS);
        new Lwjgl3Application(new Main(authToken), configuration);
    }
}
