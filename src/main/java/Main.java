import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
  static List<String> declaredMethods = new ArrayList<>();
  static String filePath = "";

  public static void main(String[] args) throws Exception {

    getDeclaredMethods();
    Scanner scanner = new Scanner(System.in);
    boolean isRunning = true;
    do {
      System.out.print("$ ");
      String input = scanner.nextLine();
      String[] shellArgs = input.split(" ");
      isRunning = callCommand(shellArgs);
      filePath = "";
    } while (isRunning);
    scanner.close();
  }

  private static boolean callCommand(String[] args) {
    switch (args[0]) {
      case "exit":
        return exit(args);
      case "echo":
        return echo(args);
      case "type":
        return type(args);
      default:
        if (programExists(args[0])) {
          return runExternalProgram(args);
        }
        return notFound(args[0]);
    }
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

  private static boolean runExternalProgram(String[] args) {
    try {
      Runtime.getRuntime().exec(args);
    } catch (IOException e) {
      System.err.println("Something went wrong");
      e.printStackTrace();
    }
    return true;
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

  private static boolean notFound(String input) {
    System.out.printf("%s: command not found\n", input);
    return true;
  }

  private static boolean invalidArgument() {
    System.out.println("Invalid argument");
    return true;
  }

  private static void getDeclaredMethods() throws ClassNotFoundException {
    Class<?> c = Class.forName("Main");
    Method m[] = c.getDeclaredMethods();
    for (Method n : m) {
      declaredMethods.add(n.getName());
    }
    declaredMethods.remove("main");
    declaredMethods.remove("notFound");
    declaredMethods.remove("invalidArgument");
  }
}
