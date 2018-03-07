package rjmdatabase;

import rjmdatabase.dbcomponents.Record;
import rjmdatabase.dbcomponents.Table;
import rjmdatabase.dbcomponents.Database;
import rjmdatabase.fileutils.FileUtil;
import rjmdatabase.fileutils.TableFileReadWriter;
import rjmdatabase.printutils.TablePrinter;

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
        Record.main(args);
        Table.main(args);
        FileUtil.main(args);
        TableFileReadWriter.main(args);
        TablePrinter.main(args);
        Database.main(args);
    }
}
