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

import javax.annotation.Nonnull;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.csstudio.alarm.treeView.EventtimeUtil;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.platform.logging.CentralLogger;


/**
 * Utility class for building the tree model. The methods of this class only
 * create tree itemsin the object model of the tree. No LDAP entries are created
 * or modified by this class.
 *
 * @author Joerg Rathlev
 */
public final class AlarmTreeNodeModifier {

    // TODO (bknerr) : if used anywhere else in this package, encapsulate
    // in static package-visible class

    private static final String EPICS_ALARM_HIGH_UN_ACKN = "epicsAlarmHighUnAckn";

    private static final String EPICS_ALARM_TIME_STAMP = "epicsAlarmTimeStamp";

    private static final String EPICS_ALARM_SEVERITY = "epicsAlarmSeverity";

    private static final String EPICS_CSS_STRIP_CHART = "epicsCssStripChart";

    private static final String EPICS_CSS_DISPLAY = "epicsCssDisplay";

    private static final String EPICS_HELP_PAGE = "epicsHelpPage";

    private static final String EPICS_HELP_GUIDANCE = "epicsHelpGuidance";

    private static final String EPICS_CSS_ALARM_DISPLAY = "epicsCssAlarmDisplay";


    /**
     * The logger that is used by this class.
     */
    private static final CentralLogger LOG = CentralLogger.getInstance();

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
    public static void setEpicsAttributes(@Nonnull final AbstractAlarmTreeNode node,
                                          @Nonnull final Attributes attrs) throws NamingException {

        final Attribute alarmDisplayAttr = attrs.get(EPICS_CSS_ALARM_DISPLAY);
        if (alarmDisplayAttr != null) {
            final String display = (String) alarmDisplayAttr.get();
            if (display != null) {
                node.setCssAlarmDisplay(display);
            }
        }

        final Attribute helpPageAttr = attrs.get(EPICS_HELP_PAGE);
        if (helpPageAttr != null) {
            final String helpPage = (String) helpPageAttr.get();
            if ((helpPage != null) && helpPage.matches("^http://.+")) {
                try {
                    node.setHelpPage(new URL(helpPage));
                } catch (final MalformedURLException e) {
                    LOG.warn(AlarmTreeBuilder.class.getName(), EPICS_HELP_PAGE + " attribute for node "
                            + node + " contains a malformed URL");
                }
            }
        }

        final Attribute helpGuidanceAttr = attrs.get(EPICS_HELP_GUIDANCE);
        if (helpGuidanceAttr != null) {
            final String helpGuidance = (String) helpGuidanceAttr.get();
            if (helpGuidance != null) {
                node.setHelpGuidance(helpGuidance);
            }
        }

        final Attribute displayAttr = attrs.get(EPICS_CSS_DISPLAY);
        if (displayAttr != null) {
            final String display = (String) displayAttr.get();
            if (display != null) {
                node.setCssDisplay(display);
            }
        }

        final Attribute chartAttr = attrs.get(EPICS_CSS_STRIP_CHART);
        if (chartAttr != null) {
            final String chart = (String) chartAttr.get();
            if (chart != null) {
                node.setCssStripChart(chart);
            }
        }
    }

    /**
     * Sets the alarm state of the given node based on the given attributes.
     *
     * @param node
     *            the node.
     * @param attrs
     *            the attributes.
     */
    static void setAlarmState(@Nonnull final ProcessVariableNode node, @Nonnull final Attributes attrs)
            throws NamingException {
        final Attribute severityAttr = attrs.get(EPICS_ALARM_SEVERITY);
        final Attribute eventtimeAttr = attrs.get(EPICS_ALARM_TIME_STAMP);
        final Attribute highUnAcknAttr = attrs.get(EPICS_ALARM_HIGH_UN_ACKN);
        if (severityAttr != null) {
            final String severityVal = (String) severityAttr.get();
            if (severityVal != null) {
                final Severity sev = Severity.parseSeverity(severityVal);
                Date date = null;
                if (eventtimeAttr != null) {
                    final String eventtimeStr = (String) eventtimeAttr.get();
                    if (eventtimeStr != null) {
                        date = EventtimeUtil.parseTimestamp(eventtimeStr);
                    }
                }
                if (date == null) {
                    date = new Date();
                }
                node.updateAlarm(new Alarm(node.getName(), sev, date));
            }
        }
        Severity unack = Severity.NO_ALARM;
        if (highUnAcknAttr != null) {
            final String severity = (String) highUnAcknAttr.get();
            if (severity != null) {
                unack = Severity.parseSeverity(severity);
            }
        }
        node.setHighestUnacknowledgedAlarm(new Alarm(node.getName(), unack, new Date()));
    }

}
