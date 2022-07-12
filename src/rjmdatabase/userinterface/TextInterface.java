package rjmdatabase.userinterface;

import rjmdatabase.dbcomponents.Database;

import java.io.IOException;
import java.util.StringJoiner;

import static rjmdatabase.userinterface.ConsoleInterfaceUtil.*;

public class TextInterface
{
    private static Database database;

    public static void main(String[] args) {
        if (args.length != 1)
        {
            println("Usage requires one parameter, the name of the database to interact with.");
            return;
        }

        String databaseFolderName = args[0];
        database = new Database(databaseFolderName);

        mainMenu();
    }

    private static void mainMenu()
    {
        int choice;
        while (true)
        {
            println("What would you like to do?");
            println("1). List tables.");
            println("2). Add table.");
            println("3). Edit table.");
            println("4). Print table.");
            println("5). Quit.");

            choice = getIntInput();

            try
            {
                switch (choice)
                {
                    case 1:
                        String[] tableNames = database.getTableNames();
                        if (tableNames.length == 0)
                        {
                            println("Currently no tables present.");
                            println();
                            break;
                        }
                        println("Tables:");
                        for (String tableName : tableNames)
                            println("    " + tableName);
                        println();
                        break;
                    case 2:
                        addTable();
                        break;
                    case 3:
                        editTable();
                        break;
                    case 4:
                        println("Enter the name of the table you'd like to print");
                        String tableName = getLineOfInputNoSpaces();
                        if (tableName == null)
                            break;
                        database.printTable(tableName);
                        break;
                    case 5:
                        if (saveDatabaseAndQuit())
                            return;
                        else
                            break;
                    default:
                        println("Please enter one of the numbers listed.");
                }
            }
            catch (Throwable e)
            {
                println("An exception or error was thrown during the database operation.");
                String msg = e.getMessage();
                if (msg != null)
                    println(msg);
                boolean shouldSave = userRespondedYes("Do you want to save the database state? Bear in mind that the database may currently be in an invalid state.");

                if (shouldSave)
                    saveDatabase();
            }
        }
    }

    // return true if saving was successful.
    private static boolean saveDatabase()
    {
        try
        {
            database.saveDatabase();
            return true;
        }
        catch (IOException e)
        {
            println("Unable to save database.");
            return false;
        }
    }

    // Returns true if we should quit after save.
    private static boolean saveDatabaseAndQuit()
    {
        boolean couldSave = saveDatabase();
        if (couldSave)
            return true;
        else
            return userRespondedYes("Do you still want to quit?");
    }

    private static void addTable()
    {
        println("Enter table name, or leave blank to return to menu:");
        String name = getLineOfInputNoSpaces();
        if (name == null)
            return;

        StringJoiner fieldNames = new StringJoiner(", ");
        String fieldName;
        while (true)
        {
            println("Input the name of a column to add, or leave blank if you're done:");
            fieldName = getLineOfInputNoSpaces();
            if (fieldName == null)
                break;
            fieldNames.add(fieldName);
        }

        try
        {
            database.addTable(name, fieldNames.toString());
            println(String.format("Table %s added.", name));
        }
        catch (IllegalArgumentException e)
        {
            println(String.format("Already a table named %s present.", name));
        }
    }

    private static void editTable()
    {
        println("Enter table name to edit, or leave blank to return to menu:");
        String name = getLineOfInputNoSpaces();
        if (name == null)
            return;

        if (!database.hasTable(name))
        {
            println(String.format("No table %s present in database.", name));
            return;
        }

        int choice;
        while (true)
        {
            println(String.format("What would you like to do with table %s?", name));
            println("1). Add a new record.");
            println("2). Search the table.");
            println("3). Update a record.");
            println("4). Delete a record.");
            println("5). Add a new column.");
            println("6). Delete a column.");
            println("7). Rename a column.");
            println("8). Rename the table.");
            println("9). Return to the main menu.");

            choice = getIntInput();

            switch (choice)
            {
                case 1:
                    addRecordToTable(name);
                    break;
                case 2:
                    println("Cannot yet search tables.");
                    break;
                case 3:
                    updateRecordInTable(name);
                    break;
                case 4:
                    deleteRecordFromTable(name);
                    break;
                case 5:
                    println("Cannot yet add a new column.");
                    break;
                case 6:
                    println("Cannot yet delete columns.");
                    break;
                case 7:
                    renameColumn(name);
                    break;
                case 8:
                    name = renameTable(name);
                    break;
                case 9:
                    return;
                default:
                    println("Please enter one of the options listed.");
            }
        }
    }

    private static void addRecordToTable(String tableName)
    {
        StringJoiner fieldJoiner = new StringJoiner(", ");
        println("Enter the value you want to store under each field name.");
        for (String fieldName : database.getFieldNamesAsArray(tableName))
        {
            println(String.format("%s:", fieldName));
            String fieldValue;
            do
            {
                fieldValue = getLineOfInput();
            }
            while (fieldValue == null);

            fieldJoiner.add(fieldValue);
        }

        database.addRecord(tableName, fieldJoiner.toString());
        println("Record added successfully.");
    }

    private static void updateRecordInTable(String tableName)
    {
        println("Please enter the key of the record you wish to update, or -1 if you wish to cancel.");
        int key = getIntInput();
        if (key == -1)
            return;

        println("Enter the name of the field whose value you wish to update, or leave blank to cancel. Your choices are:");
        String fieldNames = database.getFieldNames(tableName);
        println(fieldNames + ".");
        String fieldName = getLineOfInputNoSpaces();
        if (fieldName == null)
            return;

        println("Please enter the value you wish to save here.");
        String replacement = getLineOfInput();

        try
        {
            database.updateRecord(tableName, key, fieldName, replacement);
            println("Update completed successfully.");
        }
        catch (IndexOutOfBoundsException e)
        {
            println(e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            println(e.getMessage());
        }
    }

    private static void deleteRecordFromTable(String tableName)
    {
        println("Please enter the key of the record you wish to delete, or -1 if you wish to cancel.");
        int key = getIntInput();
        if (key == -1)
            return;

        try
        {
            database.deleteRecord(tableName, key);
            println("Record deleted successfully.");
        }
        catch (IndexOutOfBoundsException e)
        {
            println(String.format("Cannot delete record with key %d from table %s, no such record exists.", key, tableName));
        }
    }

    private static void renameColumn(String tableName)
    {
        println("What is the current name of the column? Leave blank to cancel.");
        String oldColumnName = getLineOfInputNoSpaces();
        if (oldColumnName == null)
            return;
        println("What will the new column name be?");
        String newColumnName;
        do
        {
            newColumnName = getLineOfInputNoSpaces();
        } while (newColumnName == null);

        try
        {
            database.renameColumn(tableName, oldColumnName, newColumnName);
            println("Column renamed successfully.");
        }
        catch (IllegalArgumentException e)
        {
            println(e.getMessage());
        }
    }

    private static String renameTable(String tableName)
    {
        println(String.format("What would you like to rename %s to? Leave blank to cancel.", tableName));
        String newTableName = getLineOfInputNoSpaces();
        if (newTableName == null)
            return tableName;

        database.renameTable(tableName, newTableName);
        println("Table renamed successfully.");
        return newTableName;
    }
}
