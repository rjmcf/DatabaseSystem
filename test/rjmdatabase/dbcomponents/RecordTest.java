package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.TestBase;
import rjmdatabase.testutils.Test;
import rjmdatabase.dbcomponents.Record;
import java.util.ArrayList;

public class RecordTest extends TestBase
{
    static Record emptyRecord;
    static Record filledRecord;

    /**
     * Runs tests on the Record class.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
         RecordTest tester = new RecordTest();
         tester.startTest();
    }

    @Override
    public void beforeTest()
    {
        emptyRecord = new Record();
        ArrayList<String> entries = new ArrayList<String>();
        entries.add("Field0");
        entries.add("Field1");
        entries.add("Field2");
        filledRecord = new Record(entries);
    }

    @Test
    public void testGetField()
    {
        try
        {
            emptyRecord.getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            emptyRecord.getField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }
        try
        {
            filledRecord.getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            filledRecord.getField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test passed
        }

        claim("Field0".equals(filledRecord.getField(0)));
        claim("Field1".equals(filledRecord.getField(1)));
        claim("Field2".equals(filledRecord.getField(2)));
    }

    @Test
    public void testAddField()
    {
        try
        {
            emptyRecord.addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            emptyRecord.addField(1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        emptyRecord.addField(0, "Field0");
        claim("Field0".equals(emptyRecord.getField(0)));

        try
        {
            filledRecord.addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            filledRecord.addField(4, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        filledRecord.addField(3, "Field3");
        claim("Field3".equals(filledRecord.getField(3)));
        filledRecord.addField(2, "NewField2");
        for (int i = 0; i < 5; i++)
        {
            if (i == 2)
                claim("NewField2".equals(filledRecord.getField(i)));
            else if (i > 2)
                claim(("Field"+Integer.toString(i-1)).equals(filledRecord.getField(i)));
            else
                claim(("Field"+Integer.toString(i)).equals(filledRecord.getField(i)));
        }
    }

    @Test
    public void testUpdateField()
    {
        try
        {
            emptyRecord.updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            emptyRecord.updateField(0, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            filledRecord.updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            filledRecord.updateField(3, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        filledRecord.updateField(1, "NewField1");
        for (int i = 0; i < 3; i++)
        {
            String prefix = i == 1 ? "New" : "";
            claim((prefix+"Field"+Integer.toString(i)).equals(filledRecord.getField(i)));
        }
    }

    @Test
    public void testDeleteField()
    {
        try
        {
            emptyRecord.deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            emptyRecord.deleteField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            filledRecord.deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            filledRecord.deleteField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        filledRecord.deleteField(1);
        claim("Field0".equals(filledRecord.getField(0)));
        claim("Field2".equals(filledRecord.getField(1)));
    }

    @Test
    public void testEquals()
    {
        claim(!emptyRecord.equals("Not a Record"));
        claim(emptyRecord.equals(emptyRecord));
        Record otherRecord = new Record();
        claim(emptyRecord.equals(otherRecord));
        otherRecord.addField(0, "A Field");
        claim(!emptyRecord.equals(otherRecord));

        claim(!filledRecord.equals("Not a Record"));
        claim(filledRecord.equals(filledRecord));
        ArrayList<String> entries = new ArrayList<String>();
        entries.add("Field0");
        entries.add("Field1");
        otherRecord = new Record(entries);
        claim(!filledRecord.equals(otherRecord));
        otherRecord.addField(2, "Field2");
        claim(filledRecord.equals(otherRecord));
        otherRecord.updateField(0, "NotField0");
        claim(!filledRecord.equals(otherRecord));
        otherRecord.updateField(0, "Field0");
        otherRecord.addField(3, "Field3");
        claim(!filledRecord.equals(otherRecord));
        otherRecord.deleteField(3);
        claim(filledRecord.equals(otherRecord));
    }
}
