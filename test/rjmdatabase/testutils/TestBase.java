package rjmdatabase.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class TestBase
{
    protected enum Verbosity
    {
        JUST_ERRORS, JUST_CLASSES, WITH_MESSAGES, ALL
    }

    protected Verbosity verbosity = Verbosity.ALL;

    /**
     * Runs the tests in this class.
     * @return The number of tests that were failed.
     */
    public int startTest()
    {
        String className = this.getClass().getSimpleName();
        String classBeingTested = className.replace("Test", "");
        return startTest(className, classBeingTested, true);
    }

    /**
     * Runs the tests in this class.
     * @param  classBeingTested The name of the class being tested.
     * @return                  The number of tests that were failed.
     */
    public int startTest(String className, String classBeingTested, boolean useInteractivity)
    {
        int numFailed = 0;
        if (verbosity != Verbosity.JUST_ERRORS)
            System.out.println("Testing " + classBeingTested);
        for (Method test : this.getClass().getDeclaredMethods())
        {
            if (!test.isAnnotationPresent(Test.class))
            {
                if (test.getName().startsWith("test"))
                    System.out.println(String.format("    Should %s from %s have a @Test annotation?", test.getName(), className));
                continue;
            }

            Annotation annotation = test.getAnnotation(Test.class);
            Test testAnn = (Test) annotation;
            String toPrint = String.format("    %s: ", test.getName());
            boolean wasError = true;

            if (testAnn.testEnabled())
            {
                beforeTest();
                try { test.invoke(this); wasError = false;}
                catch (IllegalAccessException iAccessE)
                {
                    toPrint += "Must be made public";
                }
                catch (IllegalArgumentException iArgE)
                {
                    toPrint += "Must take no arguments";
                }
                catch (InvocationTargetException ite)
                {
                    toPrint += "Failed";

                    if (verbosity == Verbosity.WITH_MESSAGES || verbosity == Verbosity.ALL)
                    {
                        Throwable exceptionThrown = ite.getCause();
                        if (exceptionThrown instanceof TestFailedException)
                        {
                            toPrint += String.format("\n    %s\n", exceptionThrown.getMessage());
                        }
                        else if (useInteractivity)
                            ExceptionExplorer.interactWithException(exceptionThrown, className, test.getName());
                        else
                            toPrint += ", an exception was thrown.";
                    }
                }
                afterTest();
            }
            else
            {
                toPrint += "Disabled";
            }

            if (wasError)
            {
                numFailed += 1;
                if (verbosity == Verbosity.JUST_ERRORS)
                    System.out.println(toPrint += " in " + className);
                else
                    System.out.println(toPrint);
            }
            else
            {
                if (verbosity == Verbosity.ALL)
                    System.out.println(toPrint + (toPrint.endsWith("Disabled") ? "" : "Succeeded"));
            }
        }
        if (verbosity != Verbosity.JUST_ERRORS)
            System.out.println("Testing " + classBeingTested + " completed");
        return numFailed;
    }

    protected void beforeTest() {}
    protected void afterTest() {}

    protected void claim(boolean b, String message)
    {
        // sT[0] is this line, sT[1] is this function, sT[2] is where it was called from.
        int callersLineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (!b) throw new TestFailedException(callersLineNumber, message);
    }
}
