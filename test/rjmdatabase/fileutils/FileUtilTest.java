package rjmdatabase.fileutils;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
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
    public void test()
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
        catch (IllegalArgumentException e)
        { /* test passed */ }
        catch (IOException e)
        {
            claim(false, "Should not have tried to read fakeFile.");
        }

        claim(false, "Make more tests!");
    }
}
