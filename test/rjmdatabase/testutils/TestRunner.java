package rjmdatabase.testutils;

import rjmdatabase.dbcomponents.RecordTest;
import rjmdatabase.dbcomponents.TableTest;
import rjmdatabase.dbcomponents.DatabaseTest;
import rjmdatabase.dbcomponents.TableFileReadWriterTest;
import rjmdatabase.dbcomponents.TablePrinterTest;
import rjmdatabase.fileutils.FileUtilTest;

/**
 * Runs all the tests for all the classes in this project.
 * @author Rjmcf
 */
public class TestRunner
{
    /**
     * Entry point to the TestRunner. Simply runs all the tests.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        runAllTests();
    }

    private static void runAllTests()
    {
        TestBase[] testers = new TestBase[]
                                {
                                    new FileUtilTest(),
                                    new RecordTest(),
                                    new TablePrinterTest(),
                                    new TableFileReadWriterTest(),
                                    new TableTest(),
                                    new DatabaseTest(),
                                };
        for (TestBase tester : testers)
        {
            tester.startTest();
        }
    }
}