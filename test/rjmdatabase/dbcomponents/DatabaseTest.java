package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import rjmdatabase.fileutils.FileUtil;
import java.io.IOException;
import java.io.File;

public class DatabaseTest extends TestBase
{
    String testFolder = "dbTestFolders/databaseTest";
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
    public void testHasTable()
    {
        claim(db.hasTable(personTable.getName()));
        claim(db.hasTable(animalTable.getName()));
        claim(!db.hasTable("NotATable"));
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

    /*@Test
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
    }*/
}
