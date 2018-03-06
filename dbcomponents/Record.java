package dbcomponents;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.lang.IndexOutOfBoundsException;

/**
 * Represents a Record. Implements Serializable in order to allow saving to a
 * file by serialization.
 * @author Rjmcf
 */
public class Record implements java.io.Serializable
{
    // Fields are stored as an array list to make adding new fields much easier.
    private ArrayList<String> fields;

    /**
     * Creates a Record storing the fields as Strings.
     * @param fs The list of Strings to be stored as fields.
     */
    Record(ArrayList<String> fs)
    {
        fields = fs;
    }

    /**
     * Creates an empty Record.
     */
    Record()
    {
        fields = new ArrayList<>();
    }

    /**
     * Gets the value of the field at a certain index. Throws an exception if
     * the index is out of bounds.
     * @param  i The index of the field.
     * @return   The value of the field.
     */
    String getField(int i)
    {
        if (i<0 || i >= fields.size())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        return fields.get(i);
    }

    /**
     * Updates the value of a particular field. Throws an exception if the index is
     * out of bounds.
     * @param i The index of the field.
     * @param r The new value of the field.
     */
    void updateField(int i, String r)
    {
        if (i<0 || i >= fields.size())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        fields.set(i, r);
    }

    /**
     * Adds a field to a Record. Throws an exception if the index is out of bounds.
     * @param i   The index at which to add the new field.
     * @param val The field to be added.
     */
    void addField(int i, String val)
    {
        if (i<0 || i > fields.size())
            throw new IndexOutOfBoundsException("Cannot add field at index " + Integer.toString(i));
        fields.add(i, val);
    }

    /**
     * Deletes a particular field from the Record, changing its size. Throws an
     * exception if the index is out of bounds.
     * @param i The index of the field.
     */
    void deleteField(int i)
    {
        if (i<0 || i >= fields.size())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        fields.remove(i);
    }

    /**
     * Overrides Object.equals(Object that) to allow Records to be compared.
     * @param  that The Record being compared with.
     * @return      Whether or not the Records are equal.
     */
    @Override
    public boolean equals(Object that)
    {
        if (this == that) return true;
        if (!(that instanceof Record)) return false;
        Record thatRecord = (Record)that;
        int size = fields.size();
        if (size != thatRecord.fields.size()) return false;

        // The Records are only equal if all their fields are the same.
        for (int i = 0; i < size; i++)
            if (!getField(i).equals(thatRecord.getField(i)))
                return false;

        return true;
    }

    /**
     * Runs tests on the Record class.
     * @param args Command line arguments.
     */
    public static void main(String[] args)
    {
        System.out.println("Testing Record");
        System.out.println("    Testing Empty");
        Record r1 = new Record();
        r1.testEmpty(args);

        System.out.println("    Testing Filled");
        ArrayList<String> entries = new ArrayList<String>();
        entries.add("These");
        entries.add("are");
        entries.add("entries");
        Record r2 = new Record(entries);
        r2.testFilled(args);

        System.out.println("Testing complete");
    }

    private void claim(boolean b)
    {
        if (!b) throw new Error("Test failure");
    }

    private void testEmpty(String args[])
    {
        claim(fields.size() == 0);
        try
        {
            getField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            updateField(0, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            addField(1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        addField(0, "First");
        claim(fields.size() == 1);
        claim(getField(0).equals("First"));

        try
        {
            deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            deleteField(1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        deleteField(0);
        claim(fields.size()==0);
        try
        {
            getField(0);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        Record other = new Record();
        claim(this.equals(this));
        claim(this.equals(other));
        other.addField(0, "Nope");
        claim(!this.equals(other));
    }

    private void testFilled(String args[])
    {
        claim(fields.size() == 3);
        claim(getField(0).equals("These"));
        claim(getField(1).equals("are"));
        claim(getField(2).equals("entries"));
        try
        {
            getField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            getField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        updateField(2, "Anything");
        claim(getField(2).equals("Anything"));
        try
        {
            updateField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            updateField(3, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            addField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            addField(4, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        addField(3, "Things");
        claim(fields.size() == 4);
        claim(getField(3).equals("Things"));
        addField(2, "all");
        claim(fields.size() == 5);
        claim(getField(2).equals("all"));
        claim(getField(3).equals("Anything"));

        try
        {
            deleteField(-1);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            deleteField(5);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        deleteField(2);
        deleteField(2);
        claim(fields.size() == 3);
        claim(getField(0).equals("These"));
        claim(getField(1).equals("are"));
        claim(getField(2).equals("Things"));
        try
        {
            getField(3);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        claim(!this.equals(null));
        String s = new String();
        claim(!this.equals(s));
        Record other = new Record();
        claim(this.equals(this));
        claim(!this.equals(other));
        other.addField(0, "These");
        other.addField(1, "are");
        claim(!this.equals(other));
        other.addField(2, "things");
        claim(!this.equals(other));
        other.updateField(2,"Things");
        claim(this.equals(other));
        other.addField(3, "Wait");
        claim(!this.equals(other));
    }
}
