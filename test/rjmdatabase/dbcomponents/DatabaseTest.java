package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import rjmdatabase.fileutils.FileUtil;
import java.io.IOException;
import java.io.File;

public class DatabaseTest extends TestBase
{
    static String testFolder = "dbTestFolders/databaseTest";
    static Table personTable;
    static Table animalTable;

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
        personTable.addRecord("Robyn, XX Nilfrod Avenue\nLoughborough");
        animalTable = new Table("Animal", "Name, Type, Owner");
        animalTable.addRecord("Minnie, Cat, 0");
    }

    @Override
    protected void afterTest()
    {
        FileUtil.deleteDirIfExists(new File(testFolder));
    }

    @Test
    public void testDatabase()
    {
        Database db = new Database(testFolder);

        db.addTable(personTable);
        db.addTable(animalTable);
        claim(personTable.equals(db.getTable("Person")), "Stored Table does not equal original.");
        claim(animalTable.equals(db.getTable("Animal")), "Stored Table does not equal original.");

        try
        {
            db.addTable(personTable);
            claim(false, "Table already in Database, add should fail.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        try
        {
            db.getTable("Random");
            claim(false, "Table not in Database, get should fail.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        try
        {
            db.saveDatabase();
        }
        catch (IOException e)
        {
            claim(false, "IOException whle saving Database.");
        }

        Database loaded = new Database(testFolder);
        claim(personTable.equals(loaded.getTable("Person")), "Loaded Table does not match original.");
        claim(animalTable.equals(loaded.getTable("Animal")), "Loaded Table does not match original.");
    }
}
