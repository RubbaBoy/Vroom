package com.github.vroom.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClasspathUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClasspathUtility.class);

    private static Map<String, File> classpathMap = new ConcurrentHashMap<>();

    public static File findInClasspath(String relativeDir) throws FileNotFoundException {
        if (classpathMap.containsKey(relativeDir)) {
            return classpathMap.get(relativeDir);
        }

        var findingDir = relativeDir.replaceAll("(\\|/)", File.separator);
        var paths = System.getProperty("java.class.path").split(File.pathSeparator);
        var foundFile = Arrays.stream(paths).parallel()
                .map(path -> Paths.get(path, findingDir))
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("File not found in classpath: " + relativeDir)).toFile();
        classpathMap.put(relativeDir, foundFile);
        return foundFile;
    }

}
