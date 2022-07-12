package rjmdatabase.testutils;

import java.io.File;
import java.util.ArrayList;

/**
 * Runs all the tests for all the classes in this project.
 * @author Rjmcf
 */
public class TestRunner
{
    private static final String[] ignorePackages = new String[]{"testutils"};
    /**
     * Entry point to the TestRunner. Simply runs all the tests.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        // If we have passed an argument, it's beacuse we don't want the interactivity.
        runAllTests(args.length == 0);
    }

    private static void runAllTests(boolean useInteractivity)
    {
        String topLevelTestDirName = "rjmdatabase";
        File topLevelTestDir = new File(topLevelTestDirName);

        int numFailed = 0;
        for (String fName : getAllTestClassFileNames(topLevelTestDirName))
        {
            try
            {
                Object o = Class.forName(fName).newInstance();
                if (!(o instanceof TestBase))
                    throw new Error(String.format("Test %s does not inherit TestBase.", fName));

                String testClassName = fName.replaceAll("(\\w)+\\.", "");
                String classBeingTested = testClassName.replace("Test", "");
                TestBase test = (TestBase)o;
                numFailed += test.startTest(testClassName, classBeingTested, useInteractivity);
            }
            catch (ClassNotFoundException e)
            {
                throw new Error(fName + " class was not found.");
            }
            catch (InstantiationException e)
            {
                throw new Error(fName + " class could not be instantiated.");
            }
            catch (IllegalAccessException e)
            {
                throw new Error(fName + " class could not be accessed.");
            }
        }

        if (numFailed > 0)
            throw new Error(String.format("%d tests failed.", numFailed));
        else
            System.out.println("All tests passed.");
    }

    private static ArrayList<String> getAllTestClassFileNames(String dirName)
    {
        ArrayList<String> result = new ArrayList<>();
        for (String pck : ignorePackages)
            if (dirName.contains(pck))
                return result;

        String packageName = dirName.replaceAll("/", ".");
        File dir = new File(dirName);
        for (File f : dir.listFiles())
        {
            if (f.isDirectory())
            {
                result.addAll(getAllTestClassFileNames(dirName + "/" + f.getName()));
            }
            else
            {
                if (f.getName().endsWith("Test.class") && !f.getName().contains("$"))
                {
                    String fName = f.getName();
                    String nameNoExtension = fName.substring(0,fName.length()-6);
                    result.add(packageName + "." + nameNoExtension);
                }
            }
        }

        return result;
    }
}
