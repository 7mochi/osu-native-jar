package io.github.nanamochi.osu_native.utils;

import io.github.nanamochi.osu_native.StandardRulesetTests;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class File {
  public static String resourceToTempFile(String path) throws IOException {
    try (InputStream is = StandardRulesetTests.class.getClassLoader().getResourceAsStream(path)) {
      Path temp = Files.createTempFile("resource-", "-" + path);
      Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
      return temp.toString();
    }
  }
}
