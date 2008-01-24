package org.csstudio.sds.ui.internal.properties.view;

/**
 * Listener for changes in objects of type <code>IPropertySheetEntry</code>.
 * <p>
 * This interface is public since it appears in the api of
 * <code>IPropertySheetEntry</code>. It is not intended to be implemented
 * outside of this package.
 * <p>
 * 
 * @author Sven Wende
 */
public interface IPropertySheetEntryListener {
	/**
	 * A node's children have changed (children added or removed).
	 * 
	 * @param node
	 *            the node whose's children have changed
	 */
	void childEntriesChanged(IPropertySheetEntry node);

	/**
	 * A entry's error message has changed.
	 * 
	 * @param entry
	 *            the entry whose's error message has changed
	 */
	void errorMessageChanged(IPropertySheetEntry entry);

	/**
	 * A entry's value has changed.
	 * 
	 * @param entry
	 *            the entry whose's value has changed
	 */
	void valueChanged(IPropertySheetEntry entry);
}
