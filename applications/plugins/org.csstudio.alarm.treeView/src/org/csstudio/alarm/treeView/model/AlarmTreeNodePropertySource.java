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
package org.csstudio.alarm.treeView.model;

import java.net.URL;

import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Adapter to provide property support for {@link IAlarmTreeNode}s. Note that
 * the alarm state of the node is not provided as a property. This is
 * intentional, as updates of the alarm state would not be relected in the
 * property sheet until the selection in the tree is updated or modified.
 * 
 * @author Joerg Rathlev
 */
public class AlarmTreeNodePropertySource implements IPropertySource2 {

	/**
	 * The descriptors of the properties provided by this source.
	 */
	private static final IPropertyDescriptor[] PROPERTY_DESCRIPTORS;
	
	/**
	 * The node for which this property source provides properties.
	 */
	private AbstractAlarmTreeNode _node;
	
	/**
	 * IDs for the properties.
	 */
	private static enum PropertyID {
		
		/**
		 * Property ID for the name property.
		 */
		NAME,
		
		/**
		 * Property ID of the object class property. 
		 */
		OBJECT_CLASS,
		
		/**
		 * Property ID of the help page property.
		 */
		HELP_PAGE,
		
		/**
		 * Property ID of the help guidance property.
		 */
		HELP_GUIDANCE,
		
		/**
		 * Property ID of the CSS alarm display property.
		 */
		CSS_ALARM_DISPLAY,
		
		/**
		 * Property ID of the CSS display property.
		 */
		CSS_DISPLAY,
		
//		/**
//		 * Property ID of the CSS strip chart property.
//		 */
//		CSS_STRIP_CHART,
	}

	static {
		PROPERTY_DESCRIPTORS = new IPropertyDescriptor[6];
		PropertyDescriptor descriptor;
		
		// name
		descriptor = new PropertyDescriptor(PropertyID.NAME, "name");
		descriptor.setAlwaysIncompatible(true);
		descriptor.setDescription("The name of the object.");
		PROPERTY_DESCRIPTORS[0] = descriptor;
		
		// object class
		descriptor = new PropertyDescriptor(PropertyID.OBJECT_CLASS, "object class");
		descriptor.setDescription("The object class of the object.");
		PROPERTY_DESCRIPTORS[1] = descriptor;
		
		// help page
		descriptor = new TextPropertyDescriptor(PropertyID.HELP_PAGE, "help page");
		descriptor.setDescription("The help page. This should be the URL of a web page.");
		PROPERTY_DESCRIPTORS[2] = descriptor;
		
		// help guidance
		descriptor = new TextPropertyDescriptor(PropertyID.HELP_GUIDANCE, "help guidance");
		descriptor.setDescription("A short description of the object.");
		PROPERTY_DESCRIPTORS[3] = descriptor;
		
		// CSS alarm display
		descriptor = new TextPropertyDescriptor(PropertyID.CSS_ALARM_DISPLAY, "alarm display");
		descriptor.setDescription("The CSS alarm display.");
		PROPERTY_DESCRIPTORS[4] = descriptor;
		
		// CSS display
		descriptor = new TextPropertyDescriptor(PropertyID.CSS_DISPLAY, "display");
		descriptor.setDescription("The CSS display.");
		PROPERTY_DESCRIPTORS[5] = descriptor;
	}
	
	/**
	 * Creates a new property source for the given node.
	 * @param node the node.
	 */
	public AlarmTreeNodePropertySource(final AbstractAlarmTreeNode node) {
		this._node = node;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final Object getEditableValue() {
		// not editable
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final IPropertyDescriptor[] getPropertyDescriptors() {
		return PROPERTY_DESCRIPTORS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final Object getPropertyValue(final Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
				return _node.getName();
			case OBJECT_CLASS:
				// TODO: not supported by nodes yet
				return null;
			case HELP_PAGE:
				URL page = _node.getHelpPage();
				return (page != null) ? page.toString() : "";
			case HELP_GUIDANCE:
				return _node.getHelpGuidance();
			case CSS_ALARM_DISPLAY:
				return _node.getCssAlarmDisplay();
			case CSS_DISPLAY:
				// TODO: not supported by nodes yet
				return null;
			default:
				return null;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isPropertySet(final Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
				// Name doesn't have a default value, always return true.
				return true;
			case OBJECT_CLASS:
				// TODO: not supported by nodes yet
				return false;
			case HELP_PAGE:
				return _node.getHelpPage() != null;
			case HELP_GUIDANCE:
				return _node.getHelpGuidance() != null;
			case CSS_ALARM_DISPLAY:
				return _node.getCssAlarmDisplay() != null;
			case CSS_DISPLAY:
				// TODO: not supported by nodes yet
				return false;
			default:
				// this source does not have the specified property
				return false;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void resetPropertyValue(final Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case HELP_PAGE:
				// TODO: edit in LDAP
				_node.setHelpPage(null);
			case HELP_GUIDANCE:
				// TODO: edit in LDAP
				_node.setHelpGuidance(null);
			case CSS_ALARM_DISPLAY:
				// TODO: edit in LDAP
				_node.setCssAlarmDisplay(null);
			default:
				// do nothing
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setPropertyValue(final Object id, final Object value) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case HELP_GUIDANCE:
				try {
					String str = (String) value;
					if (str.equals("")) {
						str = null;
					}
					DirectoryEditor.modifyHelpGuidance(_node, str);
				} catch (DirectoryEditException e) {
					// TODO Auto-generated catch block
				}
				break;
			default:
				// do nothing
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean isPropertyResettable(final Object id) {
		return id == PropertyID.HELP_PAGE
			|| id == PropertyID.HELP_GUIDANCE
			|| id == PropertyID.CSS_ALARM_DISPLAY;
	}
}
