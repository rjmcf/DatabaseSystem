package rjmdatabase.testutils;

import rjmdatabase.fileutils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This redirects all calls to a PrintStream to use my own file writing methods.
 * This is useful when testing TablePrinter, as normally PrintStreams don't let
 * you select a character encoding.
 * @author Rjmcf
 */
public class PrintStreamFileWriter extends java.io.PrintStream
{
    private static String dummyFileName;
    private ArrayList<String> lines = new ArrayList<>();
    private String fileName;

    static
    {
        // This enables me to use dummyFileName in the constructor.
        dummyFileName = "test.txt";
    }

    public PrintStreamFileWriter(String fN) throws FileNotFoundException
    {
        super(dummyFileName);
        fileName = fN;
    }

    @Override
    public void println() { lines.add(""); }

    @Override
    public void println(String s) { lines.add(s); }

    @Override
    public void close()
    {
        super.close();
        FileUtil.deleteFileIfExists(new File(dummyFileName));
        try
        {
            FileUtil.writeFile(fileName, lines.toArray(new String[0]));
        }
        catch (IOException e)
        {
            throw new Error("IOException thrown while writing to file.");
        }
    }
}
