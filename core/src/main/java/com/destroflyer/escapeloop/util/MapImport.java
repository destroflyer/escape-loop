package com.destroflyer.escapeloop.util;

import com.destroflyer.escapeloop.game.loader.MapFileLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MapImport {

    private static final String MAP_NAME_PREFIX = "Level_";

    public static void main(String[] args) {
        importAllMaps();
    }

    public static void importAllMaps() {
        for (File srcDirectory : new File(getSrcMapsDirectoryPath()).listFiles()) {
            int mapNumber = Integer.parseInt(srcDirectory.getName().substring(MAP_NAME_PREFIX.length()));
            importMap(mapNumber);
        }
    }

    public static void importMap(int mapNumber) {
        File srcDirectory = new File(getSrcMapsDirectoryPath() + "/Level_" + mapNumber);
        File dstDirectory = new File(MapFileLoader.DIRECTORY + "/" + mapNumber);
        dstDirectory.mkdir();
        try {
            copyFile(srcDirectory, dstDirectory, "data.json");
            copyFile(srcDirectory, dstDirectory, "Terrain.csv");
            copyFile(srcDirectory, dstDirectory, "Terrain.png");
            copyFile(srcDirectory, dstDirectory, "Decoration.png");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("NewApi")
    private static void copyFile(File srcDirectory, File dstDirectory, String fileName) throws IOException {
        Files.copy(Paths.get(srcDirectory.getPath() + "/" + fileName), Paths.get(dstDirectory.getPath() + "/" + fileName.toLowerCase()), StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean isSrcMapsDirectoryPathSet() {
        return getSrcMapsDirectoryPath() != null;
    }

    private static String getSrcMapsDirectoryPath() {
        return System.getenv("IMPORT_FROM_MAP_DIRECTORY");
    }
}
