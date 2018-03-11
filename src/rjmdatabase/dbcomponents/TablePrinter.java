package rjmdatabase.dbcomponents;

import java.util.StringJoiner;
import java.util.Collections;

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
    // The String which, when repeated, is used to build the Record separator
    private static final String RECORD_SEPARATOR_CHAR = "-";
    // The start of the Record separator when we include the first line.
    private static final String RECORD_SEPARATOR_FIRST_CHAR = "|-";
    // The String that appears in Record separator on the boundary between two
    // fields.
    private static final String RECORD_SEPARATOR_BETWEEN_CHAR = "-+-";
    // The end of the Record separator when we include the last line.
    private static final String RECORD_SEPARATOR_LAST_CHAR = "-|";
    // The String used to separate fields when printing.
    private static final String FIELD_SEPARATOR = " | ";

    /**
     * Prints the supplied Table to the console.
     * @param t The Table to be printed.
     */
    static void printTable(String tableName, String[][] tableData)
    {
        // First row contains version number, not important for here.
        // Stores some useful stats about the size of the table.
        int numCols = tableData[1].length;
        int numRows = tableData.length - 1;

        // Stores the maximum sizes of individual columns and rows.
        int[] colWidths = new int[numCols];
        int[] rowHeights = new int[numRows];

        // Set the maximum height and width for every row and column by checking
        // the number of lines in each field and the width of each field.
        String value;
        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numCols; col++)
            {
                // +1 to skip the version number.
                value = tableData[row+1][col];
                String[] lines = value.split("\n");
                // If this value has more lines than the current max height for this row,
                // update the max height.
                if (lines.length > rowHeights[row])
                    rowHeights[row] = lines.length;
                // If there's a line in this field wider than the current max width for
                // this column, update the max width.
                for (String line: lines)
                    if (line.length() > colWidths[col])
                        colWidths[col] = line.length();
            }

        // Build the record separator based on how wide each column is.
        String recSepPrefix = INCLUDE_FIRST_COL_LINE ? RECORD_SEPARATOR_FIRST_CHAR : "";
        String recSepSuffix = INCLUDE_FINAL_COL_LINE ? RECORD_SEPARATOR_LAST_CHAR : "";
        StringJoiner recSepJ = new StringJoiner(RECORD_SEPARATOR_BETWEEN_CHAR, recSepPrefix, recSepSuffix);
        for (int width: colWidths)
            recSepJ.add(String.join("", Collections.nCopies(width, RECORD_SEPARATOR_CHAR)));
        String recordSeparator = recSepJ.toString();

        // Space for the actual Strings that we are going to print.
        // The first level corresponds to records, the second corresponds to
        // lines within fields in those records.
        String[][] linesToPrint = new String[numRows][];

        // Build the String to print for each row in lines.
        StringJoiner lineJ;
        for (int row = 0; row < numRows; row++)
        {
            // For this record we need to print as many lines as is the maximum
            // height for this record.
            String[] linesInThisRow = new String[rowHeights[row]];

            // Split each field in this record into lines.
            String[][] fieldsInLines = new String[numCols][];
            for (int col = 0; col < numCols; col++)
                // Start from 1 to skip the version number
                fieldsInLines[col] = tableData[row+1][col].split("\n");
            // Now build up each line for this record.
            for (int lineNum = 0; lineNum < rowHeights[row]; lineNum++)
            {
                lineJ = getLineStringJoiner();
                // We add lines of fields to the line column by column.
                for (int col = 0; col < numCols; col++)
                {
                    String formatString = "%-" + colWidths[col] + "s";
                    String insert = lineNum >= fieldsInLines[col].length ? "" : fieldsInLines[col][lineNum];
                    lineJ.add(String.format(formatString, insert));
                }
                linesInThisRow[lineNum] = lineJ.toString();
            }
            linesToPrint[row] = linesInThisRow;
        }

        // Actually print the table
        System.out.println();
        System.out.println(tableName);
        System.out.println();
        if (INCLUDE_FIRST_ROW_LINE)
            System.out.println(recordSeparator);

        for (int row = 0; row < linesToPrint.length; row++)
        {
            String[] linesForRow = linesToPrint[row];
            for (String line: linesForRow)
                System.out.println(line);
            // Only print the record separator if we are between records or
            // we want to include the separator after the final line anyway.
            if (row != linesToPrint.length - 1 || INCLUDE_FINAL_ROW_LINE)
                System.out.println(recordSeparator);
        }

        System.out.println();
    }

    // Gets the StringJoiner used to build up lines to print.
    private static StringJoiner getLineStringJoiner()
    {
        String prefix = INCLUDE_FIRST_COL_LINE ? FIELD_SEPARATOR.replaceFirst("\\s++", "") : "";
        String suffix = INCLUDE_FINAL_COL_LINE ? FIELD_SEPARATOR.replaceFirst("\\s++$", "") : "";
        return new StringJoiner(FIELD_SEPARATOR, prefix, suffix);
    }
}
