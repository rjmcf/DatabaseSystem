package rjmdatabase.testutils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

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
        // If we have passed an argument, it's because we don't want the interactivity.
        runAllTests(args.length == 0);
    }

    private static void runAllTests(boolean useInteractivity)
    {
        String topLevelTestDirName = "";
        String packageName = "rjmdatabase";

        int numTestClasses = 0;
        int totalTestsPassed = 0;
        int totalTestsFailed = 0;

        for (String fName : getAllTestClassFileNames(topLevelTestDirName, packageName))
        {
            try
            {
                Object o = Class.forName(fName).getDeclaredConstructor().newInstance();
                if (!(o instanceof TestBase))
                    throw new Error(String.format("Test %s does not inherit TestBase.", fName));

                String testClassName = fName.replaceAll("(\\w)+\\.", "");
                String classBeingTested = testClassName.replace("Test", "");
                TestBase test = (TestBase)o;
                TestResults results = test.startTest(testClassName, classBeingTested, useInteractivity);

                numTestClasses++;
                totalTestsPassed += results.numPassed;
                totalTestsFailed += results.numFailed;
            }
            catch (ClassNotFoundException e)
            {
                throw new Error(fName + " class was not found.");
            }
            catch (InstantiationException e)
            {
                throw new Error(fName + " class could not be instantiated.");
            }
            catch (NoSuchMethodException e)
            {
                throw new Error(fName + " class could not be instantiated since there is no default constructor.");
            }
            catch (IllegalAccessException e)
            {
                throw new Error(fName + " class could not be accessed.");
            }
            catch (InvocationTargetException e)
            {
                throw new Error(fName + " class' constructor threw an exception.");
            }
        }

        System.out.println(String.format("Testing Complete: %d test classes run", numTestClasses));
        if (totalTestsFailed > 0)
        {
            System.out.println(String.format("%d tests passed", totalTestsPassed));
            throw new Error(String.format("%d tests failed", totalTestsFailed));
        }
        else if (totalTestsPassed > 0)
        {
            System.out.println(String.format("All %d tests passed", totalTestsPassed));
        }
    }

    private static ArrayList<String> getAllTestClassFileNames(String dirName, String packageName)
    {
        // TODO consider how to make this work in all environments
        String fullDirName = dirName + packageName.replace(".", "/");

        ArrayList<String> result = new ArrayList<>();
        for (String pck : ignorePackages)
            if (fullDirName.contains(pck))
                return result;

        File dir = new File(fullDirName);
        if (!dir.exists())
        {
            throw new Error(String.format("Directory '%s' does not exist", dir.getAbsolutePath()));
        }

        for (File f : Objects.requireNonNull(dir.listFiles()))
        {
            if (f.isDirectory())
            {
                result.addAll(getAllTestClassFileNames(dirName, String.format("%s.%s", packageName, f.getName())));
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
