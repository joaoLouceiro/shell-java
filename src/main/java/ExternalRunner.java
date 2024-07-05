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

public class ExternalRunner {

  static ExecutorService executorService = Executors.newFixedThreadPool(10);

  public static boolean run(String[] args) {
    ProcessBuilder builder = new ProcessBuilder();
    builder.command(args);
    Process process;
    try {
      process = builder.start();
      StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
      Future<?> future = executorService.submit(streamGobbler);
      process.waitFor();
      process.exitValue();
      future.get(10, TimeUnit.SECONDS);

    } catch (IOException | InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
    }
    return true;
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
