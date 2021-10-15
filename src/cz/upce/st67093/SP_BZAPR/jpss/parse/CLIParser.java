package cz.upce.st67093.SP_BZAPR.jpss.parse;

import cz.upce.st67093.SP_BZAPR.jpss.execute.BadCommandException;

public interface CLIParser {
    ParsedArguments parse(Options options, String[] arguments) throws BadArgumentException;
}
