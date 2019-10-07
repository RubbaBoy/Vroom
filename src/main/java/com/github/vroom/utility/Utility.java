package com.github.vroom.utility;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Utility {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utility.class);

    public static String loadResource(String fileName) {
        try (var stream = Utility.class.getResourceAsStream(fileName)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static int[] listIntToArray(List<Integer> list) {
        int[] result = list.stream().mapToInt((Integer v) -> v).toArray();
        return result;
    }

    public static float[] listToArray(List<Float> list) {
        int size = list != null ? list.size() : 0;
        float[] floatArr = new float[size];
        for (int i = 0; i < size; i++) {
            floatArr[i] = list.get(i);
        }
        return floatArr;
    }

    public static boolean existsResourceFile(String fileName) {
        boolean result;
        try (InputStream is = Utility.class.getResourceAsStream(fileName)) {
            result = is != null;
        } catch (Exception excp) {
            result = false;
        }
        return result;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
        ByteBuffer buffer;

        var path = Paths.get(resource);

        try {
            if (Files.isReadable(path)) {
                try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                    buffer = MemoryUtil.memAlloc((int) fc.size() + 1);
                    while (fc.read(buffer) != -1);
                }
            } else {
                try (var source = Utility.class.getResourceAsStream(resource);
                     ReadableByteChannel rbc = Channels.newChannel(source)) {
                    buffer = MemoryUtil.memAlloc(bufferSize);

                    while (rbc.read(buffer) != -1) {
                        if (!buffer.hasRemaining()) {
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                        }
                    }
                }
            }

            return buffer.flip();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
