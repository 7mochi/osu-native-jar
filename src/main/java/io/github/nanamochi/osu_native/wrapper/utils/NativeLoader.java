package io.github.nanamochi.osu_native.wrapper.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLoader {
  private static boolean loaded = false;

  private static boolean isArm64(String arch) {
    return arch.contains("aarch64") || arch.contains("arm64");
  }

  public static synchronized void ensureLoaded() {
    if (loaded) return;

    String os = System.getProperty("os.name").toLowerCase();
    String arch = System.getProperty("os.arch").toLowerCase();
    String platformDir;

    if (os.contains("win")) {
      platformDir = "windows-x86-64";
    } else if (os.contains("mac")) {
      platformDir = isArm64(arch) ? "darwin-aarch64" : "darwin-x86-64";
    } else if (os.contains("linux")) {
      platformDir = isArm64(arch) ? "linux-aarch64" : "linux-x86-64";
    } else {
      platformDir = "linux-x86-64";
    }

    String libName = System.mapLibraryName("osu.Native");
    String resourcePath = "/" + platformDir + "/" + libName;
    try {
      try (InputStream is = NativeLoader.class.getResourceAsStream(resourcePath)) {
        if (is == null) {
          throw new IOException("Not found native library resource: " + resourcePath);
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
