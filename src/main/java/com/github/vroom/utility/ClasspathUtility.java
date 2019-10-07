package com.github.vroom.utility;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public final class ClasspathUtility {

    private ClasspathUtility() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static String getAbsolutePath(URL relativeUrl) {
        try {
            return new File(relativeUrl.toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to get absolute path for relative URL: " + relativeUrl, e);
        }
    }

}
