package rjmdatabase.dbcomponents;

public class TablePrinterTest
{
    /**
     * Run tests for TablePrinter
     * @param args Command line args
     */
    public static void main(String[] args) {
        System.out.println("Testing TablePrinter");
        TablePrinterTest tpt = new TablePrinterTest();
        tpt.test();
        System.out.println("Testing TablePrinter complete");
    }

    private void test()
    {
        Table t = new Table("Person", "Name, Address");
        t.addRecord(new String[]{"Robin", "XX Nilford Road\nLeicester\nLE3 3GF"});
        t.addRecord(new String[]{"Laura\nCollins", "XX Mewton Avenue\nBodworth\nCV2 0UQ"});
        TablePrinter.printTable(t.getName(), t.getTableData());
    }
}
