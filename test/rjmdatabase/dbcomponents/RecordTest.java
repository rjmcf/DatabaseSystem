package rjmdatabase.dbcomponents;

import rjmdatabase.dbcomponents.Record;
import java.util.ArrayList;

public class RecordTest extends rjmdatabase.TestBase
{
    /**
     * Runs tests on the Record class.
     * @param args Command line arguments.
     */
     public static void main(String[] args) {
         RecordTest tester = new RecordTest();
         tester.startTest(args);
     }

    @Override
    protected  void test(String[] args)
    {
        Record r1 = new Record();
        testEmpty(r1);
        ArrayList<String> entries = new ArrayList<String>();
        entries.add("These");
        entries.add("are");
        entries.add("entries");
        Record r2 = new Record(entries);
        testFilled(r2);
    }

    private void testEmpty(Record record)
    {
        try
        {
            record.getField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            record.updateField(0, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            record.addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.addField(1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        record.addField(0, "First");
        claim(record.getField(0).equals("First"));
        try
        {
            record.getField(1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }

        try
        {
            record.deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.deleteField(1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        record.deleteField(0);
        try
        {
            record.getField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        Record other = new Record();
        claim(record.equals(record));
        claim(record.equals(other));
        other.addField(0, "Nope");
        claim(!record.equals(other));
    }

    private void testFilled(Record record)
    {
        claim(record.getField(0).equals("These"));
        claim(record.getField(1).equals("are"));
        claim(record.getField(2).equals("entries"));
        try
        {
            record.getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.getField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        record.updateField(2, "Anything");
        claim(record.getField(2).equals("Anything"));
        try
        {
            record.updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.updateField(3, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            record.addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.addField(4, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        record.addField(3, "Things");
        claim(record.getField(3).equals("Things"));
        record.addField(2, "all");
        claim(record.getField(2).equals("all"));
        claim(record.getField(3).equals("Anything"));

        try
        {
            record.deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            record.deleteField(5);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        record.deleteField(2);
        record.deleteField(2);
        claim(record.getField(0).equals("These"));
        claim(record.getField(1).equals("are"));
        claim(record.getField(2).equals("Things"));
        try
        {
            record.getField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        claim(!record.equals(null));
        String s = new String();
        claim(!record.equals(s));
        Record other = new Record();
        claim(record.equals(record));
        claim(!record.equals(other));
        other.addField(0, "These");
        other.addField(1, "are");
        claim(!record.equals(other));
        other.addField(2, "things");
        claim(!record.equals(other));
        other.updateField(2,"Things");
        claim(record.equals(other));
        other.addField(3, "Wait");
        claim(!record.equals(other));
    }
}
