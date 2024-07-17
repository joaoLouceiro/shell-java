import java.util.Scanner;

public class Main {
  static String filePath = "";

  public static void main(String[] args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    Shell.repl(scanner);
    scanner.close();
  }

}
