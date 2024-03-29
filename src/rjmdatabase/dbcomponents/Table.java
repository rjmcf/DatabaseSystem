package rjmdatabase.dbcomponents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Represents a Table, which stores Records.
 * @author Rjmcf
 */
public class Table
{
    // The actual name of the first column, representing the Key for the table.
    static final String KEY_COL_NAME = "KeyTable";
    // The version number of this table. Will change when new code needs to be
    // written to save and load from file.
    static final String version = "1.0";

    // The name of this table.
    private String name;
    // The next key that will be assigned to a Record. All keys are unique within
    // a table.
    private int nextKey;
    // The names of the fields stored by Records.
    private ArrayList<String> fieldNames;
    // The map of keys to Records constituting the actual Table.
    private HashMap<Integer, Record> table;
    // Whether the Table needs saving back to file
    private boolean isDirty = true;

    /**
     * Factory method to load a Table from the data read in from a file.
     * @param  name The name of the Table.
     * @param  data The data read from the Table file.
     * @return      The Table instance.
     */
    static Table createTableFromData(String name, String[][] data)
    {
        // The first line is the version number.
        if (!version.equals(data[0][0]))
        {
            throw new Error("Attempted to load old version of Table. This is not possible at present.");
        }
        // The second line is the human readable field names. We build the
        // comma separated list of field names for the Table constructor.
        String[] keyAndAttrs = data[1];
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 1; i < keyAndAttrs.length; i++)
            joiner.add(keyAndAttrs[i]);

        // The new Table instance.
        Table t = new Table(name, joiner.toString());

        // Now add the Records one at a time.
        for (int row = 2; row < data.length; row++)
        {
            String[] recordFields = data[row];
            String[] fields = new String[recordFields.length - 1];
            int key = Integer.parseInt(recordFields[0]);
            // Field 0 in Record = Field 1 in recordFields
            for (int f = 1; f < recordFields.length; f++)
                fields[f - 1] = recordFields[f];
            t.insertRecord(key, fields);
        }

        // We have just read from file, so no need to save it again right now.
        t.isDirty = false;
        return t;
    }

    /**
     * Creates a new Table with the specified name, and a comma separated list
     * of column names.
     * @param name   The name of the new Table.
     * @param fNames A comma separated list of the Table's new column names.
     */
    Table(String name, String fNames)
    {
        this.name = name;
        nextKey = 0;
        if (fNames.equals(""))
            fieldNames = new ArrayList<>();
        else
        {
            String[] fNameArray = fNames.split(", ");
            fieldNames = new ArrayList<>(Arrays.asList(fNameArray));
        }
        table = new HashMap<>();
    }

    /**
     * Gets the name of the Table.
     * @return The Table's name.
     */
    String getName()
    {
        return name;
    }

    /**
     * Returns a comma separated list of the field names.
     * @return the field names.
     */
    String getFieldNames()
    {
        StringJoiner names = new StringJoiner(", ");
        for (String name : fieldNames)
            names.add(name);

        return names.toString();
    }

    // Gets the key that will be assigned to the next Record which is added.
    private int getNextKey()
    {
        return nextKey;
    }

    /**
     * Gets whether this Table needs saving or not.
     * @return whether the Table is dirty.
     */
    boolean getIsDirty()
    {
        return isDirty;
    }

    // Finds the largest key assigned so far, and sets the next key to be one
    // larger than it. This will be called when we try to add a new Record and
    // discover that the next key is out of sync with the Records in the table.
    private void setNextKeyBasedOnRecords()
    {
        Set<Integer> keySet = table.keySet();
        nextKey = Collections.max(keySet) + 1;
    }

    // Gets a list of all the keys stored in the Table.
    private ArrayList<Integer> getAllKeys()
    {
        return new ArrayList<>(table.keySet());
    }

    /**
     * Gets the number of fields stored by Records. Also the number of columns
     * not including the key column.
     * @return the number of fields.
     */
    int getNumFields()
    {
        return fieldNames.size();
    }

    /**
     * Gets the number of Records stored by the Table.
     * @return the size of the Table.
     */
    int getNumRecords()
    {
        return table.size();
    }

    /**
     * Adds a new Record to the Table under a unique key, as long as there are
     * the right number of fields provided.
     *
     * An array is used rather than an ArrayList because arrays are easier to
     * build inline on the fly, such as new String[]{"Field1", "Field2",..., "FieldN"};
     * The equivalent with ArrayList takes N + 1 lines.
     * @param fs The values of the fields to be saved.
     */
    void addRecord(String[] fs)
    {
        ArrayList<String> fields = new ArrayList<>(Arrays.asList(fs));
        int numFieldsInTable = getNumFields();
        if (fields.size() != numFieldsInTable)
        {
            String errorMsg = String.format("Expected %d fields but got %d" , numFieldsInTable, fields.size());
            throw new IllegalArgumentException(errorMsg);
        }
        // HashMap.putIfAbsent returns null only if the key wasn't already
        // assigned in the map. Thus if the key has been assigned, we need to
        // update the next key and try again.
        Record newRecord = new Record(fields);
        while (table.putIfAbsent(nextKey, newRecord) != null)
            setNextKeyBasedOnRecords();
        nextKey++;

        isDirty = true;
    }

    /**
     * Utility method to allow you to add Records by giving a comma separated
     * list of field values.
     * @param fs The comma separated list of field values.
     */
    void addRecord(String fs)
    {
        addRecord(fs.split(", "));
    }

    /**
     * Inserts a new Record at the given key, as long as it has not already been
     * assigned. Typically only for use by code that creates a Table from a file,
     * as there may be keys missing from such a Table.
     * @param key The key at which insertion is attempted.
     * @param fs  The fields to insert as a new Record.
     */
    private void insertRecord(int key, String[] fs)
    {
        if (key < 0)
            throw new IllegalArgumentException("Key must be non-negative");
        ArrayList<String> fields = new ArrayList<>(Arrays.asList(fs));
        int numFieldsInTable = getNumFields();
        if (fields.size() != numFieldsInTable)
        {
            String errorMsg = String.format("Expected %d fields but got %d" , numFieldsInTable, fields.size());
            throw new IllegalArgumentException(errorMsg);
        }
        // Don't bother updating nextKey and trying again here, as the user
        // specified this key for a reason.
        if (table.putIfAbsent(key, new Record(fields)) != null)
            throw new IllegalArgumentException("There already exists a record with that key");
        nextKey = key + 1;
        isDirty = true;
    }

    /**
     * Gets the Record stored under the supplied key if it exists.
     * @param  key They key of the Record.
     * @return     The Record itself.
     */
    Record getRecord(int key)
    {
        if (!table.containsKey(key))
            throw new IndexOutOfBoundsException("No record found with that key");

        return table.get(key);
    }

    /**
     * Updates a Record with a given key by changing the value under the given
     * field name, if it exists.
     * @param key         The key of the Record to update.
     * @param fieldName   The name of the field to update.
     * @param replacement The new value.
     */
    void updateRecord(int key, String fieldName, String replacement)
    {
        // Fields are accessed by index in records, so we need the index of the
        // field name to refer to it by.
        int fieldIndex = fieldNames.indexOf(fieldName);
        if (fieldIndex == -1)
            throw new IllegalArgumentException("No attribute: " + fieldName + " exists");
        getRecord(key).updateField(fieldIndex, replacement);
        isDirty = true;
    }

    /**
     * Deletes the record with the specified key. This key will be unused from
     * now on, unless insertRecord is called.
     * @param key The key of the Record to be deleted.
     */
    void deleteRecord(int key)
    {
        if (table.remove(key) == null)
            throw new IndexOutOfBoundsException("No record found with that key");
        isDirty = true;
    }

    /**
     * Adds a column to the Table. The default value is added to every Record. Throws
     * an exception if the index is out of bounds.
     * @param name       The name of the new column.
     * @param defaultVal The default value to be added to every Record for this
     *                   new field.
     */
    void addColumn(int index, String name, String defaultVal)
    {
        if (index < 0 || index > fieldNames.size())
            throw new IndexOutOfBoundsException(String.format("Cannot insert new column at index %d", index));

        fieldNames.add(index, name);
        for (Record r: table.values())
            r.addField(index, defaultVal);
        isDirty = true;
    }

    /**
     * Renames a column in the Table, as long as the new name does not clash with
     * another column.
     * @param oldName The name of the column to be renamed.
     * @param newName The new name for the column.
     */
    void renameColumn(String oldName, String newName)
    {
        if (fieldNames.contains(newName))
            throw new IllegalArgumentException("Already a column named " + newName);
        int index = fieldNames.indexOf(oldName);
        if (index == -1)
            throw new IndexOutOfBoundsException("No column with name " + oldName);
        fieldNames.set(index, newName);
        isDirty = true;
    }

    /**
     * Deletes a column from the table, and the associated value from every Record.
     * @param name The name of the column to be deleted.
     */
    void deleteColumn(String name)
    {
        int index = fieldNames.indexOf(name);
        if (index == -1)
            throw new IllegalArgumentException("No column with name " + name);

        fieldNames.remove(index);
        for (Record r: table.values())
            r.deleteField(index);
        isDirty = true;
    }

    /**
     * Renames the Table.
     * @param newName The new name.
     */
    void rename(String newName)
    {
        name = newName;
        isDirty = true;
    }

    /**
     * Builds a matrix of Strings that represent the Table. The first line gives
     * the names of the fields. Every line after that gives the values of those
     * fields for a particular Record.
     * @return The PrintInfo instance.
     */
    private String[][] getTableData()
    {
        // We need space for all the Records, the names of the fields, and the
        // version number.
        String[][] tableData = new String[getNumRecords() + 2][];
        // First line stores our version number.
        tableData[0] = new String[]{version};
        // Need to have the first col name be the key col name.
        ArrayList<String> colNames = new ArrayList<>();
        colNames.add(KEY_COL_NAME);
        colNames.addAll(fieldNames);
        tableData[1] = colNames.toArray(new String[0]);

        // Record 0 is row 2.
        int counter = 2;
        ArrayList<String> fields;
        for (Map.Entry<Integer, Record> entry : table.entrySet())
        {
            fields = new ArrayList<>();
            int key = entry.getKey();
            fields.add(Integer.toString(key));
            for (int i = 0; i < getNumFields(); i ++)
            {
                Record r = entry.getValue();
                String field = r.getField(i);
                fields.add(field);
            }
            tableData[counter++] = fields.toArray(new String[0]);
        }
        return tableData;
    }

    /**
     * Prints the Table.
     */
    void printTable()
    {
        TablePrinter.printTable(name, getTableData());
    }

    /**
     * Saves the Table to a file in the specified folder.
     * @param  parentFolderPath the name of the folder in which to store the Table.
     * @throws IOException      when something goes wrong while writing.
     */
    void saveTableToFile(String parentFolderPath) throws IOException
    {
        if (isDirty)
        {
            TableFileReadWriter.writeToFile(name, getTableData(), parentFolderPath);
            isDirty = false;
        }
    }

    /**
     * Overrides Object.equals(Object that) so that Tables can be compared.
     * @param  that The Table to be compared with.
     * @return      Whether the Tables are equal.
     */
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (!(that instanceof Table)) return false;
        Table thatTable = (Table)that;
        String thatTableName = thatTable.name;
        if (!name.equals(thatTableName)) return false;
        // Tables are equal only if they share the same field names.
        String thatTableFields = thatTable.getFieldNames();
        if (!getFieldNames().equals(thatTableFields)) return false;
        int numRecords = getNumRecords();
        if (numRecords != thatTable.getNumRecords()) return false;

        // Tables are equal if equal Records are stored under every key.
        try
        {
            for (int key : table.keySet())
            {
                Record thisRecord = table.get(key);
                Record thatRecord = thatTable.getRecord(key);
                if (!thisRecord.equals(thatRecord))
                    return false;
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            // They had the same number of keys, but one of the keys didn't
            // appear in thatTable, so we got an exception.
            return false;
        }

        return true;
    }
}
