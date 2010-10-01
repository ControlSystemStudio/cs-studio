package org.csstudio.alarm.beast.server;

/** Element of the alarm hierarchy
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AlarmHierarchy
{
	/** Name of the alarm */
	final private String name;

	/** RDB ID */
	final private int id;
	
	final private AlarmHierarchy children[];

	public AlarmHierarchy(final String name, final int id)
    {
		this(name, id, new AlarmHierarchy[0]);
    }

	public AlarmHierarchy(final String name, final int id, final AlarmHierarchy children[])
    {
		this.name = name;
		this.id = id;
		this.children = children;
    }
	
    /** @return RDB ID for this alarm tree element */
	public int getID()
    {
	    return id;
    }

    /** Get the alarm tree element's name.
     *  For PVs, this may differ a little from the actual underlying control system PV:
     *  For example, the alarm name might be "Fred",
     *  but since the control system defaults to "system://Fred", the actual
     *  CS PV has a slightly different name.
     *  @return Name that was used when constructing this AlarmPV
     */
    public String getName()
    {
        return name;
    }

	
    public void dump()
    {
		dump("");
    }

	private void dump(final String indent)
    {
		final String child_indent = indent + "    ";
		System.out.println(indent + toString());
		for (AlarmHierarchy child : children)
			child.dump(child_indent);
    }
	
	@Override
	public String toString()
	{
		return name;
	}
}
