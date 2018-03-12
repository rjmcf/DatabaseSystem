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
                    println("Which table do you want to print?");
                    String tableName = in.next();
                    try
                    {
                        database.getTable(tableName);
                        println("Error: cannot yet print tables.");
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        println("No table named " + tableName + " present.");
                    }
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
            println("Error: cannot yet add table.");
            //database.addTable(name, fieldNames.toString());
            //println(String.format("Table %s added.", name));
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

        try
        {
            database.getTable(name);
            println("Error: cannot yet edit tables.");
        }
        catch (IllegalArgumentException e)
        {
            println(String.format("No table named %s present.", name));
        }
    }
}
