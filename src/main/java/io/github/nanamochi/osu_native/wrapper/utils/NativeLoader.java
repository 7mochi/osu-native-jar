package io.github.nanamochi.osu_native.wrapper.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLoader {
  private static boolean loaded = false;

  public static synchronized void ensureLoaded() {
    if (loaded) return;

    String libName = System.mapLibraryName("osu.Native");
    try {
      try (InputStream is = NativeLoader.class.getResourceAsStream("/" + libName)) {
        if (is == null) {
          throw new IOException("Not found native library resource: " + libName);
        }
        Path tempLib = Files.createTempFile("osu_native_", "_" + libName);
        tempLib.toFile().deleteOnExit();

        Files.copy(is, tempLib, StandardCopyOption.REPLACE_EXISTING);

        System.load(tempLib.toAbsolutePath().toString());
        loaded = true;
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load native library: " + libName, e);
    }
  }
}
