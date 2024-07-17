import java.util.Scanner;

public class Shell {

  static void repl(Scanner scanner) {
    boolean isRunning = true;
    do {
      System.out.print("$ ");
      String input = scanner.nextLine();
      String[] shellArgs = input.split(" ");
      isRunning = Command.callCommand(shellArgs);
    } while (isRunning);
  }

}
