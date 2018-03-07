package exceptions;

public class TestsElsewhereException extends RuntimeException
{
    public TestsElsewhereException(String className, String otherClassName)
    {
        super("The tests for " + className + " are found in " + otherClassName +
                ". Make sure to run the tests for that class.");
    }
}
