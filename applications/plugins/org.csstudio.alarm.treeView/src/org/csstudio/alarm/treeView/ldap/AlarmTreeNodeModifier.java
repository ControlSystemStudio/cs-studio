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

package org.csstudio.alarm.treeView.ldap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.EventtimeUtil;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.EpicsAlarmcfgTreeNodeAttribute;


/**
 * Utility class for building the tree model. The methods of this class only
 * create tree itemsin the object model of the tree. No LDAP entries are created
 * or modified by this class.
 *
 * @author Joerg Rathlev
 */
public final class AlarmTreeNodeModifier {
    /**
     * The logger that is used by this class.
     */
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmTreeNodeModifier.class);

 // TODO (bknerr) : try to figure out whether enums could be used for distinct groups
    private static final String ATTR_FIELD_ALARM_SEVERITY = "epicsAlarmSeverity";
    private static final String ATTR_FIELD_ALARM_TIMESTAMP = "epicsAlarmTimeStamp";
    private static final String ATTR_FIELD_ALARM_HIGH_UNACK = "epicsAlarmHighUnAckn";

	/**
	 * Private constructor.
	 */
	private AlarmTreeNodeModifier() {
	    // EMPTY
	}

	   /**
     * Evaluates the attributes (if any) of an object found in the
     * directory. If there is an alarm, triggers the alarm for the node
     * in the alarm tree.
     *
     * @param attr the object's attributes.
     * @param node the node on which the alarm must be triggered.
     * @throws NamingException if an attribute could not be retrieved
     */
    public static void evaluateAttributes(@Nonnull final Attributes attrs,
                                          @Nonnull final ProcessVariableNode node) throws NamingException {
        setAlarmState(node, attrs);
        setEpicsAttributes(node, attrs);
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


        final String alarmDsp = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY);
        if (alarmDsp != null) {
            node.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_ALARM_DISPLAY, alarmDsp);
        }

        final String helpPage = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE);
        if (helpPage != null && helpPage.matches("^http://.+")) {
            try {
                node.setProperty(EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE, new URL(helpPage).toString());
            } catch (final MalformedURLException e) {
                LOG.warn(EpicsAlarmcfgTreeNodeAttribute.HELP_PAGE.getLdapAttribute() +
                         " attribute for node " + node + " contains a malformed URL");
            }
        }

        final String help = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.HELP_GUIDANCE);
        if (help != null) {
            node.setProperty(EpicsAlarmcfgTreeNodeAttribute.HELP_GUIDANCE, help);
        }


        final String display = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY);
        if (display != null) {
            node.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_DISPLAY, display);
        }


        final String chart = extractAttribute(attrs, EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART);
        if (chart != null) {
            node.setProperty(EpicsAlarmcfgTreeNodeAttribute.CSS_STRIP_CHART, chart);
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

    /**
     * Sets the alarm state of the given node based on the given attributes.
     *
     * @param node
     *            the node.
     * @param attrs
     *            the attributes.
     */
    public static void setAlarmState(@Nonnull final IAlarmProcessVariableNode node,
                                     @Nonnull final Attributes attrs)
            throws NamingException {

        final Attribute severityAttr = attrs.get(ATTR_FIELD_ALARM_SEVERITY);
        final Attribute eventtimeAttr = attrs.get(ATTR_FIELD_ALARM_TIMESTAMP);
        setSeverityAndTimestamp(node, severityAttr, eventtimeAttr);

        final Attribute highUnAcknAttr = attrs.get(ATTR_FIELD_ALARM_HIGH_UNACK);
        setHighestUnackAlarm(node, highUnAcknAttr);
    }

    private static void setSeverityAndTimestamp(@Nonnull final IAlarmProcessVariableNode node,
                                                @Nullable final Attribute severityAttr,
                                                @Nullable final Attribute eventtimeAttr) throws NamingException {
        if (severityAttr != null) {
            final String severityVal = (String) severityAttr.get();
            if (severityVal != null) {
                final EpicsAlarmSeverity sev = EpicsAlarmSeverity.parseSeverity(severityVal);
                Date date = new Date();
                if (eventtimeAttr != null) {
                    final String eventtimeStr = (String) eventtimeAttr.get();
                    if (eventtimeStr != null) {
                        date = EventtimeUtil.parseTimestamp(eventtimeStr);
                    }
                }
                node.updateAlarm(new Alarm(node.getName(), sev, date));
            }
        }
    }

    private static void setHighestUnackAlarm(@Nonnull final IAlarmProcessVariableNode node,
                                             @Nullable final Attribute highUnAcknAttr) throws NamingException {
        EpicsAlarmSeverity unack = EpicsAlarmSeverity.NO_ALARM;
        if (highUnAcknAttr != null) {
            final String severity = (String) highUnAcknAttr.get();
            if (severity != null) {
                unack = EpicsAlarmSeverity.parseSeverity(severity);
            }
        }
        node.setHighestUnacknowledgedAlarm(new Alarm(node.getName(), unack, new Date()));
    }
}
