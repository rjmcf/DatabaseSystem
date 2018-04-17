package rjmdatabase.testutils;

import static rjmdatabase.userinterface.ConsoleInterfaceUtil.*;

class ExceptionExplorer
{
    public static void interactWithException(Throwable exceptionThrown, String className, String methodName)
    {
        println("## ERROR");
        println(String.format("An exception was thrown from %s in %s:", methodName, className));
        println(exceptionThrown.toString());

        if (!userRespondedYes("Do you want to deal with it now?"))
        {
            println("## END OF ERROR");
            return;
        }

        int stackDepth = 0;
        while (true)
        {
            try
            {
                StackTraceElement stackTraceElement = exceptionThrown.getStackTrace()[stackDepth++];
                String elementMethodName = stackTraceElement.getMethodName();
                String fullElementClassName = stackTraceElement.getClassName();
                String[] packagePathToClass = fullElementClassName.split("\\.");
                String elementClassName = packagePathToClass[packagePathToClass.length - 1];
                int lineNumber = stackTraceElement.getLineNumber();
                println(String.format("%s threw an exception on line %d in %s", elementClassName, lineNumber, elementMethodName));
                if (!userRespondedYes("Do you want to see more?"))
                    break;
            }
            catch (IndexOutOfBoundsException e)
            {
                println("No more stack trace elements.");
                break;
            }
        }

        println("## END OF ERROR");
    }
}
