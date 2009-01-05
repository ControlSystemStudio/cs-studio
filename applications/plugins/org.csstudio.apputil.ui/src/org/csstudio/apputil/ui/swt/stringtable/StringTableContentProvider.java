package org.csstudio.apputil.ui.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Content provider for list of strings.
 *  <p>
 *  Will provide Integer index values which allow the label provider
 *  and editor to access the correct elements in the string list.
 * @author Kay Kasemir, Xihui Chen
 *
 */
public class StringTableContentProvider implements IStructuredContentProvider
{
	/** Magic number for the final 'add' element */
	final public static Integer ADD_ELEMENT = new Integer(-1);
	private List<?> items;

	/** {@inheritDoc} */
	public void inputChanged(final Viewer viewer, final Object old, final Object new_input)
	{
		items = (List<?>) new_input;
	}

	/** {@inheritDoc} */
	public Object[] getElements(Object arg0)
	{
		int N = items.size();
		final Integer result[] = new Integer[N+1];
		for (int i=0; i<N; ++i)
			result[i] = i;
		result[N] = ADD_ELEMENT;
		return result;
	}

	/** {@inheritDoc} */
	public void dispose()
	{
		// NOP
	}
}
