package rjmdatabase.fileutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A utility class with methods to write to and read from text files.
 * @author Rjmcf
 */
public class FileUtil
{
    public final static Charset ENCODING = StandardCharsets.UTF_8;

    /**
     * Makes the parent directories of a new file if they don't yet exist.
     * @param fName The path to the file containing any parent directories.
     */
    public static void makeDirsIfNeeded(File directories)
    {
        if (!directories.exists())
            directories.mkdirs();
    }

    /**
     * Writes the lines to the file.
     * @param  fName       The name of the file to write to.
     * @param  lines       The lines to be written.
     * @throws IOException If an io exception occurred.
     */
    public static void writeFile(String fName, String[] lines) throws IOException
    {
        // If this file is in directories that don't exist, make those
        // directories first.
        Path path = Paths.get(fName);
        Path pathToParent = path.getParent();
        if (pathToParent!=null)
            makeDirsIfNeeded(pathToParent.toFile());

        try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING))
        {
            for (String line: lines)
            {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Reads all the lines in a file.
     * @param  fName       The name of the file to be read.
     * @return             The list of lines that have been read.
     * @throws IOException If an io exception occurred.
     */
    public static ArrayList<String> readFile(String fName) throws IOException
    {
        try (FileInputStream fis = new FileInputStream(fName);
             InputStreamReader isr = new InputStreamReader(fis, ENCODING);
             BufferedReader br = new BufferedReader(isr))
        {
            ArrayList<String> result = new ArrayList<>();
            String line = br.readLine();
            while (line != null)
            {
                result.add(line);
                line = br.readLine();
            }

            return result;
        }
        catch (FileNotFoundException e)
        {
            throw new IllegalArgumentException("File " + fName + " not found");
        }
    }

    /**
     * Deletes the specified directory.
     * @param file The name of the directory to be deleted.
     */
    public static void deleteDirIfExists(File file)
    {
        if (!file.exists())
            return;
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirIfExists(f);
            }
        }
        file.delete();
    }

    public static void deleteFileIfExists(File file)
    {
        file.delete();
    }
}
