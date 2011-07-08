/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.opibuilder.commands;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.Command;

/**
 * A Command to set a property value of a widget. Use command can help to realize redo/undo.
 * @author Kai Meyer(original author), Xihui Chen (since import from SDS 2009/9) 
 *
 */
public class SetWidgetPropertyCommand extends Command {
	
	/**
	 * The {@link AbstractWidgetModel}.
	 */
	private AbstractWidgetModel widgetModel;
	/**
	 * The name of the property.
	 */
	private String prop_id;
	/**
	 * The new value for the property.
	 */
	private Object newValue;
	/**
	 * The old value of the property.
	 */
	private Object oldValue;
	
	/**
	 * Constructor.
	 * @param widget
	 * 			The widget, whose property value should be set
	 * @param prop_id
	 * 			The id of the property
	 * @param newValue
	 * 			The new value for the property
	 */
	public SetWidgetPropertyCommand(final AbstractWidgetModel widget, final String prop_id, final Object newValue) {
		this.widgetModel = widget;
		this.prop_id = prop_id;
		this.newValue = newValue;
		setLabel("Set " + prop_id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		oldValue = widgetModel.getPropertyValue(prop_id);
		widgetModel.setPropertyValue(prop_id, newValue);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		widgetModel.setPropertyValue(prop_id, oldValue);
	}

}
