package org.csstudio.dct.ui.editor.tables;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.commands.ChangeBeanPropertyCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

/**
 * Row adapter for the name of the record.
 * 
 * @author Sven Wende
 * 
 */
public class BeanPropertyTableRowAdapter extends AbstractTableRowAdapter<IElement> {
	private String key;
	private String property;
	private boolean readOnly;
	
	public BeanPropertyTableRowAdapter(String key, IElement delegate, CommandStack commandStack, String beanProperty, boolean readOnly){
		super(delegate, commandStack);
		this.key =key;
		this.property = beanProperty;
		this.readOnly = readOnly;
	}
	
	public BeanPropertyTableRowAdapter(String key, IElement delegate, CommandStack commandStack, String beanProperty){
		super(delegate, commandStack);
		this.key =key;
		this.property = beanProperty;
		this.readOnly = false;
	}

	@Override
	protected String doGetKey(IElement delegate) {
		return key;
	}

	@Override
	protected Object doGetValue(IElement delegate) {
		Object result = "";
		PropertyUtilsBean util = new PropertyUtilsBean();
		try {
			result = util.getProperty(delegate, property);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return result;
	}

	@Override
	protected Object doGetValueForDisplay(IElement delegate) {
		return doGetValue(delegate);
	}

	@Override
	protected Command doSetValue(IElement delegate, Object value) {
		Command result = null;

		if (value != null) {
			result = new ChangeBeanPropertyCommand(delegate, property, value);
		}

		return result;
	}

	@Override
	protected boolean doCanModifyValue(IElement delegate) {
		return !readOnly;
	}
}