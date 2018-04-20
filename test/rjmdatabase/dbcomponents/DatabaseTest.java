package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import rjmdatabase.testutils.PrintStreamFileWriter;
import rjmdatabase.fileutils.FileUtil;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class DatabaseTest extends TestBase
{
    String testFolder = "dbTestFolders/databaseTest";
    String printTestFolderPath = "printTestOutput";
    Table personTable;
    Table animalTable;
    Database db;

    /**
     * Run tests for Database.
     * @param args Command line args
     */
    public static void main(String[] args) {
        DatabaseTest tester = new DatabaseTest();
        tester.startTest();
    }

    @Override
    protected void beforeTest()
    {
        personTable = new Table("Person", "Name, Address");
        animalTable = new Table("Animal", "Name, Type, Owner");

        db = new Database(testFolder);
        db.addTable("Person", "Name, Address");
        db.addTable("Animal", "Name, Type, Owner");
    }

    @Override
    protected void afterTest()
    {
        FileUtil.deleteDirIfExists(new File(testFolder));
    }

    @Test
    public void testGetTableNames()
    {
        String[] tableNames = db.getTableNames();
        claim(Stream.of(tableNames).anyMatch(Predicate.isEqual("Person")), "Person table not present");
        claim(Stream.of(tableNames).anyMatch(Predicate.isEqual("Animal")), "Animal table not present");
    }

    @Test
    public void testAddTable()
    {
        try
        {
            db.addTable("Person", "This, Shouldnt, Work");
            claim(false, "Table already exists.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        db.addTable("NewTable","Some, New, Fields");
        claim(db.hasTable("NewTable"), "Newable not present.");
    }

    @Test
    public void testGetTable()
    {
        claim(personTable.equals(db.getTable(personTable.getName())), "Stored Table does not equal original.");
        claim(animalTable.equals(db.getTable(animalTable.getName())), "Stored Table does not equal original.");

        try
        {
            db.addTable(personTable.getName(), personTable.getFieldNames());
            claim(false, "Table already in Database, add should fail.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        try
        {
            db.getTable("Random");
            claim(false, "Table not in Database, get should fail.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
    }

    @Test
    public void testRenameTable()
    {
        try
        {
            db.renameTable("NotATable", "NewTableName");
            claim(false, "Table that does not exist should not be renamed.");
        }
    catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.renameTable("Animal", "Person");
            claim(false, "Cannot change name to already existing table name.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        db.renameTable("Person", "NewTableName");
        claim(!db.hasTable("Person"), "Old table name should not be present.");
        claim(db.hasTable("NewTableName"), "New Table name should be present.");
        Table newTable = db.getTable("NewTableName");
        newTable.rename("Person");
        claim(personTable.equals(newTable), "Name should be only thing changed.");
    }

    @Test
    public void testHasTable()
    {
        claim(db.hasTable(personTable.getName()), "Should have this table.");
        claim(db.hasTable(animalTable.getName()), "Should have this table.");
        claim(!db.hasTable("NotATable"), "Shouldn't have this table.");
    }

    @Test
    public void testGetFieldNamesAsArray()
    {
        try
        {
            db.getFieldNamesAsArray("NotATable.");
            claim(false, "Should not be able to get field names from table not present.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        String[] animalFields = db.getFieldNamesAsArray("Animal");
        claim(animalFields.length == 3, "Name list has incorrect number of fields.");
        claim("Name".equals(animalFields[0]), "Incorrect field name.");
        claim("Type".equals(animalFields[1]), "Incorrect field name.");
        claim("Owner".equals(animalFields[2]), "Incorrect field name.");
    }

    @Test
    public void testAddRecord()
    {
        try
        {
            db.addRecord("Random", "Some, Values");
            claim(false, "Can't add Record to Table that doesn't exist.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.addRecord(personTable.getName(), "Too, Many, Fields");
            claim(false, "Too many fields for Table.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }
        try
        {
            db.addRecord(animalTable.getName(), "Too, Few");
            claim(false, "Too few fields for Table.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        db.addRecord(personTable.getName(), "Kenneth, Some Place");
        Table theTable = db.getTable(personTable.getName());
        Record newRecord = theTable.getRecord(0);
        claim("Kenneth".equals(newRecord.getField(0)), "Incorrect field values.");
        claim("Some Place".equals(newRecord.getField(1)), "Incorrect field values.");
        try
        {
            newRecord.getField(2);
            claim(false, "Should only have 2 fields.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
    }

    @Test
    public void testPrintTable()
    {
        try
        {
            db.printTable("NotATable");
            claim(false, "Can't print non-existent Table.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        File testFolder = new File(printTestFolderPath);
        String testFileName = printTestFolderPath + "/dbPrintTest.txt";
        try (PrintStreamFileWriter pS = new PrintStreamFileWriter(testFileName))
        {
            TablePrinter.setPrintStream(pS);
            db.printTable("Animal");
            TablePrinter.setPrintStream(System.out);
        }
        catch (FileNotFoundException e)
        {
            claim(false, "File not found.");
        }

        try
        {
            ArrayList<String> lines = FileUtil.readFile(testFileName);
            claim(lines.size() == 7, "Incorrect size of output.");
            claim(lines.get(0).equals(""), "Incorrect output");
            claim(lines.get(1).equals("Animal"), "Incorrect output");
            claim(lines.get(2).equals(""), "Incorrect output");
            claim(lines.get(3).equals("|----------+------+------+-------|"), "Incorrect output");
            claim(lines.get(4).equals("| KeyTable | Name | Type | Owner |"), "Incorrect output");
            claim(lines.get(5).equals("|----------+------+------+-------|"), "Incorrect output");
            claim(lines.get(6).equals(""), "Incorrect output");
        }
        catch (IOException e)
        {
            claim(false, "IOException while reading file.");
        }

        FileUtil.deleteDirIfExists(testFolder);
    }

    @Test
    public void testRenameColumn()
    {
        try
        {
            db.renameColumn("NotATable", "A", "B");
            claim(false, "Can't rename column of non-existent Table.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.renameColumn("Animal", "A", "B");
            claim(false, "Can't rename non-existent column of Table.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.renameColumn("Animal", "Name", "Type");
            claim(false, "Can't rename column of Table to already existing column name.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        db.renameColumn("Animal", "Name", "Colour");
        claim(db.getFieldNamesAsArray("Animal")[0].equals("Colour"), "Rename has failed.");
    }

    @Test
    public void testDeleteRecord()
    {
        String tableName = personTable.getName();
        db.addRecord(tableName, "John, Address1");
        db.addRecord(tableName, "Jane, Address2");

        claim(db.getTable(tableName).getNumRecords() == 2, "Table must have 2 records before test starts.");

        try
        {
            db.deleteRecord("FakeTable", 0);
            claim(false, "Table does not exist.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.deleteRecord(tableName, 2);
            claim(false, "Should not be able to delete Record not present.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        db.deleteRecord(tableName, 0);
        claim(db.getTable(tableName).getNumRecords() == 1, "Should only have 1 Record after deletion.");

        try
        {
            db.deleteRecord(tableName, 0);
            claim(false, "Cannot delete same record twice.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        claim(db.getTable(tableName).getRecord(1).getField(0).equals("Jane"), "Data in remaining Record must be correct.");
    }

    @Test
    public void testUpdateRecord()
    {
        String tableName = personTable.getName();
        db.addRecord(tableName, "John, Address1");
        db.addRecord(tableName, "Jane, Address2");

        try
        {
            db.updateRecord("FakeTable", 0, "Name", "Phil");
            claim(false, "Table does not exist");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.updateRecord(tableName, 2, "Name", "Phil");
            claim(false, "Record does not exist");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            db.updateRecord(tableName, 0, "FakeColumn", "Phil");
            claim(false, "Column does not exist");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        db.updateRecord(tableName, 0, "Name", "Phil");
        Table theTable = db.getTable(tableName);
        Record theRecord = theTable.getRecord(0);
        String theField = theRecord.getField(0);
        claim(theField.equals("Phil"), "Update has not succeeded.");
    }

    @Test
    public void testSaveDatabase()
    {
        try
        {
            db.saveDatabase();
        }
        catch (IOException e)
        {
            claim(false, "IOException while saving Database.");
        }

        Database loaded = new Database(testFolder);
        claim(personTable.equals(loaded.getTable("Person")), "Loaded Table does not match original.");
        claim(animalTable.equals(loaded.getTable("Animal")), "Loaded Table does not match original.");
    }
}
