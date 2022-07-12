package rjmdatabase.dbcomponents;

import rjmdatabase.fileutils.FileUtil;
import rjmdatabase.testutils.PrintStreamFileWriter;
import rjmdatabase.testutils.Test;
import rjmdatabase.testutils.TestBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class TablePrinterTest extends TestBase
{
    private String testFolderName = "printerTestOutput";

    /**
     * Run tests for TablePrinter
     * @param args Command line args
     */
     public static void main(String[] args) {
         TablePrinterTest tester = new TablePrinterTest();
         tester.startTest();
     }

    @Test
    public void test()
    {
        Table t = new Table("Person", "Name, Address");
        t.addRecord(new String[]{"Robin", "XX Nilford Road\nLeicester\nLE3 3GF"});
        t.addRecord(new String[]{"Laura\nCollins", "XX Mewton Avenue\nBodworth\nCV2 0UQ"});
        File testFolder = new File(testFolderName);
        String testFileName = testFolderName + "/printTest.txt";
        try (PrintStreamFileWriter pS = new PrintStreamFileWriter(testFileName))
        {
            TablePrinter.setPrintStream(pS);
            t.printTable();
            TablePrinter.setPrintStream(System.out);
        }
        catch (FileNotFoundException e)
        {
            claim(false, "File not found.");
        }

        try
        {
            ArrayList<String> lines = FileUtil.readFile(testFileName);
            claim(lines.size() == 15, "Incorrect size of output.");
            claim(lines.get( 0).equals(""), "Wrong output");
            claim(lines.get( 1).equals("Person"), "Wrong output");
            claim(lines.get( 2).equals(""), "Wrong output");
            claim(lines.get( 3).equals("|----------+---------+------------------|"), "Wrong output");
            claim(lines.get( 4).equals("| KeyTable | Name    | Address          |"), "Wrong output");
            claim(lines.get( 5).equals("|----------+---------+------------------|"), "Wrong output");
            claim(lines.get( 6).equals("| 0        | Robin   | XX Nilford Road  |"), "Wrong output");
            claim(lines.get( 7).equals("|          |         | Leicester        |"), "Wrong output");
            claim(lines.get( 8).equals("|          |         | LE3 3GF          |"), "Wrong output");
            claim(lines.get( 9).equals("|----------+---------+------------------|"), "Wrong output");
            claim(lines.get(10).equals("| 1        | Laura   | XX Mewton Avenue |"), "Wrong output");
            claim(lines.get(11).equals("|          | Collins | Bodworth         |"), "Wrong output");
            claim(lines.get(12).equals("|          |         | CV2 0UQ          |"), "Wrong output");
            claim(lines.get(13).equals("|----------+---------+------------------|"), "Wrong output");
            claim(lines.get(14).equals(""), "Wrong output");
        }
        catch (IOException e)
        {
            claim(false, "IOException while reading file.");
        }

        FileUtil.deleteDirIfExists(testFolder);
    }
}
