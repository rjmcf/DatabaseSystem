package dbcomponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.Collections;


/**
 * Represents a Table, which stores Records. Implements Serializable in order to
 * allow saving to a file by serialization.
 * @author Rjmcf
 */
public class Table implements java.io.Serializable
{
    /**
     * The actual name of the first column, representing the Key for the table.
     */
    public static final String KEY_COL_NAME = "KeyTable";

    // The name of this table.
    private String name;
    // The next key that will be assigned to a Record. All keys are unique within
    // a table.
    private int nextKey;
    // The names of the fields stored by Records.
    private ArrayList<String> fieldNames;
    // The map of keys to Records constituing the actual Table.
    private HashMap<Integer, Record> table;

    /**
     * Creates a new Table with the specified name, and a comma separated list
     * of column names.
     * @param name  The name of the new Table.
     * @param fNames A comma separated list of the Table's new column names.
     */
    public Table(String name, String fNames)
    {
        this.name = name;
        nextKey = 0;
        fieldNames = new ArrayList<>(Arrays.asList(fNames.split(", ")));
        table = new HashMap<>();
    }

    /**
     * Gets the name of the Table.
     * @return The Table's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the key that will be assigned to the next Record which is added.
     * @return The next key to be assigned.
     */
    public int getNextKey()
    {
        return nextKey;
    }

    // Finds the largest key assigned so far, and sets the next key to be one
    // larger than it. This will be called when we try to add a new Record and
    // discover that the next key is out of sync with the Records in the table.
    private void setNextKeyBasedOnRecords()
    {
        nextKey = Collections.max(table.keySet()) + 1;
    }

    private ArrayList<Integer> getAllKeys()
    {
        return new ArrayList<>(table.keySet());
    }

    /**
     * Gets the number of fields stored by Records. Also the number of columns
     * not including the key column.
     * @return The number of fields.
     */
    public int getNumFields()
    {
        return fieldNames.size();
    }

    /**
     * Gets a comma separated list of the names of the fields stored by Records.
     * @return The names of the fields.
     */
    public String getFieldNames()
    {
        StringJoiner joiner = new StringJoiner(", ");
        for (String s: fieldNames)
            joiner.add(s);
        return joiner.toString();
    }

    /**
     * Gets the number of Records stored by the Table.
     * @return The number of Records.
     */
    public int getNumRecords()
    {
        return table.size();
    }

    /**
     * Adds a new Record to the Table under a unique key, as long as there are
     * the right number of fields provided.
     * <p>
     * An array is used rather than an
     * ArrayList because arrays are easier to build inline on the fly, such as
     * new String[]{"Field1", "Field2",..., "FieldN"}; The equivalent with
     * ArrayList takes N + 1 lines.
     * @param fs The values of the fields to be saved.
     */
    public void addRecord(String[] fs)
    {
        ArrayList<String> fields = new ArrayList<>(Arrays.asList(fs));
        if (fields.size() != getNumFields())
            throw new IllegalArgumentException("Expected " +
                Integer.toString(getNumFields()) + " fields but got " +
                Integer.toString(fields.size())
            );
        // HashMap.putIfAbsent returns null only if the key wasn't already
        // assigned in the map. Thus if the key has been assigned, we need to
        // update the next key.
        while (table.putIfAbsent(nextKey, new Record(fields)) != null)
            setNextKeyBasedOnRecords();
        nextKey++;
    }

    /**
     * Utility method to allow you to add Records by giving a comma separated
     * list of field values.
     * @param fs The comma separated list of field values.
     */
    public void addRecord(String fs)
    {
        addRecord(fs.split(", "));
    }

    /**
     * Inserts a new Record at the given key, as long as it has not already been
     * assigned. Typically only for use by code that creates a Table from a file,
     * as there may be keys missing from such a Table.
     * <p>
     * An array is used rather than an
     * ArrayList because arrays are easier to build inline on the fly, such as
     * new String[]{"Field1", "Field2",..., "FieldN"}; The equivalent with
     * ArrayList takes N + 1 lines.
     * @param key The key at which insertion is attempted.
     * @param fs  The fields to insert as a new Record.
     */
    public void insertRecord(int key, String[] fs)
    {
        if (key < 0)
            throw new IllegalArgumentException("Key must be non-negative");
        ArrayList<String> fields = new ArrayList<>(Arrays.asList(fs));
        if (fields.size() != getNumFields())
            throw new IllegalArgumentException("Expected " +
                Integer.toString(getNumFields()) + " fields but got " +
                Integer.toString(fields.size())
            );
        // Don't bother updating nextKey and trying again here, as the user
        // specified this key for a reason.
        if (table.putIfAbsent(key, new Record(fields)) != null)
            throw new IllegalArgumentException("There already exists a record with that key");
        nextKey = key + 1;
    }

    /**
     * Gets the Record stored under the supplied key if it exists.
     * @param  key They key of the Record.
     * @return     The Record itself.
     */
    public Record getRecord(int key)
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
    public void updateRecord(int key, String fieldName, String replacement)
    {
        // Fields are accessed by index in records, so we need the index of the
        // field name to refer to it by.
        int fieldIndex = fieldNames.indexOf(fieldName);
        if (fieldIndex == -1)
            throw new IndexOutOfBoundsException("No attribute: " + fieldName + " exists");
        getRecord(key).updateField(fieldIndex, replacement);
    }

    /**
     * Deletes the record with the specified key. This key will be unused from
     * now on, unless insertRecord is called.
     * @param key The key of the Record to be deleted.
     */
    public void deleteRecord(int key)
    {
        if (table.remove(key) == null)
            throw new IndexOutOfBoundsException("No record found with that key");
    }

    /**
     * Adds a column the Table. The default value is added to every Record. Throws
     * an exception if the index is out of bounds.
     * @param name       The name of the new column.
     * @param defaultVal The default value to be added to every Record for this
     *                   new field.
     */
    public void addColumn(int index, String name, String defaultVal)
    {
        if (index < 0 || index > fieldNames.size())
            throw new IndexOutOfBoundsException("Cannot insert new column at index " + Integer.toString(index));

        fieldNames.add(index, name);
        for (Record r: table.values())
            r.addField(index, defaultVal);
    }

    /**
     * Renames a column in the Table, as long as the new name does not clash with
     * another column.
     * @param oldName The name of the column to be renamed.
     * @param newName The new name for the column.
     */
    public void renameColumn(String oldName, String newName)
    {
        if (fieldNames.contains(newName))
            throw new IllegalArgumentException("Already a column named " + newName);
        int index = fieldNames.indexOf(oldName);
        if (index == -1)
            throw new IllegalArgumentException("No column with name " + oldName);
        fieldNames.set(index, newName);
    }

    /**
     * Deletes a column from the table, and the associated value from every Record.
     * @param name The name of the column to be deleted.
     */
    public void deleteColumn(String name)
    {
        int index = fieldNames.indexOf(name);
        if (index == -1)
            throw new IllegalArgumentException("No column with name " + name);

        fieldNames.remove(index);
        for (Record r: table.values())
            r.deleteField(index);
    }

    /**
     * Renames the Table.
     * @param newName The new name.
     */
    public void rename(String newName)
    {
        name = newName;
    }

    /**
     * Overrides Object.equals(Object that) so that Tables can be compared.
     * @param  that The Table to be compared with.
     * @return      Whether the Tables are equal.
     */
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (!(that instanceof Table))return false;
        Table thatTable = (Table)that;
        if (!name.equals(thatTable.name)) return false;
        // Tables are equal only if they share the same field names.
        if (!getFieldNames().equals(thatTable.getFieldNames())) return false;
        int numRecords = getNumRecords();
        if (numRecords != thatTable.getNumRecords()) return false;

        // Tables are equal if equal Records are stored under every key.
        try
        {
            for (int key : table.keySet())
                if (!table.get(key).equals(thatTable.getRecord(key)))
                    return false;
        }
        catch (IndexOutOfBoundsException e)
        {
            // They had the same number of keys, but one of the keys didn't
            // appear in thatTable, so we got an exception.
            return false;
        }

        return true;
    }

    /**
     * Builds a matrix of Strings that represent the Table. The first line
     * gives the name of the Table. The second line gives the names of the
     * fields. Every line after that gives the values of those fields for a
     * particular Record.
     * @return The PrintInfo instance.
     */
    public String[][] getTableData()
    {
        // We need space for all the Records, the name of the Table, and the names
        // of the fields.
        String[][] tableData = new String[getNumRecords() + 2][];
        tableData[0] = new String[]{getName()};
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
            fields.add(Integer.toString(entry.getKey()));
            for (int i = 0; i < getNumFields(); i ++)
                fields.add(entry.getValue().getField(i));
            tableData[counter++] = fields.toArray(new String[0]);
        }
        return tableData;
    }

    /**
     * Factory method to load a Table from the data read in from a file.
     * @param  name The name of the Table.
     * @param  data The data read from the Table file.
     * @return      The Table instance.
     */
    public static Table createTableFromData(String name, String[][] data)
    {
        // The first line is the human readable field names. We build the
        // comma separated list of field names for the Table constructor.
        String[] keyAndAttrs = data[0];
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 1; i < keyAndAttrs.length; i++)
            joiner.add(keyAndAttrs[i]);

        // The new Table instance.
        Table t = new Table(name, joiner.toString());

        // Now add the Records one at a time.
        for (int row = 1; row < data.length; row++)
        {
            String[] recordFields = data[row];
            String[] fields = new String[recordFields.length - 1];
            int key = Integer.parseInt(recordFields[0]);
            for (int f = 1; f < recordFields.length; f++)
                fields[f - 1] = recordFields[f];
            t.insertRecord(key, fields);
        }

        return t;
    }

    /**
     * Runs the tests for this class.
     * @param args Command line args.
     */
    public static void main(String[] args) {
        System.out.println("Testing Table");
        Table t = new Table("Animal", "Name, Breed");
        t.test(args);
        System.out.println("Testing complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test failed");
    }

    private void test(String args[])
    {
        testGetters();
        testAddRecord();
        testInsertRecord();
        testGetRecord();
        testDeleteRecord();
        testUpdateRecord();
        testAddColumn();
        testDeleteColumn();
        testRename();
        testRenameColumn();
        testEquals();
        testGetAllKeys();
        testAddRecordAsSingleString();
        testGetTableData();
    }

    private void testGetters()
    {
        claim(getName().equals("Animal"));
        claim(getNumFields() == 2);
        claim(getFieldNames().equals("Name, Breed"));
        claim(getNumRecords() == 0);
    }

    private void testAddRecord()
    {
        addRecord(new String[] {"Dog", "Corgi"});
        claim(getNumRecords() == 1);
        try
        {
            addRecord(new String[]{"Dog"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            addRecord(new String[]{"Dog", "Corgi", "Cute"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
    }

    private void testInsertRecord()
    {
        try
        {
            insertRecord(-1, new String[]{"Dog", "Corgi"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            insertRecord(0, new String[]{"Dog", "Corgi"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            insertRecord(1, new String[]{"Dog"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            insertRecord(1, new String[]{"Dog", "Corgi", "Cute"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        insertRecord(1, new String[]{"Dog", "Corgi"});
        claim(getNumRecords() == 2);
    }

    private void testGetRecord()
    {
        Record r = getRecord(0);
        claim(r.getField(0).equals("Dog"));
        claim(r.getField(1).equals("Corgi"));
        try
        {
            r.getField(2);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            Record r2 = getRecord(2);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            Record r2 = getRecord(-1);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
    }

    private void testDeleteRecord()
    {
        try
        {
            deleteRecord(2);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        deleteRecord(0);
        claim(getNumRecords() == 1);
        try
        {
            deleteRecord(0);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
    }

    private void testUpdateRecord()
    {
        try
        {
            updateRecord(0, "Breed", "Dalmation");
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            updateRecord(1,"NumLegs","2");
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        updateRecord(1,"Name","Robin");
        claim(getRecord(1).getField(0).equals("Robin"));
    }

    private void testAddColumn()
    {
        try
        {
            addColumn(-1, "NumLegs", "2");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            addColumn(3, "NumLegs", "2");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        addColumn(2, "NumLegs", "2");
        claim(getNumFields() == 3);
        claim(getFieldNames().equals("Name, Breed, NumLegs"));
        claim(getRecord(1).getField(2).equals("2"));

        addColumn(2, "Colour", "White");
        claim(getNumFields() == 4);
        claim(getFieldNames().equals("Name, Breed, Colour, NumLegs"));
        claim(getRecord(1).getField(2).equals("White"));
    }

    private void testDeleteColumn()
    {
        try
        {
            deleteColumn("Jam");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }

        deleteColumn("Breed");
        deleteColumn("Colour");
        claim(getNumFields() == 2);
        claim(getFieldNames().equals("Name, NumLegs"));
        for (Record r:table.values())
        {
            try
            {
                r.getField(2);
                claim(false);
            }
            catch (IndexOutOfBoundsException e)
            {
                // test passed
            }
            claim(r.getField(1).equals("2"));
        }
    }

    private void testRename()
    {
        rename("Person");
        claim(getName().equals("Person"));
    }

    private void testRenameColumn()
    {
        try
        {
            renameColumn("Owner", "Slave");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            renameColumn("Name", "NumLegs");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }

        renameColumn("NumLegs", "NumPets");
        claim(getFieldNames().equals("Name, NumPets"));
    }

    private void testEquals()
    {
        addRecord(new String[]{"Laura", "1"});
        Table t = new Table("People", "Name, NumPets");
        t.addRecord(new String[] {"Robin", "2"});
        t.deleteRecord(0);
        t.addRecord(new String[] {"Robin", "2"});
        t.addRecord(new String[]{"Laura", "1"});
        claim(!this.equals(t));
        t.rename("Person");
        t.renameColumn("NumPets", "NumLegs");
        claim(!this.equals(t));
        t.renameColumn("NumLegs", "NumPets");
        t.deleteRecord(2);
        claim(!this.equals(t));
        t.addRecord(new String[]{"Laura", "1"});
        claim(!this.equals(t));
        t.deleteRecord(3);
        deleteRecord(2);
        addRecord(new String[]{"Laura", "1"});
        deleteRecord(3);
        addRecord(new String[]{"Laura", "1"});
        t.addRecord(new String[]{"Laura", "2"});
        claim(!this.equals(t));
        t.updateRecord(4,"NumPets","1");
        claim(this.equals(t));
        t.addRecord(new String[]{"Amy","0"});
        claim(!this.equals(t));
    }

    private void testGetAllKeys()
    {
        ArrayList<Integer> allKeys = getAllKeys();
        claim(allKeys.size() == 2);
        claim(allKeys.get(0) == 1);
        claim(allKeys.get(1) == 4);
    }

    private void testAddRecordAsSingleString()
    {
        addRecord("Kat, 2");
        claim(getNumRecords() == 3);
        Record r = getRecord(5);
        claim(r.getField(0).equals("Kat"));
        claim(r.getField(1).equals("2"));
    }

    private void testGetTableData()
    {
        String[][] tableData = getTableData();
        claim(tableData[0][0].equals(getName()));
        claim(tableData[1][0].equals(KEY_COL_NAME));
        for (int i = 1; i < tableData[1].length; i++)
            tableData[1][i].equals(fieldNames.get(i-1));
        for (int row = 2; row < tableData.length; row++)
        {
            Record record = getRecord(Integer.parseInt(tableData[row][0]));
            for (int field = 1; field < tableData[row].length; field++)
                claim(record.getField(field - 1).equals(tableData[row][field]));
        }
    }
}
