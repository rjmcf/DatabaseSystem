package rjmdatabase.testutils;

import rjmdatabase.fileutils.FileUtil;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

public class PrintStreamFileWriter extends java.io.PrintStream
{
    private ArrayList<String> lines = new ArrayList<>();
    private String dummyFileName = "test.txt";
    private String fileName;

    public PrintStreamFileWriter(String fN) throws FileNotFoundException
    {
        super("test.txt");
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
