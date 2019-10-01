package com.github.vroom.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Utility {

    private Utility() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static String loadResource(String fileName) throws IOException {
        try (var stream = Utility.class.getResourceAsStream(fileName)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        var floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

}
