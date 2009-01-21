package org.csstudio.dct.ui.editor;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.eclipse.gef.commands.Command;

/**
 * Table adapter that allows for an easy adaption of an arbitrary property of a
 * domain object.
 * 
 * @author Sven Wende
 * 
 */
public final class BeanPropertyTableRowAdapter extends AbstractTableRowAdapter<IElement> {
	private String key;
	private String property;
	private boolean readOnly;

	/**
	 * Constructor.
	 * 
	 * @param key
	 *            the key for the key column
	 * @param delegate
	 *            the element
	 * @param beanProperty
	 *            the name of a bean property that should be used in the value
	 *            column
	 * @param readOnly
	 *            true, if the table row should be read only
	 */
	public BeanPropertyTableRowAdapter(String key, IElement delegate, String beanProperty, boolean readOnly) {
		super(delegate);
		this.key = key;
		this.property = beanProperty;
		this.readOnly = readOnly;
	}

	/**
	 * @Override {@inheritDoc}
	 */
	protected String doGetKey(IElement delegate) {
		return key;
	}

	/**
	 * @Override {@inheritDoc}
	 */
	@Override
	protected String doGetValue(IElement delegate) {
		String result = "";
		PropertyUtilsBean util = new PropertyUtilsBean();
		try {
			Object o = util.getProperty(delegate, property);
			result = o != null ? o.toString() : null;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return result;
	}

	/**
	 * @Override {@inheritDoc}
	 */
	@Override
	protected String doGetValueForDisplay(IElement delegate) {
		return doGetValue(delegate);
	}

	/**
	 * @Override {@inheritDoc}
	 */
	@Override
	protected Command doSetValue(IElement delegate, Object value) {
		Command result = null;

		if (value != null) {
			result = new ChangeBeanPropertyCommand(delegate, property, value);
		}

		return result;
	}

	/**
	 * @Override {@inheritDoc}
	 */
	@Override
	protected boolean doCanModifyValue(IElement delegate) {
		return !readOnly;
	}
}
