package helper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger {

  private static Path getFileName(String name) {
    String fileName = name + ".txt";
    return Paths.get("logs", fileName);
  }

  private static void setup(String name) {
    Path path = getFileName(name);
    if (path.toFile().exists()) {
      path.toFile().delete();
    }

    try {
      File f = new File(path.toString());
      f.getParentFile().mkdirs();
      f.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public static void print(String name, Object content) {
    print(name, content.toString());
  }

  public static void print(String name, String content) {
    try {
      final Path path = getFileName(name);

      if (!path.toFile().exists()) {
        setup(name);
      }

      content += "\n";
      Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
