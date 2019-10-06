package com.github.vroom.utility;

import java.io.File;
import java.net.URISyntaxException;

public final class ClasspathUtility {

    private ClasspathUtility() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static String getAbsolutePath(String relativePath) {
        try {
            return new File(ClasspathUtility.class.getResource(relativePath).toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to get absolute path for relative path: " + relativePath, e);
        }
    }

}
