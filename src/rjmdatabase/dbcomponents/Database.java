package rjmdatabase.dbcomponents;

import java.util.HashMap;
import rjmdatabase.fileutils.FileUtil;
import java.io.File;
import java.io.IOException;

/**
 * Represents a database. Currently a collection of Tables with some utility
 * methods. Singleton class to ensure that Singleton TableFileReadWriter is not
 * abused, and to restrict number of Database sessions to 1.
 * @author Rjmcf
 */
public class Database
{
    private static Database instance;

    private HashMap<String, Table> tables;
    // The path to where all the Table files will be saved.
    private String parentDirPath;
    private boolean useSerialization;


    private Database()
    { }

    /**
     * Creates a new Database using the supplied folder name and saving method.
     * @param  fN The name of the folder to store all tables under.
     * @param  uS Whether to use serialization to store the files.
     * @return    The Database instance.
     */
    public static Database createNewDatabase(String fN, boolean uS)
    {
        instance = initialiseOldDatabase(fN, uS);
        FileUtil.makeParentDirsIfNeeded(instance.parentDirPath + "dummy.txt");

        return instance;
    }

    private static Database initialiseOldDatabase(String fN, boolean uS)
    {
        if (instance == null)
            instance = new Database();

        instance.tables = new HashMap<>();
        instance.parentDirPath = fN + "/";
        instance.useSerialization = uS;

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
            table.saveTableToFile(parentDirPath, useSerialization);
        }
    }

    /**
     * Loads the database Tables from the given folder name.
     * @param  fN          The folder name to load the Tables from.
     * @param  uS          Whether to use serialization when loading the Tables.
     * @return             The loaded Database instance.
     * @throws IOException If an io exception occurred.
     */
    public static Database loadDatabase(String fN, boolean uS) throws IOException
    {
        Database result = Database.initialiseOldDatabase(fN, uS);
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
            addTable(TableFileReadWriter.readFromFile(tableName, parentDirPath, useSerialization));
        }
    }
}
