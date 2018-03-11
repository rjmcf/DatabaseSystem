package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import java.io.IOException;
import java.lang.IllegalArgumentException;

public class TableTest extends TestBase
{
    private String tableTestFolderPath = "dbTestFolders/table/";
    private Table emptyTable;
    private Table filledTable;
    /**
     * Runs all the tests for Table.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        TableTest tester = new TableTest();
        tester.startTest();
    }

    @Override
    public void beforeTest()
    {
        emptyTable = new Table("EmptyTable", "");
        filledTable = new Table("Person", "Name, Age, NumberOfPets");
        filledTable.addRecord("Susan, 21, 1");
        filledTable.addRecord("James, 47, 3");
        filledTable.addRecord("Alex, 17, 0");
    }

    @Test
    public void testGetters()
    {
        claim("EmptyTable".equals(emptyTable.getName()), "Table name does not match original.");
        claim("Person".equals(filledTable.getName()), "Table name does not match original.");
        claim(emptyTable.getNumFields() == 0, "Number of fields incorrect.");
        claim(filledTable.getNumFields() == 3, "Number of fields incorrect.");
        claim("".equals(emptyTable.getFieldNames()), "Field names do not match original.");
        claim("Name, Age, NumberOfPets".equals(filledTable.getFieldNames()), "Field names do not match original.");
        claim(emptyTable.getNumRecords() == 0, "Number of records incorrect.");
        claim(filledTable.getNumRecords() == 3, "Number of records incorrect.");
    }

    @Test
    public void testAddRecord()
    {
        try
        {
            emptyTable.addRecord(new String[]{"Dog"});
            claim(false, "Too many fields supplied.");
        }
        catch(IllegalArgumentException e) { /* test passed */ }
        try
        {
            filledTable.addRecord(new String[]{"Dog"});
            claim(false, "Too few fields supplied.");
        }
        catch(IllegalArgumentException e) { /* test passed */ }

        filledTable.addRecord(new String[] {"Jefferson", "78", "0"});
        claim(filledTable.getNumRecords() == 4, "Number of records hasn't changed after add.");
    }

    @Test
    public void testAddRecordAsSingleString()
    {
        try
        {
            emptyTable.addRecord("TooMany");
            claim(false, "Too many fields.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        try
        {
            filledTable.addRecord("TooFew");
            claim(false, "Too few fields.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        filledTable.addRecord("Jean, 37, 4");
        Record record = filledTable.getRecord(3);
        claim("Jean".equals(record.getField(0)), "Incorrect field value");
        claim("37".equals(record.getField(1)), "Incorrect field value");
        claim("4".equals(record.getField(2)), "Incorrect field value");
    }

    @Test
    public void testGetRecord()
    {
        try
        {
            emptyTable.getRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyTable.getRecord(0);
            claim(false, "Invalid index 0");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        try
        {
            filledTable.getRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledTable.getRecord(3);
            claim(false, "Invalid index 3");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        Record r0 = filledTable.getRecord(0);
        String[] fields = new String[]{"Susan", "21", "1"};
        for (int i = 0; i < 3; i++)
            claim(fields[i].equals(r0.getField(i)), "Incorrect Field" + Integer.toString(i) + " value");
        try
        {
            r0.getField(3);
            claim(false, "Invalid index 3");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
    }

    @Test
    public void testDeleteRecord()
    {
        try
        {
            emptyTable.deleteRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyTable.deleteRecord(0);
            claim(false, "Invalid index 0");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        try
        {
            filledTable.deleteRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledTable.deleteRecord(3);
            claim(false, "Invalid index 3");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }


        filledTable.deleteRecord(0);
        claim(filledTable.getNumRecords() == 2, "Incorrect number of records after deletion.");
        try
        {
            filledTable.deleteRecord(0);
            claim(false, "Should not be able to re-delete Records.");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        Record r1 = filledTable.getRecord(1);
        claim("James".equals(filledTable.getRecord(1).getField(0)), "Incorrect record returned after deletion");
    }

    @Test
    public void testUpdateRecord()
    {
        try
        {
            emptyTable.updateRecord(-1, "Any", "Thing");
            claim(false, "Invalid index -1");
        }
        catch(IllegalArgumentException e) { /* test passed */ }
        try
        {
            emptyTable.updateRecord(0,"Any","Thing");
            claim(false, "Invalid index 0");
        }
        catch(IllegalArgumentException e) { /* test passed */ }

        try
        {
            filledTable.updateRecord(-1, "Name", "Fake");
            claim(false, "Invalid index -1");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledTable.updateRecord(3,"Name","Nope");
            claim(false, "Invalid index 3");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledTable.updateRecord(1,"NotAColumn","Val");
            claim(false, "Invalid column name NotAColumn");
        }
        catch(IllegalArgumentException e) { /* test passed */ }
        filledTable.updateRecord(1, "Name", "Beatrice");
        Record record = filledTable.getRecord(1);
        claim("Beatrice".equals(record.getField(0)), "Incorrect field value.");
        claim("47".equals(record.getField(1)), "Incorrect field value.");
        claim("3".equals(record.getField(2)), "Incorrect field value.");
    }

    @Test
    public void testAddColumn()
    {
        try
        {
            emptyTable.addColumn(-1,"ColumnName", "Default");
            claim(false, "Invalid index -1");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyTable.addColumn(1,"ColumnName", "Default");
            claim(false, "Invalid index 1");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        emptyTable.addColumn(0, "Column0", "Default");
        claim(emptyTable.getNumFields() == 1, "Number of fields is wrong.");
        claim("Column0".equals(emptyTable.getFieldNames()), "Field names are wrong.");

        try
        {
            filledTable.addColumn(-1,"ColumnName", "Default");
            claim(false, "Invalid index -1");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledTable.addColumn(4,"ColumnName", "Default");
            claim(false, "Invalid index 4");
        }
        catch(IndexOutOfBoundsException e) { /* test passed */ }
        filledTable.addColumn(2, "NewColumn", "DefaultVal");
        claim(filledTable.getNumFields() == 4, "Number of fields is wrong;");
        claim("Name, Age, NewColumn, NumberOfPets".equals(filledTable.getFieldNames()), "Field names are wrong.");
        claim("DefaultVal".equals(filledTable.getRecord(1).getField(2)), "Default val not saved.");
    }

    @Test
    public void testDeleteColumn()
    {
        try
        {
            emptyTable.deleteColumn("NoColumn");
            claim(false, "No column named NoColumn");
        }
        catch(IllegalArgumentException e) { /* test passed */ }

        try
        {
            filledTable.deleteColumn("NoColumn");
            claim(false, "No column named NoColumn");
        }
        catch(IllegalArgumentException e) { /* test passed */ }

        filledTable.deleteColumn("Age");
        claim(filledTable.getNumFields() == 2, "Number of fields is wrong.");
        claim("Name, NumberOfPets".equals(filledTable.getFieldNames()), "Names of fields are wrong.");

        Record record = filledTable.getRecord(1);
        try
        {
            record.getField(2);
            claim(false, "Column should be deleted.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        claim("3".equals(record.getField(1)), "Incorrect field value");
    }

    @Test
    public void testRename()
    {
        emptyTable.rename("VacuousTable");
        claim("VacuousTable".equals(emptyTable.getName()), "Incorrect Table name.");
        filledTable.rename("StuffedTable");
        claim("StuffedTable".equals(filledTable.getName()), "Incorrect Table name.");
    }

    @Test
    public void testRenameColumn()
    {
        try
        {
            emptyTable.renameColumn("Any", "Other");
            claim(false, "No column with that name.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        try
        {
            filledTable.renameColumn("NotAColumn", "Other");
            claim(false, "No column with that name.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }
        try
        {
            filledTable.renameColumn("Name", "Age");
            claim(false, "Already a column with that name.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        filledTable.renameColumn("NumberOfPets", "NumPets");
        claim("Name, Age, NumPets".equals(filledTable.getFieldNames()), "Incorrect field names.");
    }

    @Test
    public void testEquals()
    {
        Table otherEmpty = new Table("EmptyTable2", "");
        claim(!emptyTable.equals(new Record()), "Different class shouldn't be equal.");
        claim(emptyTable.equals(emptyTable), "Same reference should be equal.");
        claim(!emptyTable.equals(otherEmpty), "Different name so not equal.");
        otherEmpty.rename("EmptyTable");
        otherEmpty.addColumn(0, "NewColumn", "0");
        claim(!emptyTable.equals(otherEmpty), "Too many fields so not equal.");
        otherEmpty.deleteColumn("NewColumn");
        claim(emptyTable.equals(otherEmpty), "All same so equal.");
        emptyTable.addColumn(0, "NewColumn", "0");
        claim(!emptyTable.equals(otherEmpty), "Too few fields so not equal.");


        Table otherFilled = new Table("People", "Name, Age, NumberOfPets");
        otherFilled.addRecord("Susan, 21, 1");
        otherFilled.addRecord("James, 47, 3");
        otherFilled.addRecord("Alex, 17, 0");
        claim(!filledTable.equals(otherFilled), "Different name so not equal");
        otherFilled.rename("Person");
        otherFilled.renameColumn("NumberOfPets", "NumLegs");
        claim(!filledTable.equals(otherFilled), "Different fields so not equal");
        otherFilled.renameColumn("NumLegs", "NumberOfPets");
        otherFilled.deleteRecord(2);
        claim(!filledTable.equals(otherFilled), "Too few Records so not equal");
        otherFilled.addRecord("Alex, 17, 0");
        claim(!filledTable.equals(otherFilled), "Different keys so not equal");
        filledTable.deleteRecord(2);
        claim(!filledTable.equals(otherFilled), "Too many Records so not equal");
        filledTable.addRecord("Alex, 17, 0");
        otherFilled.updateRecord(0, "Name", "Betty");
        claim(!filledTable.equals(otherFilled), "Different Records so not equal");
        otherFilled.updateRecord(0, "Name", "Susan");
        claim(filledTable.equals(otherFilled), "All same so equal");
    }

    @Test
    public void testPrintTable()
    {
        emptyTable.printTable();
        filledTable.printTable();
    }

    @Test
    public void testSaveTableToFile()
    {
        claim(emptyTable.getIsDirty(), "Should be dirty when first created.");
        try
        {
            emptyTable.saveTableToFile(tableTestFolderPath, true);
            claim(!emptyTable.getIsDirty(), "Should not be dirty after saving.");
            claim(emptyTable.equals(TableFileReadWriter.readFromFile(emptyTable.getName(), tableTestFolderPath, true)), "Serialization loaded table does not equal original.");
        }
        catch (IOException e)
        {
            claim (false, "IOException while reading or writing by serialization.");
        }
        emptyTable.rename("NewEmptyTable");
        claim(emptyTable.getIsDirty(), "Should be dirty after rename.");
        try
        {
            emptyTable.saveTableToFile(tableTestFolderPath, false);
            claim(!emptyTable.getIsDirty(), "Should not be dirty after saving.");
            claim(emptyTable.equals(TableFileReadWriter.readFromFile(emptyTable.getName(), tableTestFolderPath, false)), "RjmMethod loaded table does not equal original.");
        }
        catch (IOException e)
        {
            claim (false, "IOException while reading or writing by RjmMethod.");
        }

        claim(filledTable.getIsDirty(), "Should be dirty when first created.");
        try
        {
            filledTable.saveTableToFile(tableTestFolderPath, true);
            claim(!filledTable.getIsDirty(), "Should not be dirty after saving.");
            claim(filledTable.equals(TableFileReadWriter.readFromFile(filledTable.getName(), tableTestFolderPath, true)), "Serialization loaded table does not equal original.");
        }
        catch (IOException e)
        {
            claim (false, "IOException while reading or writing by serialization.");
        }
        filledTable.rename("NewFilledTable");
        claim(filledTable.getIsDirty(), "Should be dirty after rename.");
        try
        {
            filledTable.saveTableToFile(tableTestFolderPath, false);
            claim(!filledTable.getIsDirty(), "Should not be dirty after saving.");
            claim(filledTable.equals(TableFileReadWriter.readFromFile(filledTable.getName(), tableTestFolderPath, false)), "RjmMethod loaded table does not equal original.");
        }
        catch (IOException e)
        {
            claim (false, "IOException while reading or writing by RjmMethod.");
        }
    }

    @Test
    public void testCreateTableFromDataTest()
    {
        String[][] emptyTableData = emptyTable.getTableData();
        claim(emptyTable.equals(Table.createTableFromData(emptyTable.getName(), emptyTableData)), "Created Table does not equal original.");
        String[][] filledTableData = filledTable.getTableData();
        claim(filledTable.equals(Table.createTableFromData(filledTable.getName(), filledTableData)), "Created Table does not equal original.");
    }

    @Test
    public void testIsDirty()
    {
        claim(emptyTable.getIsDirty(), "Should be dirty after first creation.");
        claim(filledTable.getIsDirty(), "Should be dirty after first creation.");

        // Rename Table
        emptyTable.rename("NewEmptyName");
        claim(emptyTable.getIsDirty(), "Should be dirty after rename.");
        try {emptyTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!emptyTable.getIsDirty(), "Shouldn't be dirty after saving.");

        filledTable.rename("NewFilledName");
        claim(filledTable.getIsDirty(), "Should be dirty after rename.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Rename column
        filledTable.renameColumn("Name", "AString");
        claim(filledTable.getIsDirty(), "Should be dirty after renameColumn.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Add column
        emptyTable.addColumn(0, "NewColumn", "Blah");
        claim(emptyTable.getIsDirty(), "Should be dirty after addColumn.");
        try {emptyTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!emptyTable.getIsDirty(), "Shouldn't be dirty after saving.");

        filledTable.addColumn(0, "NewColumn", "Blah");
        claim(filledTable.getIsDirty(), "Shouldn't be dirty after addColumn.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Delete column
        emptyTable.deleteColumn("NewColumn");
        claim(emptyTable.getIsDirty(), "Should be dirty after deleting column.");
        try {emptyTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!emptyTable.getIsDirty(), "Shouldn't be dirty after saving.");

        filledTable.deleteColumn("NewColumn");
        claim(filledTable.getIsDirty(), "Should be dirty after deleting column.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Add Record
        filledTable.addRecord("Somebody, 30, 10");
        claim(filledTable.getIsDirty(), "Should b dirty after adding Record.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Update Record
        filledTable.updateRecord(3, "AString", "Elliot");
        claim(filledTable.getIsDirty(), "Should be dirty after updating Record.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Delete Record
        filledTable.deleteRecord(3);
        claim(filledTable.getIsDirty(), "Should be dirty after deleting Record.");
        try {filledTable.saveTableToFile(tableTestFolderPath, true);}
        catch (IOException e) { claim(false, "IOException while saving to file."); }
        claim(!filledTable.getIsDirty(), "Shouldn't be dirty after saving.");

        // Load Table
        try
        {
            Table o = TableFileReadWriter.readFromFile(filledTable.getName(), tableTestFolderPath, true);
            claim(!o.getIsDirty(), "Shouldn't be dirty after loading.");
        }
        catch (IOException e)
        {
            claim(false, "IOException while reading from file.");
        }
    }
}
