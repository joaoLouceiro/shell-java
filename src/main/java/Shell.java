import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Shell {

  private static Path cwd = Paths.get("");

  static void repl(Scanner scanner) {
    boolean isRunning = true;
    do {
      System.out.print("$ ");
      String input = scanner.nextLine();
      String[] shellArgs = input.split(" ");
      isRunning = Command.callCommand(shellArgs);
    } while (isRunning);
  }

  public static Path getCwd() {
    return cwd;
  }

  public static void setCwd(Path cwd) {
    Shell.cwd = cwd;
  }

}
