package rjmdatabase.dbcomponents;

import java.io.IOException;

public class DatabaseTest
{
    /**
     * Run tests for Database.
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("Testing Database");
        DatabaseTest dT = new DatabaseTest();
        dT.test();
        System.out.println("Testing Database complete");
    }

    private void test()
    {
        String serTestFolder = "dbTestFolders/databaseSer";
        String rjmTestFolder = "dbTestFolders/databaseRjm";
        Database db = Database.createNewDatabase(rjmTestFolder, false);
        testDatabase(db, rjmTestFolder, false);
        db = Database.createNewDatabase(serTestFolder, true);
        testDatabase(db, serTestFolder, true);
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test failed");
    }

    private void testDatabase(Database db, String folderName, boolean usingSync)
    {
        Table personTable = new Table("Person", "Name, Address");
        personTable.addRecord("Robyn, XX Nilfrod Avenue\nLoughborough");
        Table animalTable = new Table("Animal", "Name, Type, Owner");
        animalTable.addRecord("Minnie, Cat, 0");

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
