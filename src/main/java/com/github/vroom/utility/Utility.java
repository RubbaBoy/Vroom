package com.github.vroom.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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
        if (list == null) {
            return new float[0];
        }

        int size = list.size();
        var floatArr = new float[size];

        Iterator<Float> iterator = list.iterator();

        for (int i = 0; i < size && iterator.hasNext(); i++) {
            floatArr[i] = iterator.next();
        }

        return floatArr;
    }

    public static int[] listIntToArray(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

}
