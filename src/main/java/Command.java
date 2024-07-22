import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Command {
  static List<String> declaredMethods = getDeclaredMethods();
  private static String filePath;

  public static boolean callCommand(String[] args) {
    switch (args[0]) {
      case "exit":
        return exit(args);
      case "echo":
        return echo(args);
      case "pwd":
        return pwd();
      case "type":
        return type(args);
      case "cd":
        return cd(args);
      default:
        if (programExists(args[0])) {
          return ExternalRunner.run(args);
        }
        return notFound(args[0]);
    }
  }

  private static boolean echo(String[] args) {
    System.out.println(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
    return true;
  }

  private static boolean exit(String[] args) {
    if (args.length != 2 || !args[1].equals("0")) {
      return invalidArgument();
    }
    return false;
  }

  private static boolean type(String[] args) {
    if (args.length != 2) {
      return invalidArgument();
    }
    if (declaredMethods.contains(args[1])) {
      System.out.printf("%s is a shell builtin\n", args[1]);
      return true;
    }

    if (programExists(args[1])) {
      System.out.printf("%s is %s\n", args[1], filePath);
      return true;
    }

    System.out.printf("%s: not found\n", args[1]);
    return true;
  }

  private static boolean cd(String[] args) {
    if (args.length != 2) {
      return invalidArgument();
    }

    Path path = Paths.get(args[1]);
    if (!Files.isDirectory(path)) {
      System.out.printf("%s: No such file or directory\n", args[1]);
      return true;
    }

    System.setProperty("user.dir", path.toAbsolutePath().normalize().toString());
    return true;
  }

  private static boolean pwd() {
    System.out.println(System.getProperty("user.dir"));
    return true;
  }

  /*
   * It would be simpler to have a static list with the different methods manually
   * added, but I want to try out some Java Reflection
   */
  private static List<String> getDeclaredMethods() {
    List<String> declaredMethods = new ArrayList<>();
    try {
      Class<?> c = Class.forName("Command");
      Method m[] = c.getDeclaredMethods();
      for (Method n : m) {
        declaredMethods.add(n.getName());
      }
      declaredMethods.remove("main");
      declaredMethods.remove("notFound");
      declaredMethods.remove("invalidArgument");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return declaredMethods;
  }

  private static boolean notFound(String input) {
    System.out.printf("%s: command not found\n", input);
    return true;
  }

  private static boolean invalidArgument() {
    System.out.println("Invalid argument");
    return true;
  }

  private static boolean programExists(String command) {
    String[] pathEnv = System.getenv("PATH").split(":");
    for (String p : pathEnv) {
      String tmpFileName = p + "/" + command;
      Path path = Paths.get(tmpFileName);
      if (Files.exists(path)) {
        filePath = tmpFileName;
        return true;
      }
    }
    return false;
  }
}
