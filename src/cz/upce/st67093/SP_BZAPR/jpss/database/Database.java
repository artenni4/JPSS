package cz.upce.st67093.SP_BZAPR.jpss.database;

import cz.upce.st67093.SP_BZAPR.jpss.execute.WrongFileFormatException;
import cz.upce.st67093.SP_BZAPR.jpss.parse.BadArgumentException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {
    private DatabaseInfo info;
    private ArrayList<Record> records;
    private SealedObject sealedRecords;
    private File databaseFile;
    private String password;
    private boolean locked;

    public Database() {
        password = "";
        records = new ArrayList<>();
        locked = true;
    }

    private static Cipher getCipherFromPassword(String password, int encryptMode) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] salt = {
                (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
                (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99,
                (byte)0x47, (byte)0xa3, (byte)0x21, (byte)0x8c,
                (byte)0x0e, (byte)0xcc, (byte)0x4e, (byte)0xd3
        };
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKey tmpKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec);
        SecretKey key = new SecretKeySpec(tmpKey.getEncoded(), "AES");
        cipher.init(encryptMode, key, new IvParameterSpec(salt));
        return cipher;
    }

    @SuppressWarnings("unchecked")
    public void decryptDatabase(String password) {
        try {
            Cipher cipher = getCipherFromPassword(password, Cipher.DECRYPT_MODE);
            records = (ArrayList<Record>) sealedRecords.getObject(cipher);
            this.password = password;
            locked = false;
            System.out.println("Database is unlocked");
            return;
        }
        catch (InvalidKeyException | BadPaddingException e) {
            System.out.println("Wrong password");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        locked = true;
    }

    public void readFile(String filePath) throws BadArgumentException, WrongFileFormatException {
        System.out.println("Reading file: " + filePath);
        databaseFile = new File(filePath);

        FileInputStream in = null;
        try {
            in = new FileInputStream(databaseFile);
            ObjectInputStream objIn = new ObjectInputStream(in);
            info = (DatabaseInfo) objIn.readObject();
            sealedRecords = (SealedObject) objIn.readObject();
            objIn.close();
            in.close();
        }
        catch (IOException | ClassNotFoundException ignored) {}
        catch (ClassCastException e) {
            throw new WrongFileFormatException("Failed to recognize the database file");
        }

        if (in == null) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Database file does not exist. Would you like to create new database? (y/n): ");
            if (sc.nextLine().charAt(0) == 'y') {
                createDatabaseFile();
            }
            else {
                throw new BadArgumentException("File does not exist");
            }
        }
    }

    public void writeFile() {
        if (password.isEmpty()) {
            System.out.println("Cannot encrypt database, password is null");
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(databaseFile);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(info);

            // Encrypt records
            Cipher cipher = getCipherFromPassword(password, Cipher.ENCRYPT_MODE);
            SealedObject encryptedRecords = new SealedObject(records, cipher);

            objOut.writeObject(encryptedRecords);
            objOut.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDatabaseFile() throws BadArgumentException {
        System.out.println("Creating new database");
        boolean fileCreated = false;
        try {
            fileCreated = databaseFile.createNewFile();
        } catch (IOException ignore) {}

        if (!fileCreated) {
            throw new BadArgumentException("Cannot create file with such name");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter password for database: ");
        this.password = sc.nextLine();
        System.out.println("Enter some optional information for database");
        System.out.print("Owner of the database(enter to skip): ");
        String owner = sc.nextLine();
        System.out.print("Description of the database(enter to skip): ");
        String description = sc.nextLine();
        long dateAsSeconds = java.time.Instant.now().getEpochSecond();
        info = new DatabaseInfo(owner, description, dateAsSeconds);
        locked = false; // unlock if database is just created
    }

    public Record getRecord(int index) {
        return records.get(index);
    }

    public void addRecord(Record record) {
        records.add(record);
    }

    public void deleteRecord(int index) {
        records.remove(index);
    }

    public void updateRecord(int index, Record record) {
        records.set(index, record);
    }

    public ArrayList<Record> getAllRecords() {
        return records;
    }

    public DatabaseInfo getInfo() {
        return info;
    }

    public int getSize() {
        return records.size();
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        writeFile();
        records = new ArrayList<>();
        locked = true;
    }
}
