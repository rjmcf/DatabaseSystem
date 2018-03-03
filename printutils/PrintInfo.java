package printutils;

/**
 * A utility class that passes information from Table to TablePrinter.
 * @author Rjmcf
 */
public class PrintInfo
{
    String tableName;
    String recordSeparator;
    String[][] linesToPrint;

    /**
     * Gets the Table name to be printed.
     * @return The Table name.
     */
    public String getTableName()
    {
        return tableName;
    }

    /**
     * Sets the name of the Table to be printed.
     * @param tN The table name.
     */
    public void setTableName(String tN)
    {
        tableName = tN;
    }

    /**
     * Gets the String used to separate Records.
     * @return The Record separator.
     */
    public String getRecordSeparator()
    {
        return recordSeparator;
    }

    /**
     * Sets the String used to separate Records.
     * @param rS The Record separator.
     */
    public void setRecordSeparator(String rS)
    {
        recordSeparator = rS;
    }

    /**
     * Gets the list of the lines to print. The first level represents Records,
     * the second represents lines within fields of Records.
     * @return The lines to print.
     */
    public String[][] getLinesToPrint()
    {
        return linesToPrint;
    }

    /**
     * Sets the list of the lines to print.
     * @param lTP The lines to print.
     */
    public void setLinesToPrint(String[][] lTP)
    {
        linesToPrint = lTP;
    }
}
