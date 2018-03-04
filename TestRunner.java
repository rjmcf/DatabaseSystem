import dbcomponents.Record;
import dbcomponents.Table;
import dbcomponents.Database;
import fileutils.FileUtil;
import fileutils.TableFileReadWriter;
import printutils.TablePrinter;

/**
 * Runs all the tests for all the classes in this project.
 * @author Rjmcf
 */
class TestRunner
{
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
        Record.main(args);
        Table.main(args);
        FileUtil.main(args);
        TableFileReadWriter.main(args);
        TablePrinter.main(args);
        Database.main(args);
    }
}
