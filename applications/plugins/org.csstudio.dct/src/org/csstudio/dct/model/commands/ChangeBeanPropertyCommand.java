/**
 * 
 */
package org.csstudio.dct.model.commands;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.eclipse.gef.commands.Command;

public class ChangeBeanPropertyCommand extends Command {
	public ChangeBeanPropertyCommand(Object delegate, String propertyName, Object value) {
		super();
		this.delegate = delegate;
		this.propertyName = propertyName;
		this.value = value;
	}

	private Object delegate;
	private String propertyName;
	private Object value;
	private Object oldValue;
	
	@Override
	public void execute() {
		PropertyUtilsBean util = new PropertyUtilsBean();
		try {
			oldValue = util.getProperty(delegate, propertyName);
			util.setProperty(delegate, propertyName, value);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void undo() {
		PropertyUtilsBean util = new PropertyUtilsBean();
		try {
			util.setProperty(delegate, propertyName, oldValue);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
}