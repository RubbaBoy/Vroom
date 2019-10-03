package com.github.vroom.utility;

public final class ClasspathUtility {

    private ClasspathUtility() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static String getAbsolutePath(String relativePath) {
        return ClasspathUtility.class.getResource(relativePath).getPath().substring(1);
    }

}
