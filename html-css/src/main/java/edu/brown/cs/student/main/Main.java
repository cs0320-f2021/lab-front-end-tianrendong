package edu.brown.cs.student.main;

import freemarker.template.Configuration;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static Autocorrector ac;

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {

    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    parser.accepts("prefix");
    parser.accepts("whitespace");
    OptionSpec<Integer> ledSpec =
        parser.accepts("led").withRequiredArg().ofType(Integer.class);
    OptionSpec<String> dataSpec =
        parser.accepts("data").withRequiredArg().ofType(String.class);

    OptionSet options = parser.parse(args);
    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }
    if (options.has("data")) {
      boolean prefix = false;
      boolean whitespace = false;
      int led = 0;

      String files = options.valueOf(dataSpec);
      if (options.has("prefix")) {
        prefix = true;
      }
      if (options.has("whitespace")) {
        whitespace = true;
      }
      if (options.has("led")) {
        led = (int) options.valueOf(ledSpec);
      }

      // Create autocorrector using files and flags passed in.
      ac = new Autocorrector(files, prefix, whitespace, led);

      // For each line of input from user, output autocorrect suggestions.
      try (BufferedReader br = new BufferedReader(
          new InputStreamReader(System.in))) {
        String input;
        while ((input = br.readLine()) != null) {
          Set<String> suggestions = ac.suggest(input);
          for (String s : suggestions) {
            System.out.println(s);
          }
        }
        br.close();
      } catch (Exception e) {
        System.out.println("ERROR: Invalid input for REPL");
      }
    } else {
      System.out.println("ERROR: usage");
      System.out.print(
          "./run --data=<list of files> \n[--prefix] [--whitespace] [--led=<led>]\n[--gui] [--port=<port>]\n");
    }
  }

  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * * IMPLEMENT METHOD runSparkServer() HERE
   */
  private void runSparkServer(int port) {
    // TODO
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }

  /**
   *  IMPLEMENT AutocorrectHandler HERE
   *
   *  A handler to produce our autocorrect service site.
   *  @return ModelAndView to render.
   *  (autocorrect.ftl).
   */

  /**
   *  IMPLEMENT SubmitHandler HERE
   *
   *  A handler to print out suggestions once form is submitted.
   *  @return ModelAndView to render.
   *  (autocorrect.ftl).
   */

}
