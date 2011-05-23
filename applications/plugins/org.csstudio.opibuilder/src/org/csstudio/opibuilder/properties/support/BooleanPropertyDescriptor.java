/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Descriptor for a property that is a boolean value which should be edited with
 * a boolean cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * IPropertyDescriptor pd = new BooleanPropertyDescriptor(&quot;fg&quot;, &quot;boolean&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author Kai Meyer (original author), Xihui Chen
 */
public final class BooleanPropertyDescriptor extends PropertyDescriptor {

	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public BooleanPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);

		setLabelProvider(new BooleanLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new CheckboxCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

	/**
	 * A label provider for boolean value, which displays a checked or unchecked box image.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	private final static class BooleanLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Image getImage(final Object element) {
			if (element instanceof Boolean) {
				if (((Boolean)element).booleanValue()) {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/checked.gif");
				} else {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/unchecked.gif");
				}
			} else {
				return null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof Boolean) {
				if (((Boolean)element).booleanValue()) {
					return "yes";
				} else {
					return "no";
				}
			} else {
				return element.toString();
			}
		}
	}
}
