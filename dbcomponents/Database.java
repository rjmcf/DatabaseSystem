package dbcomponents;

import java.util.HashMap;
import fileutils.TableFileReadWriter;

/**
 * Represents a database. Currently a collection of Tables with some utility
 * methods. Singleton class to ensure that Singleton TableFileReadWriter is not
 * abused, and to restrict number Database sessions to 1.
 * @author Rjmcf
 */
public class Database
{
    private static Database instance;

    //TODO Set up a folder for Database that stores the names of the tables to
    //TODO be loaded. Have a save function that saves the database and all
    //TODO associated tables, and have a load function that loads all associated
    //TODO tables. Look into incremental saving.
    private HashMap<String, Table> tables;
    // The path to where all the Table files will be saved.
    private String filePath;
    private TableFileReadWriter tableFileReadWriter;

    private Database()
    {
        tables = new HashMap<>();
    }

    /**
     * Gets the instance of the Database class, with the supplied folder name
     * and saving method.
     * @param  fN The name of the folder to store all tables under.
     * @param  uS Whether to use serialization to store the files.
     * @return    The Database instance.
     */
    public static Database getInstance(String fN, boolean uS)
    {
        if (instance == null)
            instance = new Database();

        instance.filePath = fN + "/";
        instance.tableFileReadWriter = TableFileReadWriter.getInstance(fN, uS);

        return instance;
    }

    /**
     * Add a Table to the database as long as no table with the same name is
     * already present.
     * @param t The Table to be added.
     */
    public void addTable(Table t)
    {
        String tableName = t.getName();
        if (tables.putIfAbsent(tableName, t) != null)
            throw new IllegalArgumentException("Table " + tableName + " already in database.");
    }

    /**
     * Gets the named Table if it exists in the database.
     * @param  tableName The name of the Table to be retrieved.
     * @return           The Table instance desired.
     */
    public Table getTable(String tableName)
    {
        Table t = tables.get(tableName);
        if (t == null)
            throw new IndexOutOfBoundsException("No table " + tableName + " in database");
        return t;
    }

    /**
     * Runs tests for this class.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Testing Database");
        Database db = Database.getInstance("dbTestFolder", false);
        db.test(args);
        System.out.println("Testing complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test failed");
    }

    private void test(String[] args)
    {
        Table personTable = new Table("Person", "Name, Address");
        personTable.addRecord("Robyn, XX Nilfrod Avenue\nLoughborough");
        Table animalTable = new Table("Animal", "Name, Type, Owner");
        animalTable.addRecord("Minnie, Cat, 0");

        addTable(personTable);
        addTable(animalTable);
        claim(personTable.equals(getTable("Person")));
        claim(animalTable.equals(getTable("Animal")));

        try
        {
            addTable(personTable);
            claim(false);
        }
        catch (IllegalArgumentException e)
        {
            // test passed
        }

        try
        {
            getTable("Random");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
    }

}
