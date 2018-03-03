package tablesrecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.Collections;
import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import printutils.PrintInfo;

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
    // The String used to separate fields in files.
    private static final String FILE_FIELD_SEPARATOR = String.valueOf((char)0x1F);
    // The encoding used to convert between hex and String.
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    // The String used to separate fields when printing.
    private static final String PRINT_FIELD_SEPARATOR = " | ";
    // The String which, when repeated, is used to build the Record separator
    // for printing.
    private static final String RECORD_SEPARATOR_CHAR = "-";
    // The start of the Record separator when we include the first line.
    private static final String RECORD_SEPARATOR_FIRST_CHAR = "|-";
    // The String that appears in Record separator on the boundary between two
    // fields.
    private static final String RECORD_SEPARATOR_BETWEEN_CHAR = "-+-";
    // The end of the Record separator when we include the last line.
    private static final String RECORD_SEPARATOR_LAST_CHAR = "-|";

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

    /**
     * Finds the largest key assigned so far, and sets the next key to be one
     * larger than it. This will be called when we try to add a new Record and
     * discover that the next key is out of sync with the Records in the table.
     */
    public void setNextKeyBasedOnRecords()
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
        getRecord(key).setField(fieldIndex, replacement);
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
     * Adds a column to the end of the table. The default value is added to the
     * end of every Record.
     * @param name       The name of the new column.
     * @param defaultVal The default value to be added to every Record for this
     *                   new field.
     */
    public void addColumn(String name, String defaultVal)
    {
        fieldNames.add(name);
        for (Record r: table.values())
            r.addField(defaultVal);
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
        if (!(that instanceof Table)) return false;
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
            // A key in this table is not in the other Table, so they are not equal.
            return false;
        }

        return true;
    }

    /**
     * Generates and returns the PrintInfo instance specific to this Table.
     * @param  includeFirstColLine Whether to include the first vertical line when
     *                             printing.
     * @param  includeLastColLine  Whether to include the last vertical line when
     *                             printing.
     * @return                     The PrintInfo instance.
     */
    public PrintInfo getPrintInfo(boolean includeFirstColLine, boolean includeLastColLine)
    {
        PrintInfo printInfo = new PrintInfo();
        printInfo.setTableName(getName());

        // Stores some useful stats about the size of the table.
        int numCols = getNumFields() + 1; // +1 because of the key column.
        int numRecords = getNumRecords();
        // Stores the maximum sizes of individual columns and rows. There is the
        // implicit assumption here that the header row is always exactly one
        // line tall.
        int[] colWidths = new int[numCols];
        int[] recordHeights = new int[numRecords];
        // Stores the names of the columns.
        String[] colNames = new String[numCols];
        colNames[0] = KEY_COL_NAME;
        int counter = 1;
        for (String colName : getFieldNames().split(", "))
            colNames[counter++] = colName;
        // Sets the initial widths of the columns to be that of their names.
        for (int a = 0; a < colNames.length; a++)
            colWidths[a] = colNames[a].length();

        // Space for every field.
        String[][] fields = new String[numRecords][numCols];
        String field;
        Record record;
        int row = 0;
        // Set the maximum height and width for every row and column by checking
        // the number of lines in each field and the width of each field.
        for (Map.Entry<Integer, Record> entry : table.entrySet())
        {
            record = entry.getValue();
            for (int col = 0; col < numCols; col++)
            {
                // Make sure that the first column is the key.
                if (col == 0)
                    field = Integer.toString(entry.getKey());
                else
                    field = record.getField(col-1); // Column 1 is Field 0.
                // Check the actual height and width of this field.
                setRowHeightAndColWidthForField(field, recordHeights, row, colWidths, col);
                fields[row][col] = field;
            }
            row++; // Remember to increment the row counter.
        }

        // Build the record separator based on how wide each column is.
        String recSepPrefix = includeFirstColLine ? RECORD_SEPARATOR_FIRST_CHAR : "";
        String recSepSuffix = includeLastColLine ? RECORD_SEPARATOR_LAST_CHAR : "";
        StringJoiner recSepJ = new StringJoiner(RECORD_SEPARATOR_BETWEEN_CHAR, recSepPrefix, recSepSuffix);
        for (int width: colWidths)
            recSepJ.add(String.join("", Collections.nCopies(width, RECORD_SEPARATOR_CHAR)));
        printInfo.setRecordSeparator(recSepJ.toString());

        // Space for the actual Strings that we are going to print.
        // +1 because we include the header row now.
        // The first level corresponds to records, the second corresponds to
        // lines within fields in those records.
        String[][] linesToPrint = new String[1 + numRecords][];

        // Build the header row
        StringJoiner lineJ = getLineStringJoiner(includeFirstColLine, includeLastColLine);
        for (int i = 0; i < numCols; i++)
        {
            // The amount of padding required due to the difference between
            // the max width and the width of these actual names.
            int numSpacesRequired = colWidths[i] - colNames[i].length();
            lineJ.add(colNames[i] + getNSpaces(numSpacesRequired));
        }
        // Here we make the assumption that the header row is one line tall.
        linesToPrint[0] = new String[]{lineJ.toString()};

        // Now build the String to print for each record in lines.
        for (int recordNum = 0; recordNum < numRecords; recordNum++)
        {
            // For this record we need to print as many lines as is the maximum
            // height for this record.
            String[] linesInThisRecord = new String[recordHeights[recordNum]];

            // Split each field in this record into lines.
            String[][] fieldsInLines = new String[numCols][];
            for (int col = 0; col < numCols; col++)
                fieldsInLines[col] = fields[recordNum][col].split("\n");
            // Now build up each line for this record.
            for (int lineNum = 0; lineNum < recordHeights[recordNum]; lineNum++)
            {
                lineJ = getLineStringJoiner(includeFirstColLine, includeLastColLine);
                // We add lines of fields to the line column by column.
                for (int col = 0; col < numCols; col++)
                {
                    // If there are no more lines in this column, pad it with spaces.
                    if (fieldsInLines[col].length <= lineNum)
                        lineJ.add(getNSpaces(colWidths[col]));
                    else
                    {
                        // The amount of padding required due to the difference between
                        // the max width and the width of these actual lines.
                        int numSpacesRequired = colWidths[col] - fieldsInLines[col][lineNum].length();
                        lineJ.add(fieldsInLines[col][lineNum] + getNSpaces(numSpacesRequired));
                    }
                }
                linesInThisRecord[lineNum] = lineJ.toString();
            }
            // +1 because we skip the header row.
            // Record 0 is Line 1.
            linesToPrint[recordNum+1] = linesInThisRecord;
        }
        printInfo.setLinesToPrint(linesToPrint);
        return printInfo;
    }

    private void setRowHeightAndColWidthForField(String field, int[] recordHeights, int row, int[] colWidths, int col)
    {
        String[] lines = field.split("\n");
        // If this field has more lines than the current max height for this row,
        // update the max height.
        if (lines.length > recordHeights[row])
            recordHeights[row] = lines.length;
        // If there's a line in this field wider than the current max width for
        // this column, update the max width.
        for (String line: lines)
            if (line.length() > colWidths[col])
                colWidths[col] = line.length();
    }

    // Gets the StringJoiner used to build up lines to print.
    private StringJoiner getLineStringJoiner(boolean includeFirstColLine, boolean includeFinalColLine)
    {
        String prefix = includeFirstColLine ? PRINT_FIELD_SEPARATOR.replaceFirst("^\\s++", "") : "";
        String suffix = includeFinalColLine ? PRINT_FIELD_SEPARATOR.replaceFirst("\\s++$", "") : "";
        return new StringJoiner(PRINT_FIELD_SEPARATOR, prefix, suffix);
    }

    private String getNSpaces(int n)
    {
        return String.join("", Collections.nCopies(n, " "));
    }

    /**
     * Gets a list of the lines to write to a file to store this Table.
     * @return The lines to write.
     */
    public String[] prepareLinesForWriting()
    {
        // The lines to print. +1 because of the header row containing the
        // field names.
        String[] lines = new String[getNumRecords() + 1];
        StringJoiner joiner = new StringJoiner(FILE_FIELD_SEPARATOR);

        // Build the header row. No need to hex up these names, as it is assumed
        // that they will not contain commas or newLines.
        for (String a: getFieldNames().split(", "))
            joiner.add(a);
        lines[0] = joiner.toString();

        // Now build the line to print for each Record.
        Record r;
        int recordSize = getNumFields();
        int counter = 1;
        for (Map.Entry<Integer,Record> entry : table.entrySet())
        {
            r = entry.getValue();
            joiner = new StringJoiner(FILE_FIELD_SEPARATOR);
            joiner.add(Integer.toString(entry.getKey()));
            for (int f = 0; f < recordSize; f++)
            {
                // We convert to hex to allow strange characters in these fields.
                joiner.add(convertStringToHex(r.getField(f)));
            }
            lines[counter++] = joiner.toString(); // Remember to increment the counter.
        }

        return lines;
    }

    private String convertStringToHex(String s)
    {
        String result = String.format("%x", new BigInteger(1, s.getBytes(ENCODING)));
        // Remember to add a leading "0" if we need it.
        return result.length() % 2 == 0 ? result : "0" + result;
    }

    /**
     * Factory method to load a Table from the lines read in from a file.
     * @param  name  The name of the Table.
     * @param  lines The lines read from the Table file.
     * @return       The Table instance.
     */
    public static Table createTableFromLines(String name, ArrayList<String> lines)
    {
        // The first line is the human readable field names. We build the
        // comma separated list of field names for the Table constructor.
        String[] keyAndAttrs = lines.get(0).split(FILE_FIELD_SEPARATOR);
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < keyAndAttrs.length; i++)
            joiner.add(keyAndAttrs[i]);

        // Can discard that first line now.
        lines.remove(0);

        // The new Table instance.
        Table t = new Table(name, joiner.toString());

        // Now add the Records one at a time.
        for (String line: lines)
        {
            String[] keyAndValues = line.split(FILE_FIELD_SEPARATOR);
            String[] fields = new String[keyAndValues.length - 1];
            int key = Integer.parseInt(keyAndValues[0]);
            for (int f = 1; f < keyAndValues.length; f++)
                fields[f - 1] = convertHexToString(keyAndValues[f]);
            t.insertRecord(key, fields);
        }

        return t;
    }

    private static String convertHexToString(String h)
    {
        byte[] bytes = DatatypeConverter.parseHexBinary(h);
        return new String(bytes, ENCODING);
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
        testStringHexConversions();
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
        claim(r.getSize() == 2);
        claim(r.getField(0).equals("Dog"));
        claim(r.getField(1).equals("Corgi"));
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
        addColumn("NumLegs", "2");
        claim(getNumFields() == 3);
        claim(getFieldNames().equals("Name, Breed, NumLegs"));
        claim(getRecord(1).getField(2).equals("2"));
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
        claim(getNumFields() == 2);
        claim(getFieldNames().equals("Name, NumLegs"));
        for (Record r:table.values())
        {
            claim(r.getSize() == 2);
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
        deleteRecord(2);
        addRecord(new String[]{"Laura", "1"});
        t.addRecord(new String[]{"Laura", "2"});
        claim(!this.equals(t));
        t.updateRecord(3,"NumPets","1");
        claim(this.equals(t));
        t.addRecord(new String[]{"Amy","0"});
        claim(!this.equals(t));
    }

    private void testGetAllKeys()
    {
        ArrayList<Integer> allKeys = getAllKeys();
        claim(allKeys.size() == 2);
        claim(allKeys.get(0) == 1);
        claim(allKeys.get(1) == 3);
    }

    private void testStringHexConversions()
    {
        String s = "a\na!, b";
        String h = convertStringToHex(s);
        claim(h.equals("610a61212c2062"));
        claim(Table.convertHexToString(h).equals(s));
    }
}
