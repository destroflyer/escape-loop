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
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("Escape Loop");
        configuration.setWindowIcon("./icon/128.png", "./icon/64.png", "./icon/32.png", "./icon/16.png");
        configuration.setWindowedMode(Main.VIEWPORT_WIDTH, Main.VIEWPORT_HEIGHT);
        configuration.useVsync(true);
        // Safeguard as vsync doesn't always work on Linux (And add 1 to try to match fractional refresh rates)
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        new Lwjgl3Application(new Main(), configuration);
    }
}
