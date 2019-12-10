package helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import org.apache.commons.io.FileUtils;

public class Logger {

  private static boolean init = false;

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
     // e.printStackTrace();
    }

  }

  public static void print(String name, Map content) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String string = gson.toJson(content);

    print(name, string);
  }

  public static void print(String name, Object content) {
    print(name, content.toString());
  }

  public static void print(String name, String content) {

    try {
      final Path path = getFileName(name);

      if (!init) {
        init = true;
       // FileUtils.cleanDirectory(path.toFile().getParentFile());
        for (File file: path.toFile().getParentFile().listFiles()) {
          if(!file.getName().equals("GameData.txt")) {
            System.out.println(file.getName());
            file.delete();
          }
        }
      }
      if (!path.toFile().exists()) {
        setup(name);
      }

      content += "\n";
      Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    } catch (final IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public static void print(String name, String content, Boolean append) {
    String header = "";
    if(!append) print(name,content);
    else {
      try {
        final Path path = getFileName(name);
        if (!path.toFile().exists()) {
          setup(name);
          header = "Personality,agentType,nrPlayers,nrCrazyInvestors,nrHighRollerInvestors,nrNormalInvestors,nrSafeBetterInvestors,nrCrazyManagers,nrHighRollerManagers,nrNormalManagers,nrSafeBetterManagers,nrCompanies,nrBlueCompanies,nrGreenCompanies,nrYellowCompanies,nrRedCompanies,finalCapital\n";

        }
        content = header + content;
        content += "\n";
        Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
      } catch (final IOException ioe) {
        ioe.printStackTrace();
      }
    }

  }
}
