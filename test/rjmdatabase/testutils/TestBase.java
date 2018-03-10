package rjmdatabase.testutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

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
            boolean wasError = false;

            if (testAnn.testEnabled())
            {
                beforeTest();
                try { test.invoke(this); }
                catch (IllegalAccessException iAccessE)
                {
                    toPrint += "Must be made public";
                    wasError = true;
                }
                catch (IllegalArgumentException iArgE)
                {
                    toPrint += "Must take no arguments";
                    wasError = true;
                }
                catch (InvocationTargetException ite)
                {
                    toPrint += "Failed";
                    if (verbosity == Verbosity.WITH_MESSAGES || verbosity == Verbosity.ALL)
                        toPrint += "\n" + ite.getCause().getMessage();
                    wasError = true;
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
        if (!b) throw new Error("Test failure: " + message);
    }
}
