package org.csstudio.diag.pvfields;


public class PVField
{
    final private String name;
    final private String original_value;
    private String current_value = "<disconnected>";
    
    public PVField(String name, String original_value)
    {
        this.name = name;
        this.original_value = original_value;
    }

    public String getName()
    {
    	return name;
    }

    public String getOriginalValue()
    {
    	return original_value;
    }
    
    public String getCurrentValue()
    {
		return current_value;
	}

	@Override
    public String toString()
    {
        return String.format(
                "%s: original_value=%s, current_value=%s",
                name, original_value, current_value);
    }
}
