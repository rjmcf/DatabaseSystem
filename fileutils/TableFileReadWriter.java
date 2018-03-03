package fileutils;

import tablesrecords.Table;
import tablesrecords.Record;

import java.io.IOException;
import java.util.ArrayList;

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
    // The folder in which all Tables are saved.
    private static final String PARENT_DIR_PATH = "tableFiles/";


    // The file extension with which table files are saved.
    private String fileExtension;
    // Determines which method of reading and writing files to be used.
    private boolean useSerialization;
    private static TableFileReadWriter instance;

    private TableFileReadWriter() { }

    /**
     * Gets the TableFileReadWriter instance.
     * @param  uS Whether to use the Serialization method or my own method.
     * @return    The TableFileReadWriter instance.
     */
    public static TableFileReadWriter getInstance(boolean uS)
    {
        if (instance == null)
            instance = new TableFileReadWriter();

        instance.useSerialization = uS;
        instance.fileExtension = uS ? ".ser" : ".rjmTable";
        return instance;
    }

    /**
     * Writes the given table to a file, using the chosen method.
     * @param  t The Table to write.
     * @return   Whether the writing was successful.
     */
    public boolean writeToFile(Table t)
    {
        if (useSerialization)
            return serWriteToFile(t);
        else
            return rjmWriteToFile(t);
    }

    /**
     * Reads a table from a file, using the chosen method.
     * @param  name The name of the table to be read.
     * @return      The Table instance that has been loaded.
     */
    public Table readFromFile(String name)
    {
        if (useSerialization)
            return serReadFromFile(name);
        else
            return rjmReadFromFile(name);
    }

    private boolean serWriteToFile(Table t)
    {
        try
        {
            FileOutputStream fileOut = new FileOutputStream(PARENT_DIR_PATH + t.getName() + fileExtension);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            // This requires both Table and Record to implement Serializable.
            out.writeObject(t);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in " + PARENT_DIR_PATH + t.getName() + fileExtension);
            return true;
        }
        catch (IOException i) {
            i.printStackTrace();
            return false;
        }
    }

    private Table serReadFromFile(String tableName)
    {
        Table t = null;
        try
        {
            FileInputStream fileIn = new FileInputStream(PARENT_DIR_PATH + tableName + fileExtension);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            // This requires both Table and Record to implement Serializable.
            t = (Table) in.readObject();
            in.close();
            fileIn.close();
        }
        catch (FileNotFoundException f)
        {
            System.out.println("Table " + tableName + " file not found");
        }
        catch (IOException i)
        {
            System.out.println("Could not read Table " + tableName + " from file");
            i.printStackTrace();
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("Table class not found");
            c.printStackTrace();
        }
        finally
        {
            return t;
        }
    }

    private boolean rjmWriteToFile(Table t)
    {
        String[] lines = t.prepareLinesForWriting();
        String filePath = PARENT_DIR_PATH + t.getName() + fileExtension;
        return FileUtil.writeFile(filePath, lines);
    }

    private Table rjmReadFromFile(String name)
    {
        ArrayList<String> lines = FileUtil.readFile(PARENT_DIR_PATH + name + fileExtension);
        if (lines == null)
        {
            System.err.println("Could not read table " + name + " from file");
            return null;
        }

        return Table.createTableFromLines(name, lines);
    }

    /**
     * Runs the tests on this class.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        System.out.println("Testing TableFileReadWriter");
        TableFileReadWriter tfrw1 = TableFileReadWriter.getInstance(true);
        tfrw1.test(args);
        TableFileReadWriter tfrw2 = TableFileReadWriter.getInstance(false);
        tfrw2.test(args);
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
        claim(writeToFile(t));
        Table r = readFromFile(t.getName());
        claim(t.equals(r));
        Table n = readFromFile("NotATable");
        claim(n == null);
    }
}
