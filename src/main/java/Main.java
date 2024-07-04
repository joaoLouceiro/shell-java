import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    String input;
    do {
      System.out.print("$ ");
      input = scanner.nextLine();
      if (!input.equals("exit 0"))
        System.out.printf("%s: command not found\n", input);
    } while (!input.equals("exit 0"));
    scanner.close();
  }

}
