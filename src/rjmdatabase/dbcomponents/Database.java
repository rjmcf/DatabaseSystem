package rjmdatabase.dbcomponents;

import rjmdatabase.fileutils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Represents a database. Currently a collection of Tables with some utility
 * methods. Singleton class to restrict number of Database sessions to 1.
 * @author Rjmcf
 */
public class Database
{
    private static Database instance;

    private HashMap<String, Table> tables;
    // The path to where all the Table files will be saved.
    private String parentDirPath;


    private Database()
    { }

    /**
     * Creates a new Database using the supplied folder name.
     * @param  fN The name of the folder to store all tables under.
     * @return    The Database instance.
     */
    public static Database createNewDatabase(String fN)
    {
        instance = initialiseDatabase(fN);
        FileUtil.makeDirsIfNeeded(new File(instance.parentDirPath));

        return instance;
    }

    private static Database initialiseDatabase(String fN)
    {
        if (instance == null)
            instance = new Database();

        instance.tables = new HashMap<>();
        instance.parentDirPath = fN + "/";

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
     * Saves the Tables stored in this database to the correct folder.
     * @throws IOException If an io exception occurred.
     */
    public void saveDatabase() throws IOException
    {
        for (Table table : tables.values())
        {
            table.saveTableToFile(parentDirPath);
        }
    }

    /**
     * Loads the database Tables from the given folder name.
     * @param  fN          The folder name to load the Tables from.
     * @return             The loaded Database instance.
     * @throws IOException If an io exception occurred.
     */
    public static Database loadDatabase(String fN) throws IOException
    {
        Database result = Database.initialiseDatabase(fN);
        result.loadTablesFromFile();
        return result;
    }

    private void loadTablesFromFile() throws IOException
    {
        File parentDir = new File(parentDirPath);
        if (!parentDir.exists())
            throw new IllegalArgumentException("Folder " + parentDirPath + " does not exist");
        String tableName;
        for (File tableFile : parentDir.listFiles())
        {
            tableName = TableFileReadWriter.getTableNameFromFileName(tableFile.getName());
            if (tableName == null)
                continue;
            addTable(TableFileReadWriter.readFromFile(tableName, parentDirPath));
        }
    }
}
