package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import java.io.IOException;

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

        filledTable.addRecord(new String[] {"Cat", "Bengal"});
        claim(t.getNumRecords() == 2, "Number of records hasn't changed after add.");
    }

    @Test
    public void testGetRecord()
    {
        try
        {
            emptyTable.getRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }
        try
        {
            emptyTable.getRecord(0);
            claim(false, "Invalid index 0");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }

        try
        {
            filledTable.getRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }
        try
        {
            filledTable.getRecord(3);
            claim(false, "Invalid index 3");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }

        Record r0 = filledTable.getRecord(0);
        String[] fields = new String[]{"Susan", "21", "1"};
        for (int i = 0; i < 3; i++)
            claim(fields[i].equals(r0.getField(i)), "Incorrect Field" + Integer.toString(i) + " value");
        try
        {
            r0.getField(3);
            claim(false, "Invalid index 3");
        }
        catch (IOException e)
        { /* test passed */ }
    }

    @Test
    public void testDeleteRecord()
    {
        try
        {
            emptyTable.deleteRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (InvalidArgumentException e)
        { /* test passed */ }
        try
        {
            emptyTable.deleteRecord(0);
            claim(false, "Invalid index 0");
        }
        catch (InvalidArgumentException e)
        { /* test passed */ }

        try
        {
            filledTable.deleteRecord(-1);
            claim(false, "Invalid index -1");
        }
        catch (InvalidArgumentException e)
        { /* test passed */ }
        try
        {
            filledTable.deleteRecord(3);
            claim(false, "Invalid index 3");
        }
        catch (InvalidArgumentException e)
        { /* test passed */ }


        filledTable.deleteRecord(0);
        claim(filledTable.getNumRecords() == 2, "Incorrect number of records after deletion.");
        try
        {
            filledTable.deleteRecord(0);
            claim(false, "Should not be able to re-delete Records.");
        }
        catch(IndexOutOfBoundsException e)
        { /* test passed */ }
        Record r1 = filledTable.getRecord(1);
        claim("James".equals(filledTable.getRecord(1).getField(0)), "Incorrect record returned after deletion");
    }

    public void test()
    {
        testUpdateRecord(t);
        testAddColumn(t);
        testDeleteColumn(t);
        testRename(t);
        testRenameColumn(t);
        testEquals(t);
        testAddRecordAsSingleString(t);
        testPrintTable(t);
        testSaveTableToFile(t);
        testIsDirty(t);
        testCreateTableFromDataTest(t);
    }

    private void testUpdateRecord(Table t)
    {
        try
        {
            t.updateRecord(0, "Breed", "Dalmation");
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            t.updateRecord(1,"NumLegs","2");
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        t.updateRecord(1,"Name","Robin");
        claim(t.getRecord(1).getField(0).equals("Robin"));
    }

    private void testAddColumn(Table t)
    {
        try
        {
            t.addColumn(-1, "NumLegs", "2");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            t.addColumn(3, "NumLegs", "2");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        t.addColumn(2, "NumLegs", "2");
        claim(t.getNumFields() == 3);
        claim(t.getFieldNames().equals("Name, Breed, NumLegs"));
        claim(t.getRecord(1).getField(2).equals("2"));

        t.addColumn(2, "Colour", "White");
        claim(t.getNumFields() == 4);
        claim(t.getFieldNames().equals("Name, Breed, Colour, NumLegs"));
        claim(t.getRecord(1).getField(2).equals("White"));
    }

    private void testDeleteColumn(Table t)
    {
        try
        {
            t.deleteColumn("Jam");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }

        t.deleteColumn("Breed");
        t.deleteColumn("Colour");
        claim(t.getNumFields() == 2);
        claim(t.getFieldNames().equals("Name, NumLegs"));
        Record r = t.getRecord(1);
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

    private void testRename(Table t)
    {
        t.rename("Person");
        claim(t.getName().equals("Person"));
    }

    private void testRenameColumn(Table t)
    {
        try
        {
            t.renameColumn("Owner", "Slave");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            t.renameColumn("Name", "NumLegs");
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }

        t.renameColumn("NumLegs", "NumPets");
        claim(t.getFieldNames().equals("Name, NumPets"));
    }

    private void testEquals(Table m)
    {
        m.addRecord(new String[]{"Laura", "1"});
        Table t = new Table("People", "Name, NumPets");
        t.addRecord(new String[] {"Robin", "2"});
        t.deleteRecord(0);
        t.addRecord(new String[] {"Robin", "2"});
        t.addRecord(new String[]{"Laura", "1"});
        claim(!m.equals(t));
        t.rename("Person");
        t.renameColumn("NumPets", "NumLegs");
        claim(!m.equals(t));
        t.renameColumn("NumLegs", "NumPets");
        t.deleteRecord(2);
        claim(!m.equals(t));
        t.addRecord(new String[]{"Laura", "1"});
        claim(!m.equals(t));
        t.deleteRecord(3);
        m.deleteRecord(2);
        m.addRecord(new String[]{"Laura", "1"});
        m.deleteRecord(3);
        m.addRecord(new String[]{"Laura", "1"});
        t.addRecord(new String[]{"Laura", "2"});
        claim(!m.equals(t));
        t.updateRecord(4,"NumPets","1");
        claim(m.equals(t));
        t.addRecord(new String[]{"Amy","0"});
        claim(!m.equals(t));
    }

    private void testAddRecordAsSingleString(Table t)
    {
        t.addRecord("Kat, 2");
        claim(t.getNumRecords() == 3);
        Record r = t.getRecord(5);
        claim(r.getField(0).equals("Kat"));
        claim(r.getField(1).equals("2"));
    }

    private void testPrintTable(Table t)
    {
        t.printTable();
    }

    private void testSaveTableToFile(Table t)
    {
        claim(t.getIsDirty());
        String parentDirPath = "dbTestFolders/table/";
        try
        {
            t.saveTableToFile(parentDirPath, true);
            claim(t.equals(TableFileReadWriter.readFromFile(t.getName(), parentDirPath, true)));
            claim(!t.getIsDirty());
        }
        catch (IOException e)
        {
            claim (false);
        }
        t.addRecord("Joe, 3");
        claim(t.getIsDirty());
        try
        {
            t.saveTableToFile(parentDirPath, false);
            claim(t.equals(TableFileReadWriter.readFromFile(t.getName(), parentDirPath, false)));
            claim(!t.getIsDirty());
        }
        catch (IOException e)
        {
            claim(false);
        }
    }

    private void testIsDirty(Table t)
    {
        String parentDirPath = "dbTestFolders/table/";
        claim(!t.getIsDirty());

        // Rename Table
        t.rename("NewName");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Rename column
        t.renameColumn("NumPets", "SomeNum");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Add column
        t.addColumn(1, "NewColumn", "Blah");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Delete column
        t.deleteColumn("NewColumn");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Add Record
        t.addRecord("Another, test");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Update Record
        t.updateRecord(6, "Name", "Elliot");
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Delete Record
        t.deleteRecord(6);
        claim(t.getIsDirty());
        try {t.saveTableToFile(parentDirPath, true);}
        catch (IOException e) { claim(false); }
        claim(!t.getIsDirty());

        // Load Table
        try
        {
            Table o = TableFileReadWriter.readFromFile(t.getName(), parentDirPath, true);
            claim(!o.getIsDirty());
        }
        catch (IOException e)
        {
            claim(false);
        }
    }

    private void testCreateTableFromDataTest(Table t)
    {
        String[][] tableData = t.getTableData();
        claim(t.equals(Table.createTableFromData(t.getName(), tableData)));
    }
}
