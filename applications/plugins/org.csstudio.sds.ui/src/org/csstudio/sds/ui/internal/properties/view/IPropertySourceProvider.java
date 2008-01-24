package org.csstudio.sds.ui.internal.properties.view;

/**
 * Interface used by
 * {@link org.csstudio.sds.ui.internal.properties.view.PropertySheetEntry} to
 * obtain an
 * {@link org.csstudio.sds.ui.internal.properties.view.IPropertySource} for a
 * given object.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 * 
 * @author Sven Wende
 */
public interface IPropertySourceProvider {

	/**
	 * Returns a property source for the given object.
	 * 
	 * @param object
	 *            the object
	 * @return the property source for the object passed in (maybe
	 *         <code>null</code>)
	 */
	IPropertySource getPropertySource(Object object);
}
