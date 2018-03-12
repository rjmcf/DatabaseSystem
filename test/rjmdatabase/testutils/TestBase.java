package rjmdatabase.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.Throwable;
import java.lang.StackTraceElement;
import java.lang.Thread;

public abstract class TestBase
{
    protected enum Verbosity
    {
        JUST_ERRORS, JUST_CLASSES, WITH_MESSAGES, ALL
    }

    protected Verbosity verbosity = Verbosity.WITH_MESSAGES;
    /**
     * Runs the tests in this class. Needs to be called from main method in
     * derived classes.
     * @param args Command line arguments.
     */
    public void startTest()
    {
        String className = this.getClass().getSimpleName();
        String classBeingTested = className.replace("Test", "");
        if (verbosity != Verbosity.JUST_ERRORS)
            System.out.println("Testing " + classBeingTested);
        for (Method test : this.getClass().getDeclaredMethods())
        {
            if (!test.isAnnotationPresent(Test.class))
            {
                if (test.getName().startsWith("test"))
                    System.out.println("    Should " + test.getName() + " from have a @Test annotation?");
                continue;
            }

            Annotation annotation = test.getAnnotation(Test.class);
            Test testAnn = (Test) annotation;
            String toPrint = "    " + test.getName() + ": ";
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
                        if (!(exceptionThrown instanceof TestFailedException))
                        {
                            StackTraceElement claimStackTraceElement = exceptionThrown.getStackTrace()[1];
                            String fullClassNameForClaim = claimStackTraceElement.getClassName();
                            String[] packagePathToClass = fullClassNameForClaim.split("\\.");
                            String classNameForClaim = packagePathToClass[packagePathToClass.length - 1];
                            int claimLineNumber = claimStackTraceElement.getLineNumber();
                            toPrint += String.format(", %s threw an exception on line %d", classNameForClaim, claimLineNumber);
                        }
                        toPrint += String.format("\n    %s\n", exceptionThrown.getMessage());
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
    }

    protected void beforeTest() {}
    protected void afterTest() {}

    protected void claim(boolean b, String message)
    {
        int callersLineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
        if (!b) throw new TestFailedException(callersLineNumber, message);
    }
}
