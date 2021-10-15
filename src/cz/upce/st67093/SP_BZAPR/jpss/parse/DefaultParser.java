package cz.upce.st67093.SP_BZAPR.jpss.parse;

public class DefaultParser implements CLIParser {
    @Override
    public ParsedArguments parse(Options options, String[] arguments) throws BadArgumentException {
        ParsedArguments result = new ParsedArguments();

        // go through every argument
        // note: do not use for each cycle because we can jump over some arguments
        // for example if they are arguments for option
        for (int argIter = 0; argIter < arguments.length; argIter++) {
            String currArg = arguments[argIter]; // buffer current argument

            if (currArg.charAt(0) == '-') // if argument to be recognized as option
            {
                boolean foundOption = false;
                for (Option option : options.getOptions()) {
                    if (currArg.equals("-" + option.option()) || currArg.equals("--" + option.longOption())) {
                        if (option.hasArgument()) {
                            if (argIter + 1 < arguments.length) {
                                String nextArg = arguments[++argIter]; // advance iterator over argument of the option
                                result.getOptions().put(option.option(), nextArg);
                            }
                            else {
                                throw new BadArgumentException("Missing argument for -" + option.option() + " option");
                            }
                        }
                        else {
                            result.getOptions().put(option.option(), "");
                        }
                        foundOption = true;
                        break;
                    }
                }
                if (!foundOption) {
                    throw new BadArgumentException("Unrecognized option: " + currArg);
                }
            }
            else { // else if this is argument (option without option name)
                result.getArguments().add(currArg);
            }
        }

        return result;
    }
}
