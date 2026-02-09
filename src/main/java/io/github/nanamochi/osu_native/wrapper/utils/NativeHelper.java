package io.github.nanamochi.osu_native.wrapper.utils;

import io.github.nanamochi.osu_native.wrapper.objects.ErrorCode;
import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.charset.StandardCharsets;

public class NativeHelper {

  @FunctionalInterface
  public interface NativeStringFunction {
    int apply(MemorySegment handle, MemorySegment buffer, MemorySegment bufferSize);
  }

  public static String getString(MemorySegment handle, NativeStringFunction function) {
    try (Arena localArena = Arena.ofConfined()) {
      MemorySegment bufferSize = localArena.allocate(ValueLayout.JAVA_INT);
      int res = function.apply(handle, MemorySegment.NULL, bufferSize);
      ErrorCode code = ErrorCode.fromValue(res);
      if (code != ErrorCode.BUFFER_SIZE_QUERY && code != ErrorCode.SUCCESS) {
        throw new RuntimeException("Native call failed (size query): " + code);
      }

      int size = bufferSize.get(ValueLayout.JAVA_INT, 0);
      if (size <= 0) return "";

      MemoryLayout byteArrayLayout = MemoryLayout.sequenceLayout(size, ValueLayout.JAVA_BYTE);
      MemorySegment buffer = localArena.allocate(byteArrayLayout);

      res = function.apply(handle, buffer, bufferSize);
      if (ErrorCode.fromValue(res) != ErrorCode.SUCCESS) {
        throw new RuntimeException("Native call failed: " + ErrorCode.fromValue(res));
      }

      return buffer.getString(0, StandardCharsets.UTF_8);
    }
  }
}
