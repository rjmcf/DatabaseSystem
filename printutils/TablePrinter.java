package printutils;

import dbcomponents.Table;

/**
 * Utility class to pretty print a Table to the console.
 * @author Rjmcf
 */
public class TablePrinter
{
    private static final boolean INCLUDE_FINAL_COL_LINE = true;
    private static final boolean INCLUDE_FIRST_COL_LINE = true;
    private static final boolean INCLUDE_FINAL_ROW_LINE = true;
    private static final boolean INCLUDE_FIRST_ROW_LINE = true;

    /**
     * Prints the supplied Table to the console.
     * @param t The Table to be printed.
     */
    public static void printTable(Table t)
    {
        // The Table needs to know whether to include the Field separator at
        // the start and end of lines, so we pass the two booleans.
        PrintInfo printInfo = t.getPrintInfo(INCLUDE_FIRST_COL_LINE, INCLUDE_FINAL_COL_LINE);
        String recordSeparator = printInfo.getRecordSeparator();

        // Actually print the table
        System.out.println();
        System.out.println(printInfo.getTableName());
        System.out.println();
        if (INCLUDE_FIRST_ROW_LINE)
            System.out.println(recordSeparator);
        String[][] linesToPrint = printInfo.getLinesToPrint();
        // linesToPrint[0][0] is the header row of the field names.
        System.out.println(linesToPrint[0][0]);
        System.out.println(recordSeparator);

        for (int record = 1; record < linesToPrint.length; record++)
        {
            String[] linesForRecord = linesToPrint[record];
            for (String line: linesForRecord)
                System.out.println(line);
            // Only print the record separator if we are between records or
            // we want to include the separator after the final line anyway.
            if (record != linesToPrint.length - 1 || INCLUDE_FINAL_ROW_LINE)
                System.out.println(recordSeparator);
        }

        System.out.println();
    }

    /**
     * Runs the tests on this class.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("Testing TablePrinter");
        Table t = new Table("Person", "Name, Address");
        t.addRecord(new String[]{"Robin", "XX Nilford Road\nLeicester\nLE3 3GF"});
        t.addRecord(new String[]{"Laura\nCollins", "XX Mewton Avenue\nBodworth\nCV2 0UQ"});
        TablePrinter.printTable(t);
        System.out.println("Testing complete");
    }
}
