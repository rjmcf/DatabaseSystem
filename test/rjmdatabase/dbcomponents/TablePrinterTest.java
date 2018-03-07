package rjmdatabase.dbcomponents;

public class TablePrinterTest extends rjmdatabase.TestBase
{
    /**
     * Run tests for TablePrinter
     * @param args Command line args
     */
     public static void main(String[] args) {
         TablePrinterTest tester = new TablePrinterTest();
         tester.startTest(args);
     }

    @Override
    protected void test(String[] args)
    {
        Table t = new Table("Person", "Name, Address");
        t.addRecord(new String[]{"Robin", "XX Nilford Road\nLeicester\nLE3 3GF"});
        t.addRecord(new String[]{"Laura\nCollins", "XX Mewton Avenue\nBodworth\nCV2 0UQ"});
        TablePrinter.printTable(t.getName(), t.getTableData());
    }
}
