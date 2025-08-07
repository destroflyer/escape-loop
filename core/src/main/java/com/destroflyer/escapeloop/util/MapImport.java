package com.destroflyer.escapeloop.util;

import com.destroflyer.escapeloop.game.loader.MapLoader;

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
        for (File srcDirectory : new File(getSrcMapsDirectoryPath()).listFiles()) {
            importMap(srcDirectory.getName());
        }
    }

    public static void importMap(String mapName) {
        try {
            File srcDirectory = new File(getSrcMapsDirectoryPath() + "/" + mapName);
            File dstDirectory = new File(MapLoader.DIRECTORY + "/" + mapName);
            dstDirectory.mkdir();
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
