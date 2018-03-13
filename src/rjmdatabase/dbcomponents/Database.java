package rjmdatabase.dbcomponents;

import rjmdatabase.fileutils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Represents a database. Currently a collection of Tables with some utility
 * methods.
 * @author Rjmcf
 */
public class Database
{
    private HashMap<String, Table> tables;
    // The path to where all the Table files will be saved.
    private String parentDirPath;

    /**
     * Creates a new Database using the supplied folder name.
     * @param fN The name of the folder to store all tables under.
     */
    public Database(String fN)
    {
        tables = new HashMap<>();
        parentDirPath = fN + "/";
        FileUtil.makeDirsIfNeeded(new File(parentDirPath));
        try
        {
            loadTablesFromFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new Error("Unable to load table files.");
        }
    }

    /**
     * Gets an array containing the names of the Tables stored in this database.
     * @return The array of table names.
     */
    public String[] getTableNames()
    {
        return tables.keySet().toArray(new String[0]);
    }

    /**
     * Add a Table to the database as long as no table with the same name is
     * already present.
     * @param t The Table to be added.
     */
    public void addTable(String tableName, String fieldNames)
    {
        Table t = new Table(tableName, fieldNames);
        addTable(t);
    }

    private void addTable(Table t)
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
    Table getTable(String tableName)
    {
        Table t = tables.get(tableName);
        if (t == null)
            throw new IndexOutOfBoundsException("No table " + tableName + " in database");
        return t;
    }

    /**
     * Gets whether a named Table appears in this database.
     * @param  tableName The name of the Table to search for.
     * @return           Whether the Table is present.
     */
    public boolean hasTable(String tableName)
    {
        return tables.keySet().contains(tableName);
    }

    public String getFieldNames(String tableName)
    {
        return getTable(tableName).getFieldNames();
    }

    public void addRecord(String tableName, String fields)
    {
        Table table = getTable(tableName);
        table.addRecord(fields);
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
