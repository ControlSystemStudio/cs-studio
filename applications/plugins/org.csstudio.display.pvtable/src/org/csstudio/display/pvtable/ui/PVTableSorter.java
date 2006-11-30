package org.csstudio.display.pvtable.ui;

import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVValue;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/** A sorter for <class>PV</class> entries in a JFace <class>TableView</class>.
 *  It can sort by name (default), connection state or value.
 *  <p>
 *  Currently not used.
 *  @author Kay Kasemir
 */
public class PVTableSorter extends ViewerSorter
{
	private int sort_column;
	public static final int CONNECTION = 0;
	public static final int NAME = 1;
	public static final int VALUE = 3;

	public PVTableSorter()
	{
		sort_column = NAME;
	}

	/** Select sorting by name or connection state. */
	public void setSortColumn(int sort_column)
	{
		this.sort_column = sort_column;
	}
	
	/** All PVs are in the same category... */
	@Override
	public int category(Object element)
	{
		return 0;
	}

	/** Compare the PVs by name and/or connection state. */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2)
	{
		if (!(e1 instanceof PV && e2 instanceof PV))
			return 0;

		PV pv1 = (PV) e1;
		PV pv2 = (PV) e2;
		// System.out.println("Compare: '" + pv1.getName() + "', '" + pv2.getName() + "'");

		int cmp;
		if (sort_column == NAME)
		{ // Sort by name, then connection state
			cmp = compareNames(pv1, pv2);
			if (cmp != 0) // Done.
				return cmp;
			return compareConnection(pv1, pv2);
		}
		if (sort_column == VALUE)
		{ // Sort by value, then name
			cmp = compareValues(pv1, pv2);
			if (cmp != 0) // Done.
				return cmp;
			return compareNames(pv1, pv2);
		}
		// Sort by connection state, then name
		cmp = compareConnection(pv1, pv2);
		if (cmp != 0) // Done.
			return cmp;
		return compareNames(pv1, pv2);
	}

	private int compareNames(PV pv1, PV pv2)
	{
		return pv1.getName().compareTo(pv2.getName());
	}

	// Return -1, 0, 1.
	// Example: v1 > v2 --> positive
	private int compareValues(PV pv1, PV pv2)
	{
		Object v1 = pv1.getValue();
		Object v2 = pv2.getValue();
		// Handle null
		if (v1 == null)
			return (v2 == null) ? 0 : -1;
		if (v2 == null)
			return 1;
		// If one string is involved, compare as strings.
		if (v1 instanceof String  ||  v2 instanceof String)
			return v1.toString().compareTo(v2.toString());
		try
		{
			double diff = PVValue.toDouble(v1) - PVValue.toDouble(v2);
			if (diff > 0.0)
				return 1;
			return -1;
		}
		catch (Exception e) {}
		// Default: same number or cannot parse number...
		return 0;
	}	

	private int compareConnection(PV pv1, PV pv2)
	{
		int c1 = pv1.isConnected() ? 1 : 0;
		int c2 = pv2.isConnected() ? 1 : 0;
		return c1 - c2;
	}
}
