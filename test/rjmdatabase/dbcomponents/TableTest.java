package rjmdatabase.dbcomponents;

import java.io.IOException;

public class TableTest extends rjmdatabase.TestBase
{
    /**
     * Runs all the tests for Table.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        TableTest tester = new TableTest();
        tester.startTest(args);
    }

    @Override
    protected void test(String[] args)
    {
        Table t = new Table("Animal", "Name, Breed");
        testGetters(t);
        testAddRecord(t);
        testGetRecord(t);
        testDeleteRecord(t);
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

    private void testGetters(Table t)
    {
        claim(t.getName().equals("Animal"));
        claim(t.getNumFields() == 2);
        claim(t.getFieldNames().equals("Name, Breed"));
        claim(t.getNumRecords() == 0);
    }

    private void testAddRecord(Table t)
    {
        t.addRecord(new String[] {"Dog", "Corgi"});
        claim(t.getNumRecords() == 1);
        try
        {
            t.addRecord(new String[]{"Dog"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        try
        {
            t.addRecord(new String[]{"Dog", "Corgi", "Cute"});
            claim(false);
        }
        catch(IllegalArgumentException e)
        {
            // test passed
        }
        t.addRecord(new String[] {"Cat", "Bengal"});
        claim(t.getNumRecords() == 2);
    }

    private void testGetRecord(Table t)
    {
        Record r = t.getRecord(0);
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
            t.getRecord(2);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            t.getRecord(-1);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        r = t.getRecord(1);
        claim(r.getField(0).equals("Cat"));
        claim(r.getField(1).equals("Bengal"));
    }

    private void testDeleteRecord(Table t)
    {
        try
        {
            t.deleteRecord(2);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
        t.deleteRecord(0);
        claim(t.getNumRecords() == 1);
        try
        {
            t.deleteRecord(0);
            claim(false);
        }
        catch(IndexOutOfBoundsException e)
        {
            // test passed
        }
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
