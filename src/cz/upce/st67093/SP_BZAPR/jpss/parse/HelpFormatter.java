package cz.upce.st67093.SP_BZAPR.jpss.parse;

public class HelpFormatter {
    public static void printHelp(String message, Options options) {
        // TODO: printing help
        System.out.println("usage: " + message);
        for (Option option : options.getOptions()) {
            System.out.print("-" + option.option());
            if (!option.longOption().isEmpty()) {
                System.out.print(", --" + option.longOption());
            }
            System.out.println("\t\t\t" + option.description());
        }
    }
}
