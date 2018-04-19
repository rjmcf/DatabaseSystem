package rjmdatabase.dbcomponents;

import rjmdatabase.fileutils.FileUtil;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Represents a database. Currently a collection of Tables with some utility methods.
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
        File parentDir = new File(parentDirPath);
        FileUtil.makeDirsIfNeeded(parentDir);
        try
        {
            loadTablesFromFile(parentDir);
        }
        catch (IOException e)
        {
            throw new Error("Unable to load table files.");
        }
    }

    /**
     * Gets an array containing the names of the Tables stored in this database.
     * @return The array of table names.
     */
    public String[] getTableNames()
    {
        Set<String> tableNames = tables.keySet();
        return tableNames.toArray(new String[0]);
    }

    /**
     * Add a Table to the database as long as no table with the same name is already present.
     * @param tableName  The name of the Table to add.
     * @param fieldNames The field names of the Table.
     */
    public void addTable(String tableName, String fieldNames)
    {
        Table t = new Table(tableName, fieldNames);
        addTable(t);
    }

    // Actually add the table to the database.
    private void addTable(Table t)
    {
        String tableName = t.getName();
        // putIfAbsent returns null if key is not already present.
        if (tables.putIfAbsent(tableName, t) != null)
            throw new IllegalArgumentException("Table " + tableName + " already in database.");
    }

    // Gets the named Table if it exists in the database.
    Table getTable(String tableName)
    {
        Table t = tables.get(tableName);
        if (t == null)
            throw new IndexOutOfBoundsException("No table " + tableName + " in database");
        return t;
    }

    /**
     * Renames a Table if it exists in the database.
     * @param tableName    The old name of the Table.
     * @param newTableName The new name of the Table.
     */
    public void renameTable(String tableName, String newTableName)
    {
        Set<String> tableNames = tables.keySet();
        if (tableNames.contains(newTableName))
            throw new IllegalArgumentException("Cannot rename Table, names would clash.");
        Table t = getTable(tableName);
        t.rename(newTableName);
        // Delete the old file so you can't load both versions of the table at once.
        TableFileReadWriter.deleteTableFile(tableName, parentDirPath);
        // Remove the old key from the hashmap.
        tables.remove(tableName);
        // Store the table under the new key.
        tables.put(newTableName, t);
    }

    /**
     * Gets whether a named Table appears in this database.
     * @param  tableName The name of the Table to search for.
     * @return           Whether the Table is present.
     */
    public boolean hasTable(String tableName)
    {
        Set<String> tableNames = tables.keySet();
        return tableNames.contains(tableName);
    }

    /**
     * Gets a list of the field names for the given table.
     * @param  tableName The name of the Table.
     * @return           The list of the field names.
     */
    public String[] getFieldNames(String tableName)
    {
        Table theTable = getTable(tableName);
        String fieldNames = theTable.getFieldNames();
        return fieldNames.split(", ");
    }

    /**
     * Adds the record with the supplied fields to the specified table.
     * @param tableName The name of the Table to add the record to.
     * @param fields    The fields to create the Record out of.
     */
    public void addRecord(String tableName, String fields)
    {
        Table table = getTable(tableName);
        table.addRecord(fields);
    }

    /**
     * Prints the specified Table.
     * @param tableName The name of the Table.
     */
    public void printTable(String tableName)
    {
        Table t = getTable(tableName);
        t.printTable();
    }

    /**
     * Renames a column from the specified Table.
     * @param tableName     The name of the Table whose column is being renamed.
     * @param oldColumnName The old name of the column.
     * @param newColumnName The new name of the column.
     */
    public void renameColumn(String tableName, String oldColumnName, String newColumnName)
    {
        Table t = getTable(tableName);
        t.renameColumn(oldColumnName, newColumnName);
    }

    /**
     * Deletes a Record from the chosen Table.
     * @param tableName The table to delete from.
     * @param key       The key of the Record to delete.
     */
    public void deleteRecord(String tableName, int key)
    {
        Table t = getTable(tableName);
        t.deleteRecord(key);
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

    // Goes through all table files in parentDir and loads the Tables found.
    private void loadTablesFromFile(File parentDir) throws IOException
    {
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
