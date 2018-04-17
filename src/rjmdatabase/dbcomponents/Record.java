package rjmdatabase.dbcomponents;

import java.util.ArrayList;
import java.util.StringJoiner;

/**
 * Represents a Record.
 * @author Rjmcf
 */
public class Record
{
    // Fields are stored as an array list to make adding new fields easier.
    private ArrayList<String> fields;

    /**
     * Creates a Record storing the fields as Strings.
     * @param fs The list of Strings to be stored as fields.
     */
    Record(ArrayList<String> fs)
    {
        fields = new ArrayList<>(fs);
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
            throw new IndexOutOfBoundsException(String.format("No field %d exists", i));

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
            throw new IndexOutOfBoundsException(String.format("No field %d exists", i));

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
            throw new IndexOutOfBoundsException(String.format("Cannot add field at index %d", i));
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
            throw new IndexOutOfBoundsException(String.format("No field %d exists", i));

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
        ArrayList<String> otherRecordsFields = thatRecord.fields;
        if (size != otherRecordsFields.size()) return false;

        // The Records are only equal if all their fields are the same.
        for (int i = 0; i < size; i++)
        {
            String thatField = thatRecord.getField(i);
            if (!getField(i).equals(thatField))
                return false;
        }

        return true;
    }
}
