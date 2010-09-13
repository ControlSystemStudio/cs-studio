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

import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;

import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.alarm.service.declaration.AlarmTreeNodePropertyId;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.views.WorkbenchWindowHelper;
import org.csstudio.alarm.treeView.views.actions.AlarmTreeViewActionFactory;
import org.csstudio.platform.util.StringUtil;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.eclipse.jface.dialogs.MessageDialog;
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

    /**
	 * The LDAP attributes found for this node.
	 */
    private Attributes _attributes;

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
		descriptor = new TextPropertyDescriptor(AlarmTreeNodePropertyId.HELP_PAGE, "help page");
		descriptor.setDescription(AlarmTreeNodePropertyId.HELP_PAGE.getDescription());
		PROPERTY_DESCRIPTORS[2] = descriptor;

		// help guidance
		descriptor = new TextPropertyDescriptor(AlarmTreeNodePropertyId.HELP_GUIDANCE, "help guidance");
		descriptor.setDescription(AlarmTreeNodePropertyId.HELP_GUIDANCE.getDescription());
		PROPERTY_DESCRIPTORS[3] = descriptor;

		// CSS alarm display
		descriptor = new TextPropertyDescriptor(AlarmTreeNodePropertyId.CSS_ALARM_DISPLAY, "alarm display");
		descriptor.setDescription(AlarmTreeNodePropertyId.CSS_ALARM_DISPLAY.getDescription());
		PROPERTY_DESCRIPTORS[4] = descriptor;

		// CSS display
		descriptor = new TextPropertyDescriptor(AlarmTreeNodePropertyId.CSS_DISPLAY, "display");
		descriptor.setDescription(AlarmTreeNodePropertyId.CSS_DISPLAY.getDescription());
		PROPERTY_DESCRIPTORS[5] = descriptor;

		// CSS strip chart
		descriptor = new TextPropertyDescriptor(AlarmTreeNodePropertyId.CSS_STRIP_CHART, "strip chart");
		descriptor.setDescription(AlarmTreeNodePropertyId.CSS_STRIP_CHART.getDescription());
		PROPERTY_DESCRIPTORS[6] = descriptor;
	}

	/**
	 * Creates a new property source for the given node.
	 * @param node the node.
	 */
	public AlarmTreeNodePropertySource(@Nonnull final AbstractAlarmTreeNode node) {

	    _node = node;
        _attributes = retrieveNodeAttributesFromLdap(node);
	}

	/**
	 * Getter for the adaptableObject node.
	 * @return the node
	 */
	@Nonnull
    public AbstractAlarmTreeNode getNode() {
        return _node;
    }

	@Nonnull
    private Attributes retrieveNodeAttributesFromLdap(@Nonnull final AbstractAlarmTreeNode node) {
        final String simpleName = node.getName();
        final LdapName ldapName = node.getLdapName();

        ILdapSearchResult result = null;
        try {
            ldapName.remove(ldapName.size() - 1); // remove simple name part

            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                exitWithErrorDialog("LDAP Access", "LDAP service unavailable, try again later.");
                return null;
            }

            result =
                service.retrieveSearchResultSynchronously(ldapName,
                                                          node.getTreeNodeConfiguration().getNodeTypeName() + FIELD_ASSIGNMENT + simpleName,
                                                          SearchControls.ONELEVEL_SCOPE);
        } catch (final InvalidNameException e) {
            exitWithErrorDialog("LDAP Access",
                                "LDAP name for node " + node.getLdapName().toString() +
                                " could not be composed. LDAP lookup canceled.");
            return null;
        }
        if (result == null || result.getAnswerSet().isEmpty()) {
            exitWithErrorDialog("LDAP Access", "No result could be retrieved from LDAP.");
            return null;
        }

        final Set<SearchResult> answerSet = result.getAnswerSet();
        if (answerSet.size() > 1) {
            exitWithErrorDialog("LDAP Access", "LDAP entry for node " + node.getLdapName().toString() +
                                " is not unique.");
            return null;
        }

        final SearchResult entry = answerSet.iterator().next();
        return entry.getAttributes();
    }

    private void exitWithErrorDialog(@Nonnull final String title, @Nonnull final String message) {
        MessageDialog.openError(null, title, message);
        WorkbenchWindowHelper.hideView(AlarmTreeViewActionFactory.PROPERTY_VIEW_ID);
        PropertySourceAdapterFactory.dirty();
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
				return _node.getTreeNodeConfiguration().getDescription();
			default:
				return null;
			}
		} else if (id instanceof AlarmTreeNodePropertyId) {
		    return extractValueFromAttributes((AlarmTreeNodePropertyId) id, _attributes);
		}
		return null;
	}

	@CheckForNull
    private String extractValueFromAttributes(@Nonnull final AlarmTreeNodePropertyId id,
                                              @Nonnull final Attributes attributes) {
        final String ldapAttrName = id.getLdapAttribute();
        final Attribute attr = attributes.get(ldapAttrName);

        if (attr == null) {
            return "";
        }

        // TODO (bknerr) : how to get type safety from here?
        String resultString = "";
        try {
            if (attr.size() > 1) {
                @SuppressWarnings("unchecked")
                final NamingEnumeration<String> allValues = (NamingEnumeration<String>) attr.getAll();
                resultString = StringUtil.join(allValues, ",");
            } else {
                resultString = (String) attr.get();
            }
        } catch (final NamingException e) {
            MessageDialog.openError(null,
                                    "Extract properties from LDAP attributes",
                                    "Naming exception occurred:\n" + e.getMessage());
        }
        return resultString != null ? resultString : "";
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
		} else if (id instanceof AlarmTreeNodePropertyId) {
			return _node.getOwnProperty((AlarmTreeNodePropertyId) id) != null;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final void resetPropertyValue(@Nullable final Object id) {
		if (id instanceof AlarmTreeNodePropertyId) {
		    //_node.setProperty((AlarmTreeNodePropertyId) id, null);
		    final AlarmTreeNodePropertyId propId = (AlarmTreeNodePropertyId) id;
            final ModificationItem item = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                                                               new BasicAttribute(propId.getLdapAttribute()));
            try {
                final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
                if (service == null) {
                    exitWithErrorDialog("LDAP Access", "LDAP service unavailable, try again later.");
                    return;
                }
                service.modifyAttributes(_node.getLdapName(), new ModificationItem[] {item});
            } catch (final NamingException e) {
                exitWithErrorDialog("LDAP Access", "Naming exception for node " + _node.getName());
                return;
            }
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public final void setPropertyValue(@Nullable final Object id, @Nullable final Object value) {
		if (id instanceof AlarmTreeNodePropertyId) {
		    final AlarmTreeNodePropertyId propId = (AlarmTreeNodePropertyId) id;

		    final String oldLdapValue = extractValueFromAttributes(propId, _attributes);

		    _attributes = retrieveNodeAttributesFromLdap(_node);

		    if (_attributes == null) {
		        WorkbenchWindowHelper.hideView(AlarmTreeViewActionFactory.PROPERTY_VIEW_ID);
		        return;
		    }

		    final String currentLdapValue = extractValueFromAttributes(propId, _attributes);

		    if (!oldLdapValue.equals(currentLdapValue)) {
		        exitWithErrorDialog("Set property value failed.",
		                            "The LDAP content for this node has changed, please reopen the property sheet.");
		        return;
		    }
		    if (value.equals(currentLdapValue)) {
		        return; // No changes to set in LDAP
		    }

		    replaceAttributeValueInLdap(value, propId);
		}
      //else { Ignore }
	}

    private void replaceAttributeValueInLdap(@Nullable final Object value,
                                             @Nonnull final AlarmTreeNodePropertyId propId) {

        if (StringUtil.isBlank((String) value)) {
            resetPropertyValue(propId);
            return;
        }

        try {
            final ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                                               new BasicAttribute(propId.getLdapAttribute(), value));

            final ILdapService service = AlarmTreePlugin.getDefault().getLdapService();
            if (service == null) {
                exitWithErrorDialog("LDAP Access", "LDAP service unavailable, try again later.");
                return;
            }
            service.modifyAttributes(_node.getLdapName(), new ModificationItem[] {item});

        } catch (final NamingException e) {
            exitWithErrorDialog("Write property value to LDAP failed.",
            "The node's property could not be written to LDAP due to NamingException:\n" +
            e.getMessage());
        }
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public final boolean isPropertyResettable(@Nullable final Object id) {
	    return id instanceof AlarmTreeNodePropertyId;
	}
}
