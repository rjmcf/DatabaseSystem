package fileutils;

import dbcomponents.Table;

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
    private static TableFileReadWriter instance;

    // The folder in which all Tables are saved.
    private String parentDirPath;
    // The file extension with which table files are saved.
    private String fileExtension;
    // Determines which method of reading and writing files to be used.
    private boolean useSerialization;

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
     * @param  t The Table to write.
     * @return   Whether the writing was successful.
     */
    public boolean writeToFile(Table t) throws IOException
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
    public Table readFromFile(String name) throws IOException
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
            FileOutputStream fileOut = new FileOutputStream(parentDirPath + t.getName() + fileExtension);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            // This requires both Table and Record to implement Serializable.
            out.writeObject(t);
            out.close();
            fileOut.close();
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
        catch (IOException i)
        {
            throw new Error("Table " + tableName + " could not be read from file");
        }
        catch (ClassNotFoundException c)
        {
            throw new Error("Table class not found");
        }
    }

    private boolean rjmWriteToFile(Table t) throws IOException
    {
        String[] lines = t.prepareLinesForWriting();
        String filePath = parentDirPath + t.getName() + fileExtension;
        return FileUtil.writeFile(filePath, lines);

    }

    private Table rjmReadFromFile(String name) throws IOException
    {
        ArrayList<String> lines = FileUtil.readFile(parentDirPath + name + fileExtension);
        return Table.createTableFromLines(name, lines);
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
            claim(writeToFile(t));
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
        catch (IOException e)
        {
            // test passed
        }
        catch (IllegalArgumentException i)
        {
            // test passed
        }
    }
}
