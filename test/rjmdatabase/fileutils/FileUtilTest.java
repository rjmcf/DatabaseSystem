package rjmdatabase.fileutils;

import java.io.IOException;
import java.util.ArrayList;

public class FileUtilTest extends rjmdatabase.TestBase
{
    /**
     * Tests this class by writing to a reading from a file and comparing the
     * results.
     * @param args Command line arguments.
     */
     public static void main(String[] args) {
         FileUtilTest tester = new FileUtilTest();
         tester.startTest(args);
     }

    @Override
    protected void test(String[] args)
    {
        String fName = "dbTestFolders/testDir/testFile.txt";
        try
        {
            FileUtil.writeFile(fName, new String[] {"This is the first line", "This is the second line"});
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
        catch (IllegalArgumentException e)
        {
            // test passed
        }
        catch (IOException e)
        {
            // If it's not an IllegalArgumentException, it means it tried to
            // read from a file that doesn't exist!
            claim(false);
        }
    }
}
