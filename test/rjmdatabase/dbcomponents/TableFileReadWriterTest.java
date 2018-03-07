package rjmdatabase.dbcomponents;

import rjmdatabase.dbcomponents.Table;

import java.io.IOException;
import java.io.File;

public class TableFileReadWriterTest
{
    /**
     * Run tests for TableFileReadWriter.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Testing TableFileReadWriter");
        TableFileReadWriterTest tfrwt = new TableFileReadWriterTest();
        tfrwt.test();
        System.out.println("Testing TableFileReadWriter complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test Failed");
    }

    private void test()
    {
        String oldFolder = "dbTestFolders/tableFiles";
        String newFolder = "dbTestFolders/newTableFiles";
        runTestTableFileReadWriter(oldFolder, true, false);
        runTestTableFileReadWriter(oldFolder, false, false);
        runTestTableFileReadWriter(newFolder, true, true);
        runTestTableFileReadWriter(newFolder, false, true);
    }

    private void runTestTableFileReadWriter(String pDP, boolean uS, boolean deleteDir)
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
            claim(false);
        }
        try
        {
            Table r = TableFileReadWriter.readFromFile(t.getName(), pDP, uS);
            claim(t.equals(r));
        }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            Table n = TableFileReadWriter.readFromFile("NotATable", pDP, uS);
            claim(false);
        }
        catch (IllegalArgumentException i)
        {
            // test passed
        }
        catch (IOException e)
        {
            // Should be the first, not the second exception type.
            claim(false);
        }

        claim(TableFileReadWriter.getTableNameFromFileName(".DS_Store") == null);
        try
        {
            TableFileReadWriter.getTableNameFromFileName("tableName.txt");
            claim(false);
        }
        catch (IllegalArgumentException e)
        {
            // test passed
        }

        String tableName = "tableName";
        claim(tableName.equals(TableFileReadWriter.getTableNameFromFileName(tableName + TableFileReadWriter.SERIALIZATION_FILE_EXT)));
        claim(tableName.equals(TableFileReadWriter.getTableNameFromFileName(tableName + TableFileReadWriter.CUSTOM_METHOD_FILE_EXT)));
        if (deleteDir)
            TableFileReadWriter.deleteDir(new File(pDP));
    }
}
