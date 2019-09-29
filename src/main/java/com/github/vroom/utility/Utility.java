package com.github.vroom.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class Utility {

    private Utility() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static String loadResource(String fileName) throws IOException {
        try (var stream = Utility.class.getResourceAsStream(fileName)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
