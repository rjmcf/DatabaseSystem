package tablesrecords;

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
     * @param es The list of Strings to be stored as fields.
     */
    public Record(ArrayList<String> fs)
    {
        fields = fs;
    }

    /**
     * Creates an empty Record.
     */
    public Record()
    {
        fields = new ArrayList<>();
    }

    /**
     * Gets the number of fields stored in a Record.
     * @return The number of fields.
     */
    public int getSize()
    {
        return fields.size();
    }

    /**
     * Gets the value of the field at a certain index. Throws an exception if
     * the index is out of bounds.
     * @param  i The index of the field.
     * @return   The value of the field.
     */
    public String getField(int i)
    {
        if (i<0 || i >= getSize())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        return fields.get(i);
    }

    /**
     * Sets the value of a particular field. Throws an exception if the index is
     * out of bounds.
     * @param i The index of the field.
     * @param r The new value of the field.
     */
    public void setField(int i, String r)
    {
        if (i<0 || i >= getSize())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        fields.set(i, r);
    }

    /**
     * Adds a field to the end of the Record. Shouldn't be used directly on a
     * Record within a table.
     * @param val The field to be added.
     */
    public void addField(String val)
    {
        fields.add(val);
    }

    /**
     * Deletes a particular field from the Record, changing its size. Throws an
     * exception if the index is out of bounds.
     * @param i The index of the field.
     */
    public void deleteField(int i)
    {
        if (i<0 || i >= getSize())
            throw new IndexOutOfBoundsException("No field " + Integer.toString(i) + " exists");

        fields.remove(i);
    }

    /**
     * Gets all the fields as a comma separated list.
     * @return The String containing all the field values.
     */
    public String getAllFields()
    {
        StringJoiner j = new StringJoiner(", ");
        for (String f: fields)
            j.add(f);

        return j.toString();
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
        int size = getSize();
        if (size != thatRecord.getSize()) return false;

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
        claim(getSize() == 0);
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
            setField(0, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        try
        {
            setField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        addField("First");
        claim(getSize() == 1);
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
        claim(getSize()==0);
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
        other.addField("Nope");
        claim(!this.equals(other));

        claim(getAllFields().equals(""));
    }

    private void testFilled(String args[])
    {
        claim(getSize() == 3);
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

        setField(2, "Anything");
        claim(getField(2).equals("Anything"));
        try
        {
            setField(-1, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }
        try
        {
            setField(3, "Anything");
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        addField("Things");
        claim(getSize() == 4);
        claim(getField(3).equals("Things"));

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
            deleteField(4);
            claim(false);
        }
        catch (IndexOutOfBoundsException e)
        {
            // test pass
        }

        deleteField(2);
        claim(getSize() == 3);
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
        other.addField("These");
        other.addField("are");
        claim(!this.equals(other));
        other.addField("things");
        claim(!this.equals(other));
        other.setField(2,"Things");
        claim(this.equals(other));
        other.addField("Wait");
        claim(!this.equals(other));

        claim(getAllFields().equals("These, are, Things"));
    }
}
