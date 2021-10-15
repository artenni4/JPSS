package cz.upce.st67093.SP_BZAPR.jpss.parse;

import java.util.ArrayList;
import java.util.Arrays;

public class Options {
    final private ArrayList<Option> options;

    public Options() {
        options = new ArrayList<>();
    }

    public Options(Option[] options) {
        this.options = new ArrayList<>(Arrays.asList(options));
    }

    public ArrayList<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        options.add(option);
    }
}
