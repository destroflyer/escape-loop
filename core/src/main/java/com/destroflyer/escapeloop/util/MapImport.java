package com.destroflyer.escapeloop.util;

import com.destroflyer.escapeloop.game.loader.MapFileLoader;
import com.destroflyer.escapeloop.states.MapsState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MapImport {

    public static void main(String[] args) {
        importAllMaps();
    }

    public static void importAllMaps() {
        importMenu();
        for (int mapIndex = 0; mapIndex < MapsState.MAPS_COUNT; mapIndex++) {
            importMap(mapIndex);
        }
    }

    public static void importMenu() {
        try {
            File srcDirectory = new File(getSrcMapsDirectoryPath() + "/Menu");
            File dstDirectory = new File("./textures/menu");
            copyFile(srcDirectory, dstDirectory, "_composite.png", "background.png");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void importMap(int mapIndex) {
        File srcDirectory = new File(getSrcMapsDirectoryPath() + "/Level_" + mapIndex);
        File dstDirectory = new File(MapFileLoader.DIRECTORY + "/" + mapIndex);
        dstDirectory.mkdir();
        try {
            copyFile(srcDirectory, dstDirectory, "data.json", "data.json");
            copyFile(srcDirectory, dstDirectory, "Terrain.csv", "terrain.csv");
            copyFile(srcDirectory, dstDirectory, "Terrain.png", "terrain.png");
            copyFile(srcDirectory, dstDirectory, "Decoration.png", "decoration.png");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("NewApi")
    private static void copyFile(File srcDirectory, File dstDirectory, String srcFileName, String dstFileName) throws IOException {
        Files.copy(Paths.get(srcDirectory.getPath() + "/" + srcFileName), Paths.get(dstDirectory.getPath() + "/" + dstFileName), StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isSrcMapsDirectoryPathSet() {
        return getSrcMapsDirectoryPath() != null;
    }

    private static String getSrcMapsDirectoryPath() {
        return System.getenv("IMPORT_FROM_MAP_DIRECTORY");
    }
}
