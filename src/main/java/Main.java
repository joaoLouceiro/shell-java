import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class Main {
  static List<String> declaredMethods = new ArrayList<>();
  static String filePath = "";
  static ProcessBuilder processBuilder = new ProcessBuilder();
  static ExecutorService executorService = Executors.newFixedThreadPool(10);

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
          return run(args);
          // return runExternalProgram(args);
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

  private static boolean run(String[] args) {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(args);
    // builder.directory(new File(System.getProperty("user.home")));
    Process process;
    try {
      process = builder.start();
      StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      Future<?> future = executorService.submit(streamGobbler);
      process.waitFor();
      process.exitValue();
      future.get(10, TimeUnit.SECONDS);

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ExecutionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (TimeoutException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;
  }

  // private static boolean runExternalProgram(String[] args) {
  // try {
  //
  // Process p = Runtime.getRuntime().exec(args);
  // System.out.println(exitCode);
  // } catch (IOException e) {
  // System.err.println("Something went wrong");
  // e.printStackTrace();
  // }
  // return true;
  // }

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

  private static class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(consumer);
    }
  }
}
