package rjmdatabase;

import rjmdatabase.dbcomponents.RecordTest;
import rjmdatabase.dbcomponents.TableTest;
import rjmdatabase.dbcomponents.DatabaseTest;
import rjmdatabase.dbcomponents.TableFileReadWriterTest;
import rjmdatabase.dbcomponents.TablePrinterTest;
import rjmdatabase.fileutils.FileUtilTest;
import rjmdatabase.exceptions.TestsElsewhereException;

/**
 * Runs all the tests for all the classes in this project.
 * @author Rjmcf
 */
public class TestRunner
{
    private TestRunner() {}

    /**
     * Entry point to the TestRunner. Simply runs all the tests.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        runAllTests(args);
    }

    private static void runAllTests(String[] args)
    {
        FileUtilTest.main(args);
        try
        { TableFileReadWriterTest.main(args); }
        catch (TestsElsewhereException e)
        { }
        try
        { TablePrinterTest.main(args); }
        catch (TestsElsewhereException e)
        { }
        RecordTest.main(args);
        TableTest.main(args);
        DatabaseTest.main(args);
    }
}
