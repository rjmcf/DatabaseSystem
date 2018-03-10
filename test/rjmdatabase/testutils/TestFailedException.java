package rjmdatabase.testutils;

class TestFailedException extends RuntimeException
{
    public TestFailedException(int testLineNumber, String message)
    {
        super(String.format(String.format("line %d says: %s", testLineNumber, message)));
    }
}
