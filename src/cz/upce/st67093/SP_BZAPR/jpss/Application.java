package cz.upce.st67093.SP_BZAPR.jpss;

import cz.upce.st67093.SP_BZAPR.jpss.execute.Executor;
import cz.upce.st67093.SP_BZAPR.jpss.execute.WrongFileFormatException;
import cz.upce.st67093.SP_BZAPR.jpss.parse.*;

import java.util.Scanner;

/**
 * JPSS application class.
 * Contains `start` method to launch the program
 */
public class Application {
    final private Executor executor;

    public Application() {
        executor = new Executor();
    }

    /**
     * Starts main command loop of app.
     * Request user to input some commands
     */
    private void runLoop() {
        // TODO: use more reliable input or catch the exceptions
        Scanner sc = new Scanner(System.in);

        // Request user to input some commands
        while(!executor.shouldExit()) {
            // request new command
            System.out.print("==> ");
            String input = sc.nextLine();
            // Skip if input is empty
            if (input.isEmpty()) {
                continue;
            }

            // Try to execute a command
            executor.execute(input);
        }
    }

    /**
     * Starts JPSS application
     *
     * @param clArgs passed command line arguments
     */
    public void start(String[] clArgs) {
        // Handle command line arguments
        Options options = new Options();
        Option password = new Option("p", "password", true, "password from database");
        options.addOption(password);
        Option help = new Option("h", "help", false, "print help message");
        options.addOption(help);

        /*
         * Some ideas for options:
         * -u -- immediate prompting of password
         * -c -- force creating new database file
         * -x -- do not save date of creation
         */

        try {
            CLIParser parser = new DefaultParser();
            ParsedArguments parsedArgs = parser.parse(options, clArgs);

            // print help if needed and return
            if (parsedArgs.getOptionValue("h") != null) {
                HelpFormatter.printHelp("jpss FILE", options);
                return;
            }

            // Apply all passed arguments before entering command loop
            executor.applyArguments(parsedArgs);
        }
        catch (BadArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Use -h for usage hints");
            return;
        }
        catch (WrongFileFormatException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Print welcome message
        System.out.println("Welcome to JPSS!\nType help for getting started.");

        // Enter main loop
        runLoop();
    }
}
