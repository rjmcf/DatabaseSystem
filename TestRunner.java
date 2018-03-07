import dbcomponents.Record;
import dbcomponents.Table;
import dbcomponents.Database;
import fileutils.FileUtil;
import fileutils.TableFileReadWriter;
import printutils.TablePrinter;
import exceptions.TestsElsewhereException;

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
        FileUtil.main(args);
        try
        { TableFileReadWriter.main(args); }
        catch (TestsElsewhereException e)
        { }
        try
        { TablePrinter.main(args); }
        catch (TestsElsewhereException e)
        { }
        Record.main(args);
        Table.main(args);
        Database.main(args);
    }
}
