package fileutils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A utility class with methods to write to and read from text files.
 * @author Rjmcf
 */
public class FileUtil
{
    private final static Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Writes the lines to the file.
     * @param  fName The name of the file to write to.
     * @param  lines The lines to be written.
     * @return       Whether the writing was succesful.
     */
    public static boolean writeFile(String fName, String[] lines) throws IOException
    {
        File file = new File(fName);
        // If this file is in directories that don't exist, make those
        // directories first.
        File parent = file.getParentFile();
        if (parent != null && !parent.exists())
            parent.mkdirs();

        Path path = Paths.get(fName);
        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING))
        {
            for (String line: lines)
            {
                writer.write(line);
                writer.newLine();
            }
            return true;
        }
    }

    /**
     * Reads all the lines in a file.
     * @param  fName The name of the file to be read.
     * @return       The list of lines that have been read.
     */
    public static ArrayList<String> readFile(String fName) throws IOException
    {
        Path path = Paths.get(fName);
        try (Scanner scanner = new Scanner(path, ENCODING.name()))
        {
            ArrayList<String> result = new ArrayList<>();
            while(scanner.hasNextLine())
            {
                result.add(scanner.nextLine());
            }

            scanner.close();

            return result;
        }
    }

    /**
     * Tests this class by writing to a reading from a file and comparing the
     * results.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        System.out.println("Testing FileUtil");
        FileUtil fT = new FileUtil();
        fT.test(args);
        System.out.println("Testing complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test Failed");
    }

    private void test(String args[])
    {
        String fName = "testDir/testFile.txt";
        try
        {
            claim(FileUtil.writeFile(fName, new String[] {"This is the first line", "This is the second line"}));
        }
        catch (IOException e)
        {
            claim(false);
        }
        try
        {
            ArrayList<String> lines = FileUtil.readFile(fName);
            claim(lines != null);
            claim(lines.size() == 2);
            claim(lines.get(0).equals("This is the first line"));
            claim(lines.get(1).equals("This is the second line"));
        }
        catch (IOException e)
        {
            claim(false);
        }

        try
        {
            FileUtil.readFile("fakeFile");
            claim(false);
        }
        catch (IOException e)
        {
            // test passed
        }
    }
}
