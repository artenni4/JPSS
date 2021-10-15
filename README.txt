Java Password Storing System (JPSS) is a CLI program for storing passwords.

Steps to MVP:
1. Parsing command line arguments
 * Parse command arguments with spaces (open "my file.txt" should be command open with one argument my file.txt)
 * Catching exceptions that throws Scanner
2. 'Requesting commands' loop
 * Response to any command that user inputs
3. State machine for choosing folders, creating new records
 * Dialog with user to do some actions with database
4. Encrypting passwords
5. Writing passwords to file
 * Use binary output to file
6. Reading file
 * Use binary input from file

Possible commands:
info - get info about database.
list - print records in current folder.
create [record, folder] - create record or folder in database.
show [record, folder] - show info about folder or record in database.
delete [record, folder] - delete record or folder in database.
edit [record, folder] - edit information of record or folder.
exit - exit program.
help - output possible commands and their meaning.

Feature ideas:
hide all passwords
 * Enter password with asterisks
 * Do not show raw password in console output, maybe copy to clipboard
pass parameters to commands, for faster and more convenient usage
create folders system
