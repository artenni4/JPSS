package cz.upce.st67093.SP_BZAPR.jpss.parse;

import java.util.ArrayList;
import java.util.HashMap;

public class ParsedArguments {
    final private HashMap<String, String> options;
    final private ArrayList<String> arguments;

    public ParsedArguments() {
        options = new HashMap<>();
        arguments = new ArrayList<>();
    }

    public HashMap<String, String> getOptions() {
        return options;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public String getOptionValue(String optionName) {
        return options.get(optionName);
    }

    public String getArgumentValueByIndex(int index) {
        return arguments.get(index);
    }

    public boolean hasArgumentOfIndex(int index) {
        return (index < arguments.size());
    }
}
