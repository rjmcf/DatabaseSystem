package rjmdatabase.fileutils;

import rjmdatabase.testutils.Test;
import rjmdatabase.testutils.TestBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtilTest extends TestBase
{
    /**
     * Tests this class by writing to a reading from a file and comparing the
     * results.
     * @param args Command line arguments.
     */
     public static void main(String[] args) {
         FileUtilTest tester = new FileUtilTest();
         tester.startTest();
     }

    @Test
    public void testReadingAndWritingFiles()
    {
        String fName = "dbTestFolders/testDir/testFile.txt";
        try
        {
            FileUtil.writeFile(fName, new String[] {"This is the first line", "This is the second line"});
        }
        catch (IOException e)
        {
            claim(false, "IOException when writing.");
        }
        try
        {
            ArrayList<String> lines = FileUtil.readFile(fName);
            claim(lines != null, "readFile returns null.");
            claim(lines.size() == 2, "readFile returns wrong number of lines.");
            claim(lines.get(0).equals("This is the first line"), "First line doesn't match.");
            claim(lines.get(1).equals("This is the second line"), "Second line doesn't match.");
        }
        catch (IOException e)
        {
            claim(false, "IOException when reading.");
        }

        try
        {
            FileUtil.readFile("fakeFile");
            claim(false, "Should not be able to read from fakeFile");
        }
        catch (IllegalArgumentException e) { /* test passed */ }
        catch (IOException e)
        {
            claim(false, "Should not have tried to read fakeFile.");
        }
    }

    @Test
    public void testMakeAndDeleteDirs()
    {
        String topLevel = "fileUtilTest";
        String parentDir = topLevel + "/innerFolder1/innerFolder2";
        String fileName = parentDir + "/test.txt";
        String testLine = "Test Line.";
        File topLevelDir = new File(topLevel);
        File dir = new File(parentDir);
        claim(!topLevelDir.exists(), "Top level folder needs to not exist before start of test.");
        claim(!dir.exists(), "Parent folder needs to not exist before start of test.");

        FileUtil.makeDirsIfNeeded(dir);
        claim(dir.exists(), "Folder should exist after creation.");

        try
        {
            FileUtil.writeFile(fileName, new String[]{testLine});
            claim(FileUtil.readFile(fileName).get(0).equals(testLine), "Incorrect file contents.");
        }
        catch (IOException e)
        {
            claim(false, "IOException thrown while reading or writing to file.");
        }

        FileUtil.deleteDirIfExists(topLevelDir);
        claim(!topLevelDir.exists(), "Folder needs to not exist after deletion.");
    }

    @Test
    public void testDeleteFiles()
    {
        String fileName = "test.txt";
        String testLine = "Test Line.";
        File f = new File(fileName);
        claim(!f.exists(), "File should not exist before creation.");

        try
        {
            FileUtil.writeFile(fileName, new String[]{testLine});
            claim(FileUtil.readFile(fileName).get(0).equals(testLine), "Incorrect file contents.");
        }
        catch (IOException e)
        {
            claim(false, "IOException thrown while reading or writing to file.");
        }

        FileUtil.deleteFileIfExists(f);
        claim(!f.exists(), "File should not exist after deletion.");
    }
}
