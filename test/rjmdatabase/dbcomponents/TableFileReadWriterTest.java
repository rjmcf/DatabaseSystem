package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import rjmdatabase.dbcomponents.Table;
import rjmdatabase.fileutils.FileUtil;

import java.io.IOException;
import java.io.File;

public class TableFileReadWriterTest extends TestBase
{
    private String oldFolder = "dbTestFolders/tableFiles";
    private String newFolder = "dbTestFolders/newTableFiles";

    /**
     * Run tests for TableFileReadWriter.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        TableFileReadWriterTest tester = new TableFileReadWriterTest();
        tester.startTest();
    }

    @Override
    public void beforeTest()
    {
        FileUtil.makeDirsIfNeeded(new File(oldFolder));
        FileUtil.deleteDir(new File(newFolder));
    }

    @Test
    public void testSerializingOldFolder()
    {
        repeatableTableFileReadWriter(oldFolder, true);
    }

    @Test
    public void testSerializingNewFolder()
    {
        repeatableTableFileReadWriter(newFolder, true);
    }

    @Test
    public void testRjmMethodOldFolder()
    {
        repeatableTableFileReadWriter(oldFolder, false);
    }

    @Test
    public void testRjmMethodNewFolder()
    {
        repeatableTableFileReadWriter(newFolder, false);
    }

    private void repeatableTableFileReadWriter(String pDP, boolean uS)
    {
        Table t = new Table("TestTable", "Attr1, Attr2");
        t.addRecord(new String[]{"Val1", "Val2"});
        t.addRecord(new String[]{"Val, 3!\n", "  Val  4  \n"});
        try
        {
            TableFileReadWriter.writeToFile(t, pDP, uS);
        }
        catch (IOException e)
        {
            claim(false, "IOException while writing.");
        }
        try
        {
            Table r = TableFileReadWriter.readFromFile(t.getName(), pDP, uS);
            claim(t.equals(r), "Read Table does not match original.");
        }
        catch (IOException e)
        {
            claim(false, "IOException while reading.");
        }
        try
        {
            TableFileReadWriter.readFromFile("NotATable", pDP, uS);
            claim(false, "Should not be able to read from a Table file that doesn't exist.");
        }
        catch (IllegalArgumentException i) { /* test passed */ }
        catch (IOException e)
        {
            claim(false, "Should not be trying to read from Table file that doesn't exist.");
        }

        claim(TableFileReadWriter.getTableNameFromFileName(".DS_Store") == null, "Should not consider .DS_Store as a Table file.");
        try
        {
            TableFileReadWriter.getTableNameFromFileName("tableName.txt");
            claim(false, "Incorrect file extension.");
        }
        catch (IllegalArgumentException e) { /* test passed */ }

        String tableName = "tableName";
        claim(tableName.equals(TableFileReadWriter.getTableNameFromFileName(tableName + TableFileReadWriter.SERIALIZATION_FILE_EXT)), "Table name does not match.");
        claim(tableName.equals(TableFileReadWriter.getTableNameFromFileName(tableName + TableFileReadWriter.CUSTOM_METHOD_FILE_EXT)), "Table name does not match.");
    }
}
