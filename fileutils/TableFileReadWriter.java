package fileutils;

import dbcomponents.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.DatatypeConverter;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A utility class that reads and writes Tables to files. A Singleton class.
 * @author Rjmcf
 */
public class TableFileReadWriter
{
    private static TableFileReadWriter instance;

    // The folder in which all Tables are saved.
    private String parentDirPath;
    // The file extension with which table files are saved.
    private String fileExtension;
    // Determines which method of reading and writing files to be used.
    private boolean useSerialization;
    // The encoding used to convert between hex and String.
    private static final Charset ENCODING = StandardCharsets.UTF_8;
    // The String used to separate fields in files.
    private static final String FIELD_SEPARATOR = String.valueOf((char)0x1F);

    private TableFileReadWriter() { }

    /**
     * Gets the TableFileReadWriter instance.
     * @param  pDP The parent directory path under which to store all Table files.
     * @param  uS  Whether to use the Serialization method or my own method.
     * @return     The TableFileReadWriter instance.
     */
    public static TableFileReadWriter getInstance(String pDP, boolean uS)
    {
        if (instance == null)
            instance = new TableFileReadWriter();

        instance.parentDirPath = pDP;
        instance.useSerialization = uS;
        instance.fileExtension = uS ? ".ser" : ".rjmTable";
        return instance;
    }

    /**
     * Writes the given table to a file, using the chosen method.
     * @param  t           The Table to write.
     * @throws IOException If an io exception occurred.
     */
    public void writeToFile(Table t) throws IOException
    {
        if (useSerialization)
            serWriteToFile(t);
        else
            rjmWriteToFile(t);
    }

    /**
     * Reads a table from a file, using the chosen method.
     * @param  name        The name of the table to be read.
     * @return             The Table instance that has been loaded.
     * @throws IOException If an io exception occurred.
     */
    public Table readFromFile(String name) throws IOException
    {
        if (useSerialization)
            return serReadFromFile(name);
        else
            return rjmReadFromFile(name);
    }


    private void serWriteToFile(Table t) throws IOException
    {
        String fPath = parentDirPath + t.getName() + fileExtension;
        // Need to make sure that the parent directories exist first.
        FileUtil.makeParentDirsIfNeeded(fPath);
        try(FileOutputStream fileOut = new FileOutputStream(fPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);)
        {
            // This requires both Table and Record to implement Serializable.
            out.writeObject(t);
        }
    }

    private Table serReadFromFile(String tableName) throws IOException
    {
        Table t = null;
        try
        {
            FileInputStream fileIn = new FileInputStream(parentDirPath + tableName + fileExtension);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            // This requires both Table and Record to implement Serializable.
            t = (Table) in.readObject();
            in.close();
            fileIn.close();
            return t;
        }
        catch (FileNotFoundException f)
        {
            throw new IllegalArgumentException("Table " + tableName + " file not found");
        }
        catch (ClassNotFoundException c)
        {
            throw new Error("Table class not found");
        }
    }

    private void rjmWriteToFile(Table t) throws IOException
    {
        String[][] tableData = t.getTableData();

        // The lines to write.
        String[] lines = new String[tableData.length-1];
        StringJoiner joiner = new StringJoiner(FIELD_SEPARATOR);

        // Build the line to print for each row.
        for (int row = 1; row < tableData.length; row++)
        {
            String[] rowFields = tableData[row];
            joiner = new StringJoiner(FIELD_SEPARATOR);
            for (int f = 0; f < rowFields.length; f++)
            {
                // We convert to hex to allow strange characters in these fields.
                joiner.add(convertStringToHex(rowFields[f]));
            }
            lines[row - 1] = joiner.toString();
        }

        String filePath = parentDirPath + t.getName() + fileExtension;
        FileUtil.writeFile(filePath, lines);
    }

    private Table rjmReadFromFile(String name) throws IOException
    {
        ArrayList<String> lines = FileUtil.readFile(parentDirPath + name + fileExtension);
        String[][] tableData = new String[lines.size()][];
        for (int row = 0; row < lines.size(); row++)
        {
            tableData[row] = lines.get(row).split(FIELD_SEPARATOR);
            for (int col = 0; col < tableData[row].length; col++)
                tableData[row][col] = convertHexToString(tableData[row][col]);
        }
        return Table.createTableFromData(name, tableData);
    }

    private static String convertHexToString(String h)
    {
        byte[] bytes = DatatypeConverter.parseHexBinary(h);
        return new String(bytes, ENCODING);
    }

    private String convertStringToHex(String s)
    {
        String result = String.format("%x", new BigInteger(1, s.getBytes(ENCODING)));
        // Remember to add a leading "0" if we need it.
        return result.length() % 2 == 0 ? result : "0" + result;
    }

    /**
     * Runs the tests on this class.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        System.out.println("Testing TableFileReadWriter");
        TableFileReadWriter tfrw1 = TableFileReadWriter.getInstance("tableFiles/", true);
        tfrw1.test(args);
        TableFileReadWriter tfrw2 = TableFileReadWriter.getInstance("tableFiles/", false);
        tfrw2.test(args);
        TableFileReadWriter tfrw3 = TableFileReadWriter.getInstance("newTableFiles/", true);
        tfrw3.test(args);
        deleteDir(new File("newTableFiles/"));
        TableFileReadWriter tfrw4 = TableFileReadWriter.getInstance("newTableFiles/", false);
        tfrw4.test(args);
        deleteDir(new File("newTableFiles/"));
        System.out.println("Testing complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test Failed");
    }

    private void test(String args[])
    {
        Table t = new Table("TestTable", "Attr1, Attr2");
        t.addRecord(new String[]{"Val1", "Val2"});
        t.addRecord(new String[]{"Val, 3!\n", "  Val  4  \n"});
        try
        {
            writeToFile(t);
        }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            Table r = readFromFile(t.getName());
            claim(t.equals(r));
        }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            Table n = readFromFile("NotATable");
            claim(false);
        }
        catch (IllegalArgumentException i)
        {
            // test passed
        }
        catch (IOException e)
        {
            // Should be the first, not the second exception type.
            claim(false);
        }
    }

    private static void deleteDir(File file)
    {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
