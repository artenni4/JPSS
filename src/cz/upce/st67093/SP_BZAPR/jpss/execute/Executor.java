package cz.upce.st67093.SP_BZAPR.jpss.execute;

import cz.upce.st67093.SP_BZAPR.jpss.database.Database;
import cz.upce.st67093.SP_BZAPR.jpss.database.DatabaseInfo;
import cz.upce.st67093.SP_BZAPR.jpss.database.Record;
import cz.upce.st67093.SP_BZAPR.jpss.parse.*;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for interaction with ApplicationState.
 * Main Application class should delegate all
 * command execution and changing app state to
 * this class
 */
public class Executor {
    private final Database database;
    boolean exit;

    public Executor() {
        this.database = new Database();
        this.exit = false;
    }

    public boolean shouldExit() {
        return exit;
    }

    /**
     * Applies parsed command line arguments to the state of the app.
     */
    public void applyArguments(ParsedArguments parsedArgs) throws BadArgumentException, WrongFileFormatException {
        // Read file if specified in argument list
        boolean shouldOpenFile = parsedArgs.hasArgumentOfIndex(0);

        if (shouldOpenFile) {
            String filePath = parsedArgs.getArgumentValueByIndex(0);
            database.readFile(filePath);

            String password = parsedArgs.getOptionValue("p");
            if (password != null) {
                System.out.println("Trying password");
                database.decryptDatabase(password);
            }
        }
        else {
            throw new BadArgumentException("You should specify database file");
        }
    }

    /**
     * Executes parsed command and applies changes to application state
     */
    public void execute(String input) {
        // Check if command is allowed
        switch (input) {
            case "create":
            case "list":
            case "show":
            case "edit":
            case "delete":
            case "lock":
                if (database.isLocked()) {
                    System.out.println("Database is locked. Use unlock");
                    return;
                }
        }

        // TODO: pass arguments to command using CLIParser and Options
        switch (input) {
            case "create" -> createCommand();
            case "list" -> listCommand();
            case "show" -> showCommand();
            case "edit" -> editCommand();
            case "delete" -> deleteCommand();
            case "lock" -> lockCommand();
            case "unlock" -> unlockCommand();
            case "info" -> infoCommand();
            case "help" -> {
                System.out.println("List of possible commands:");
                System.out.println("create\tcreates record in database");
                System.out.println("list\tshows list of records in database");
                System.out.println("show\tshows specific record info");
                System.out.println("edit\tedits specific record");
                System.out.println("delete\tdeletes specific record");
                System.out.println("unlock\tunlocks database");
                System.out.println("lock\tlocks database");
                System.out.println("info\tprint saved info about database");
                System.out.println("help\tprints this message");
            }
            case "exit" -> {
                if (!database.isLocked()) {
                    lockCommand();
                }
                exit = true;
                System.out.println("Bye!");
            }
            default -> System.out.println("Invalid command! Type help to get list of commands");
        }
    }

    private void createCommand() {
        Scanner sc = new Scanner(System.in);
        String title = "", password = "", accountName, description;

        while(title.isEmpty()) {
            System.out.print("Title: ");
            title = sc.nextLine();
        }

        while (password.isEmpty()) {
            System.out.print("Password: ");
            password = sc.nextLine();
        }

        System.out.print("Account: ");
        accountName = sc.nextLine();

        System.out.print("Description: ");
        description = sc.nextLine();

        database.addRecord(new Record(title, password, accountName, description));
    }

    private void listCommand() {
        System.out.println("All records in database:");
        ArrayList<Record> records = database.getAllRecords();

        if (records.size() != 0) {
            System.out.println("#\tTitle\tDescription");
        }
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            System.out.println(i + ": " + record.title() + " > \t" + record.description());
        }
        System.out.println("Total count: " + database.getSize());
    }

    private void showCommand() {
        if (database.getSize() == 0) {
            System.out.println("Database is empty");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter index of record: ");
        int index = sc.nextInt();
        if (index >= 0 && index < database.getSize()) {
            Record record = database.getRecord(index);
            System.out.println("Index: " + index);
            System.out.println("Title: " + record.title());
            // TODO: hide password, for example copy it to clipboard
            System.out.println("Password: " + record.password());
            System.out.println("Account: " + record.accountName());
            System.out.println("Description: " + record.description());
        }
        else {
            System.out.println("Record with such index does not exist");
        }
    }

    private void deleteCommand() {
        if (database.getSize() == 0) {
            System.out.println("Database is empty");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter index of the record: ");
        int index = sc.nextInt();

        if (index >= 0 && index < database.getSize()) {
            database.deleteRecord(index);
            System.out.println("Record is deleted");
        }
        else {
            System.out.println("Record with such index does not exist");
        }
    }

    private void editCommand() {
        if (database.getSize() == 0) {
            System.out.println("Database is empty");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter index of the record: ");
        int index = sc.nextInt();

        if (index >= 0 && index < database.getSize()) {
            Record currRecord = database.getRecord(index);
            System.out.println("New title(enter for same): ");
            String title = sc.nextLine();
            System.out.println("New password(enter for same): ");
            String password = sc.nextLine();
            System.out.println("New account(enter for same): ");
            String account = sc.nextLine();
            System.out.println("New description(enter for same): ");
            String description = sc.nextLine();

            if (title.isEmpty()) {
                title = currRecord.title();
            }
            if (password.isEmpty()) {
                password = currRecord.password();
            }
            if (account.isEmpty()) {
                account = currRecord.accountName();
            }
            if (description.isEmpty()) {
                description = currRecord.description();
            }

            database.updateRecord(index, new Record(title, password, account, description));
            System.out.println("Record is edited");
        }
        else {
            System.out.println("Record with such index does not exist");
        }
    }

    private void lockCommand() {
        database.lock();
        System.out.println("Database is locked");
    }

    private void unlockCommand() {
        if (!database.isLocked()) {
            System.out.println("Database is already unlocked");
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter password for database: ");
        String password = sc.nextLine();
        database.decryptDatabase(password);
    }

    private void infoCommand() {
        DatabaseInfo info = database.getInfo();
        System.out.println("Information about database:");
        System.out.println("Owner: " + info.owner());
        System.out.println("Date of creation: " + info.dateAsString());
        System.out.println("Description: " + info.description());
    }
}
