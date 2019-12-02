package com.tterrag.advent2019.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Taken from
 * <a href="https://github.com/SizableShrimp/AdventOfCode2019/blob/e7a0095a2144d8802a24c89198e5f9d9f2486d3f/src/main/java/me/sizableshrimp/adventofcode/helper/DataReader.java">SizableShrimp's AOC
 * Project</a> and modified for use on java 8 without any libraries.
 */
public class DataReader {

    /**
     * The read method is used to locate input data for a specified {@link Day}.
     * <p>
     * This first checks to see if there is a text file with input data for the specified {@link Day} class in the "days" subfolder in resources or run directory. For example, class Day01 would have a
     * corresponding input text file in "days/day01.txt". If an input text file is found, the data from that file is returned.
     * <p>
     * If no input text file is found, this method then <u>tries to connect to the Advent Of Code servers for input data</u>. This step of the method is optional, and requires an environment variable
     * be set to use it. "AOC_SESSION" must be set as an environment variable, which should hold your session cookie for the <a href="http://adventofcode.com">Advent Of Code Website</a>. This cookie
     * can be found using browser inspection. If not set, this section of the method will not be run at all.
     * <p>
     * If a success connection is made to the AOC server, the input data is stored in a file that is located in your run directory under a "days" subfolder in case of later usage. The data fetched
     * from the server originally is then returned.
     *
     * @param clazz
     *            The {@link Day} class of which to read input data.
     * @return A list of strings representing each line of input data.
     */
    public static List<String> read(int day) {
        Path path = getPath(day);
        List<String> lines = getDataFromFile(path);

        if (lines != null)
            return lines;

        if (System.getenv("AOC_SESSION") == null)
            throw new IllegalStateException("Missing session token!");

        return getDataFromServer(day, path);
    }

    private static List<String> getDataFromServer(int day, Path path) {
        List<String> lines = new ArrayList<>();

        try {
            int year = 2019;
            URL url = new URL("https://adventofcode.com/" + year + "/day/" + day + "/input");
            
            HttpURLConnection request = (HttpURLConnection) url.openConnection(); 
            request.addRequestProperty("User-Agent", "tterrag-AOC/1.0.0 (https://tterrag.com)");
            request.addRequestProperty("Cookie", "session=" + System.getenv("AOC_SESSION"));
            
            if (request.getResponseCode() / 100 == 2) {
                try (InputStream resp = request.getInputStream();
                     InputStreamReader isr = new InputStreamReader(resp);
                     BufferedReader br = new BufferedReader(isr)) {
                    
                    while (br.ready()) {
                        lines.add(br.readLine());
                    }
                    if (path != null) {
                        write(path, lines);
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lines;
    }

    private static List<String> getDataFromFile(Path path) {
        try {
            if (path != null && Files.exists(path)) {
                return Files.readAllLines(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Path getPath(int day) {
        return Paths.get("problem_input", String.format("day%02d.txt", day));
    }

    private static void write(Path path, List<String> lines) throws IOException {
        Path parent = path.getParent();
        if (!Files.exists(parent))
            Files.createDirectory(parent);
        new Thread(() -> {
            try {
                Files.write(path, lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}