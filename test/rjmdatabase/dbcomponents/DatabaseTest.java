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
        claim(personTable.equals(db.getTable("Person")));
        claim(animalTable.equals(db.getTable("Animal")));

        try
        {
            db.addTable(personTable);
            claim(false);
        }
        catch (IllegalArgumentException e)
        {
            // test passed
        }

        try
        {
            db.getTable("Random");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }

        try
        {
            db.saveDatabase();
        }
        catch (IOException e)
        {
            claim(false);
        }

        try
        {
            Database.loadDatabase(folderName, !usingSync);
            claim(false);
        }
        catch (IllegalArgumentException e)
        {
            // test passed
        }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            Database.loadDatabase("fakeFolderName", usingSync);
            claim(false);
        }
        catch (IllegalArgumentException e)
        {
            // test passed
        }
        catch (IOException e)
        {
            claim(false);
        }

        try
        {
            Database loaded = Database.loadDatabase(folderName, usingSync);
            claim(personTable.equals(loaded.getTable("Person")));
            claim(animalTable.equals(loaded.getTable("Animal")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            claim(false);
        }
    }
}
