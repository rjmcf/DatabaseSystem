package rjmdatabase.userinterface;

import rjmdatabase.dbcomponents.Database;
import java.util.Scanner;
import java.util.StringJoiner;
import java.io.IOException;

public class TextInterface
{
    private static Scanner in;
    private static Database database;

    public static void main(String[] args) {
        if (args.length != 1)
        {
            System.out.println("Usage requires one parameter, the name of the database to interact with.");
            return;
        }

        String databaseFolderName = args[0];
        database = new Database(databaseFolderName);

        mainMenu();
    }

    private static void println(String s)
    {
        System.out.println(s);
    }

    private static void mainMenu()
    {
        in = new Scanner(System.in);
        int choice;
        while (true)
        {
            println("What would you like to do?");
            println("1). List tables.");
            println("2). Add table.");
            println("3). Edit table.");
            println("4). Print table.");
            println("5). Quit.");

            while (!in.hasNextInt()) {
                in.next();
            }
            choice = in.nextInt();

            switch (choice)
            {
                case 1:
                    String[] tableNames = database.getTableNames();
                    if (tableNames.length == 0)
                    {
                        println("Currently no tables present.");
                        break;
                    }
                    println("Tables:");
                    for (String tableName : tableNames)
                        println("    " + tableName);
                    break;
                case 2:
                    addTable();
                    break;
                case 3:
                    editTable();
                    break;
                case 4:
                    println("Error: cannot yet print tables.");
                    break;
                case 5:
                    try
                    {
                        database.saveDatabase();
                    }
                    catch (IOException e)
                    {
                        println("Unable to save database, do you still want to quit (Y/N)?");
                        if (!"Y".equals(in.next().toUpperCase()))
                            break;
                    }
                    in.close();
                    return;
                default:
                    println("Please enter one of the numbers listed.");
            }
        }
    }

    private static void addTable()
    {
        println("Enter table name, or leave blank to return to menu:");
        in.nextLine();
        String name = in.nextLine().replaceAll("\\s","");
        if (name.equals(""))
            return;
        StringJoiner fieldNames = new StringJoiner(", ");
        String fieldName;
        while (true)
        {
            println("Input the name of a column to add, or leave blank if done:");
            in.nextLine();
            fieldName = in.nextLine().replaceAll("\\s","");
            if (fieldName.equals(""))
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
        in.nextLine();
        String name = in.nextLine().replaceAll("\\s","");
        if (name.equals(""))
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
            println("4). Add a new column.");
            println("5). Delete a column.");
            println("6). Rename a column.");
            println("7). Rename the table.");
            println("8). Return to the main menu.");

            while (!in.hasNextInt()) {
                in.next();
            }
            choice = in.nextInt();

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
                    println("Cannot yet add a new column.");
                    break;
                case 5:
                    println("Cannot yet delete columns.");
                    break;
                case 6:
                    println("Cannot yet rename columns.");
                    break;
                case 7:
                    println("Cannot yet rename tables.");
                    break;
                case 8:
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
        for (String fieldName : database.getFieldNames(tableName).split(", "))
        {
            println(String.format("%s:", fieldName));
            fieldJoiner.add(in.nextLine());
        }

        database.addRecord(tableName, fieldJoiner.toString());
        println("Record added successfully.");
    }
}
