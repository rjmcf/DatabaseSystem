package rjmdatabase;

public abstract class TestBase
{
    /**
     * Runs the tests in this class. Needs to be called from main method in
     * derived classes.
     * @param args Command line arguments.
     */
    public void startTest(String[] args)
    {
        String className = this.getClass().getSimpleName();
        String classBeingTested = className.replace("Test", "");
        System.out.println("Testing " + classBeingTested);
        test(args);
        System.out.println("Testing " + classBeingTested + " completed");
    }

    protected void claim(boolean b)
    {
        if (!b) throw new Error("Test failure");
    }

    protected abstract void test(String[] args);
}
