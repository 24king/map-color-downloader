package com.limix.map;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadApplication {

    public static final FilenameFilter FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            if (name.startsWith(".") || (dir.isFile() && !dir.getName().endsWith(".png"))) {
                return false;
            }
            return true;
        }
    };
    public static final FilenameFilter Z_FILTER = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            if (!name.startsWith(".")) {
                return true;
            }
            return false;
        }
    };
    private static String MAP_URL = "http://api0.map.bdimg.com/customimage/tile?&x=%s&y=%s&z=%s&udt=20150601&customid=midnight";
    private static String MAP_BASE_IN = "/Users/limix/Downloads/map/16_17";
    private static String MAP_BASE_OUT = "/Users/limix/Downloads/map/out";
    private static String TILE_IN_PATH = MAP_BASE_IN + "/%s/%s/%s";
    private static String TILE_OUT_PATH = MAP_BASE_OUT + "/%s/%s/%s";
    public static void main(String[] args) {
        AtomicInteger num = new AtomicInteger(0);
        File file = new File(MAP_BASE_IN);
        File[] z_levels = file.listFiles(Z_FILTER);
        for (File z_level : z_levels) {
            File[] x_levels = z_level.listFiles(FILTER);
            for (File x_level : x_levels) {
                String[] y_levels = x_level.list(FILTER);
                Arrays.stream(y_levels).parallel().forEach(y_level -> {
                    String url = String.format(MAP_URL,x_level.getName(), y_level.substring(0, y_level.lastIndexOf(".")), z_level.getName());
                    try {
                        num.incrementAndGet();
                        if (num.get() % 100 == 0) {
                            System.out.println(num + " " + url);
                        }
                        File tile = new File(String.format(TILE_OUT_PATH, z_level.getName(), x_level.getName(), y_level));
                        if (!tile.exists()) {
                            File xFile = tile.getParentFile();
                            if (!xFile.exists()) {
                                xFile.mkdir();
                            }
                            FileUtils.copyURLToFile(new URL(url), tile);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
