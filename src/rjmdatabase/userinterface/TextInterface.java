package rjmdatabase.userinterface;

import rjmdatabase.dbcomponents.Database;
import static rjmdatabase.userinterface.ConsoleInterfaceUtil.*;
import java.util.StringJoiner;
import java.io.IOException;

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
                    try
                    {
                        database.saveDatabase();
                    }
                    catch (IOException e)
                    {
                        if (!userRespondedYes("Unable to save database, do you still want to quit?"))
                            break;
                    }
                    return;
                default:
                    println("Please enter one of the numbers listed.");
            }
        }
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
                    println("Cannot yet update records.");
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
        for (String fieldName : database.getFieldNames(tableName))
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
