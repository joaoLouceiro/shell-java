import java.util.Scanner;

public class Main {
  public static void main(String[] args) throws Exception {
    while (true) {
      System.out.print("$ ");

      Scanner scanner = new Scanner(System.in);
      String input = scanner.nextLine();
      System.out.printf("%s: command not found\n", input);
    }

  }
}
