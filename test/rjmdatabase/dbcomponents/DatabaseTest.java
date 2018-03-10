package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import java.io.IOException;

public class DatabaseTest extends TestBase
{
    static String serTestFolder = "dbTestFolders/databaseSer";
    static String rjmTestFolder = "dbTestFolders/databaseRjm";
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

    @Test
    public void testDatabaseSer()
    {
        Database db = Database.createNewDatabase(rjmTestFolder, false);
        repeatableDatabaseTest(db, rjmTestFolder, false);
    }

    @Test
    public void testDatabaseRjm()
    {
        Database db = Database.createNewDatabase(serTestFolder, true);
        repeatableDatabaseTest(db, serTestFolder, true);
    }

    private void repeatableDatabaseTest(Database db, String folderName, boolean usingSync)
    {

        db.addTable(personTable);
        db.addTable(animalTable);
        claim(personTable.equals(db.getTable("Person")), "Stored Table does not equal original.");
        claim(animalTable.equals(db.getTable("Animal")), "Stored Table does not equal original.");

        try
        {
            db.addTable(personTable);
            claim(false, "Table already in Database, add should fail.");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }

        try
        {
            db.getTable("Random");
            claim(false, "Table not in Database, get should fail.");
        }
        catch (IndexOutOfBoundsException e)
        { /* test passed */ }

        try
        {
            db.saveDatabase();
        }
        catch (IOException e)
        {
            claim(false, "IOException whle saving Database.");
        }

        try
        {
            Database.loadDatabase(folderName, !usingSync);
            claim(false, "Incorrect method used to load files.");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            Database.loadDatabase("fakeFolderName", usingSync);
            claim(false, "Folder name does not exist, loading should fail.");
        }
        catch (IllegalArgumentException e)
        { /* test passed */ }
        catch (IOException e)
        {
            claim(false, "IOException while loading Database, should not be trying to load fake Database.");
        }

        try
        {
            Database loaded = Database.loadDatabase(folderName, usingSync);
            claim(personTable.equals(loaded.getTable("Person")), "Loaded Table does not match original.");
            claim(animalTable.equals(loaded.getTable("Animal")), "Loaded Table does not match original.");
        }
        catch (IOException e)
        {
            claim(false, "IOException while loading Database.");
        }
    }
}
