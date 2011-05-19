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

package org.csstudio.alarm.treeview.ldap;

import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.log4j.Logger;
import org.csstudio.alarm.treeview.model.IAlarmTreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;


/**
 * Utility class for building the tree model. The methods of this class only
 * create tree items in the object model of the tree. No LDAP entries are created
 * or modified by this class.
 *
 * @author Joerg Rathlev
 */
public final class AlarmTreeNodeModifier {
    /**
     * The logger that is used by this class.
     */
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmTreeNodeModifier.class);

	/**
	 * Private constructor.
	 */
	private AlarmTreeNodeModifier() {
	    // EMPTY
	}

    /**
     * Sets the EPICS attributes of the given node based on the given
     * attributes.
     *
     * @param node
     *            the node.
     * @param attrs
     *            the attributes.
     * @throws NamingException
     *             if an attribute could not be retrieved.
     */
    public static void setEpicsAttributes(@Nonnull final IAlarmTreeNode node,
                                          @Nonnull final Attributes attrs) throws NamingException {
        setPropertyOnNode(node, attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY);

        final String helpPage = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE);
        if (helpPage != null && helpPage.matches("^http://.+")) {
            try {
                node.setProperty(EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE, new URL(helpPage).toString());
            } catch (final MalformedURLException e) {
                LOG.warn(EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE.getLdapAttribute() +
                         " attribute for node " + node + " contains a malformed URL");
            }
        }

        setPropertyOnNode(node, attrs, EpicsAlarmcfgTreeNodeAttribute.HELP_GUIDANCE);
        setPropertyOnNode(node, attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY);
        setPropertyOnNode(node, attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART);
    }
    
    
    private static void setPropertyOnNode(@Nonnull final IAlarmTreeNode node,
                                          @Nonnull final Attributes attrs,
                                          @Nonnull EpicsAlarmcfgTreeNodeAttribute attribute) throws NamingException {
        final String propertyAsString = extractAttribute(attrs, attribute);
        if (propertyAsString != null) {
            node.setProperty(attribute, propertyAsString);
        }
    }

    @CheckForNull
    private static String extractAttribute(@Nonnull final Attributes attrs,
                                           @Nonnull final EpicsAlarmcfgTreeNodeAttribute id) throws NamingException {

        final Attribute attr = attrs.get(id.getLdapAttribute());
        if (attr != null) {
            return (String) attr.get();
        }
        return null;
    }

}
