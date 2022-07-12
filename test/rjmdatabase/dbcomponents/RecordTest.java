package rjmdatabase.dbcomponents;

import rjmdatabase.testutils.Test;
import rjmdatabase.testutils.TestBase;

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
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyRecord.getField(0);
            claim(false, "Invalid index 0.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledRecord.getField(-1);
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledRecord.getField(3);
            claim(false, "Invalid index 3.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        claim("Field0".equals(filledRecord.getField(0)), "Incorrect Field0 value.");
        claim("Field1".equals(filledRecord.getField(1)), "Incorrect Field1 value.");
        claim("Field2".equals(filledRecord.getField(2)), "Incorrect Field2 value.");
    }

    @Test
    public void testAddField()
    {
        try
        {
            emptyRecord.addField(-1, "Anything");
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyRecord.addField(1, "Anything");
            claim(false, "Invalid index 1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        emptyRecord.addField(0, "Field0");
        claim("Field0".equals(emptyRecord.getField(0)), "Incorrect Field0 value.");

        try
        {
            filledRecord.addField(-1, "Anything");
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledRecord.addField(4, "Anything");
            claim(false, "Invalid index 4.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        filledRecord.addField(3, "Field3");
        claim("Field3".equals(filledRecord.getField(3)), "Incorrect Field3 value.");
        filledRecord.addField(2, "NewField2");
        for (int i = 0; i < 5; i++)
        {
            if (i == 2)
                claim("NewField2".equals(filledRecord.getField(i)), "Incorrect Field2 value.");
            else if (i > 2)
                claim(("Field"+Integer.toString(i-1)).equals(filledRecord.getField(i)), "Incorrect Field"+Integer.toString(i)+" value.");
            else
                claim(("Field"+Integer.toString(i)).equals(filledRecord.getField(i)), "Incorrect Field"+Integer.toString(i)+" value.");
        }
    }

    @Test
    public void testUpdateField()
    {
        try
        {
            emptyRecord.updateField(-1, "Anything");
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyRecord.updateField(0, "Anything");
            claim(false, "Invalid index 0.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        try
        {
            filledRecord.updateField(-1, "Anything");
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledRecord.updateField(3, "Anything");
            claim(false, "Invalid index 3.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        filledRecord.updateField(1, "NewField1");
        for (int i = 0; i < 3; i++)
        {
            String prefix = i == 1 ? "New" : "";
            claim((prefix+"Field"+Integer.toString(i)).equals(filledRecord.getField(i)), "Incorrect Field"+Integer.toString(i)+" value.");
        }
    }

    @Test
    public void testDeleteField()
    {
        try
        {
            emptyRecord.deleteField(-1);
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            emptyRecord.deleteField(0);
            claim(false, "Invalid index 0.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }

        try
        {
            filledRecord.deleteField(-1);
            claim(false, "Invalid index -1.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        try
        {
            filledRecord.deleteField(3);
            claim(false, "Invalid index 3.");
        }
        catch (IndexOutOfBoundsException e) { /* test passed */ }
        filledRecord.deleteField(1);
        claim("Field0".equals(filledRecord.getField(0)), "Incorrect Field0 value.");
        claim("Field2".equals(filledRecord.getField(1)), "Incorrect Field1 value.");
    }

    @Test
    public void testEquals()
    {
        claim(!emptyRecord.equals("Not a Record"), "Record != String.");
        claim(emptyRecord.equals(emptyRecord), "Same references should be equal.");
        Record otherRecord = new Record();
        claim(emptyRecord.equals(otherRecord), "Empty Records should be equal.");
        otherRecord.addField(0, "A Field");
        claim(!emptyRecord.equals(otherRecord), "Too many fields to be equal.");

        claim(!filledRecord.equals("Not a Record"), "Record != String.");
        claim(filledRecord.equals(filledRecord), "Same references should be equal.");
        ArrayList<String> entries = new ArrayList<String>();
        entries.add("Field0");
        entries.add("Field1");
        otherRecord = new Record(entries);
        claim(!filledRecord.equals(otherRecord), "Too few fields to be equal.");
        otherRecord.addField(2, "Field2");
        claim(filledRecord.equals(otherRecord), "Same fields should be equal.");
        otherRecord.updateField(0, "NotField0");
        claim(!filledRecord.equals(otherRecord), "Different fields should not be equal.");
        otherRecord.updateField(0, "Field0");
        otherRecord.addField(3, "Field3");
        claim(!filledRecord.equals(otherRecord), "Too many fields to be equal.");
        otherRecord.deleteField(3);
        claim(filledRecord.equals(otherRecord), "Same fields should be equal.");
    }
}
