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
package org.csstudio.alarm.treeview.model;

import static org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY;
import static org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY;
import static org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART;
import static org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute.HELP_GUIDANCE;
import static org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.treeview.jface.CellEditorValidators;
import org.csstudio.alarm.treeview.ldap.ModifyLdapAttributeModificationItem;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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
 * Changes made in the property sheet are immediately persisted in LDAP, since this a
 * <b>different</b> view and not connected to the alarm tree view and its modification items.
 * Another consequence is that the methods setPropertyValue and resetPropertyValue do not affect
 * the alarm tree view directly anymore by setting the node's properties.
 * That would be a violation of the separation of concerns rule and a breakdown of the
 * model encapsulation.
 *
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
	private final AbstractAlarmTreeNode _node;

	private final AlarmTreeView _view;

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
	}

	static {
		PROPERTY_DESCRIPTORS = new IPropertyDescriptor[7];
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
		descriptor = new TextPropertyDescriptor(HELP_PAGE, "help page");
		descriptor.setDescription(HELP_PAGE.getDescription());
		descriptor.setValidator(CellEditorValidators.get(HELP_PAGE.getPropertyClass()));
		PROPERTY_DESCRIPTORS[2] = descriptor;

		// help guidance
		descriptor = new TextPropertyDescriptor(HELP_GUIDANCE, "help guidance");
		descriptor.setDescription(HELP_GUIDANCE.getDescription());
		descriptor.setValidator(CellEditorValidators.get(HELP_GUIDANCE.getPropertyClass()));
		PROPERTY_DESCRIPTORS[3] = descriptor;

		// CSS alarm display
		descriptor = new TextPropertyDescriptor(CSS_ALARM_DISPLAY, "alarm display");
		descriptor.setDescription(CSS_ALARM_DISPLAY.getDescription());
		descriptor.setValidator(CellEditorValidators.get(CSS_ALARM_DISPLAY.getPropertyClass()));
		PROPERTY_DESCRIPTORS[4] = descriptor;

		// CSS display
		descriptor = new TextPropertyDescriptor(CSS_DISPLAY, "display");
		descriptor.setDescription(CSS_DISPLAY.getDescription());
		descriptor.setValidator(CellEditorValidators.get(CSS_DISPLAY.getPropertyClass()));
		PROPERTY_DESCRIPTORS[5] = descriptor;

		// CSS strip chart
		descriptor = new TextPropertyDescriptor(CSS_STRIP_CHART, "strip chart");
		descriptor.setDescription(CSS_STRIP_CHART.getDescription());
		descriptor.setValidator(CellEditorValidators.get(CSS_STRIP_CHART.getPropertyClass()));
		PROPERTY_DESCRIPTORS[6] = descriptor;
	}

	/**
	 * Creates a new property source for the given node.
	 * @param node the node.
	 */
	public AlarmTreeNodePropertySource(@Nonnull final AbstractAlarmTreeNode node) {

	    _node = node;
	    _view = findView();
        //_attributes = retrieveNodeAttributesFromLdap(node);
	}

	/**
	 * Getter for the adaptableObject node.
	 * @return the node
	 */
	@Nonnull
    public AbstractAlarmTreeNode getNode() {
        return _node;
    }

	@CheckForNull
	private static AlarmTreeView findView() {
        final IWorkbenchWindow activeWorkbenchWindow =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWorkbenchWindow != null) {
            final IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
            if (page != null) {
                final IViewPart view = page.findView(AlarmTreeView.getID());
                if (view instanceof AlarmTreeView) {
                    return (AlarmTreeView) view;
                }
            }
        }
        return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final Object getEditableValue() {
		// not editable
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nonnull
    public final IPropertyDescriptor[] getPropertyDescriptors() {
		return PROPERTY_DESCRIPTORS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@CheckForNull
    public final Object getPropertyValue(@Nullable final Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
				return _node.getName();
			case OBJECT_CLASS:
				return _node.getTreeNodeConfiguration().getObjectClass();
			default:
				return null;
			}
		} else if (id instanceof EpicsAlarmcfgTreeNodeAttribute) {
		    final String property = _node.getInheritedProperty((EpicsAlarmcfgTreeNodeAttribute) id);
            return property == null ? "" : property;
		}
		return null;
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean isPropertySet(@Nullable final Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
			case OBJECT_CLASS:
				// no default value, always return true.
				return true;
			default:
				// this source does not have the specified property
				return false;
			}
		} else if (id instanceof EpicsAlarmcfgTreeNodeAttribute) {
			return _node.getOwnProperty((EpicsAlarmcfgTreeNodeAttribute) id) != null;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final void resetPropertyValue(@Nullable final Object id) {
		if (id instanceof EpicsAlarmcfgTreeNodeAttribute) {
		    final EpicsAlarmcfgTreeNodeAttribute propId = (EpicsAlarmcfgTreeNodeAttribute) id;
            _node.setProperty(propId, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final void setPropertyValue(@Nullable final Object id,
                                       @Nullable final Object value) {
		if (id instanceof EpicsAlarmcfgTreeNodeAttribute) {
		    final EpicsAlarmcfgTreeNodeAttribute propId = (EpicsAlarmcfgTreeNodeAttribute) id;
		    _node.setProperty(propId, (String) value);

		    if (_node.getSource().equals(TreeNodeSource.LDAP)) {
		        _view.addLdapTreeModificationItem(new ModifyLdapAttributeModificationItem(_node.getLdapName(), propId, (String) value));
		    }
		}

	}



    /**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean isPropertyResettable(@Nullable final Object id) {
	    return id instanceof EpicsAlarmcfgTreeNodeAttribute;
	}
}
