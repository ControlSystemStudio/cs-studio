/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.client;

import java.io.PrintWriter;

import org.csstudio.alarm.beast.Messages;
import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.XMLTags;
import org.csstudio.apputil.xml.XMLWriter;
import org.eclipse.osgi.util.NLS;
import org.epics.util.time.Timestamp;

/** Leaf item in the alarm configuration tree which refers to a PV,
 *  tracking the current and alarm state, value, timestamp info
 *
 *  @author Kay Kasemir, Xihui Chen
 */
public class AlarmTreePV extends AlarmTreeLeaf
{
    private boolean enabled = true;
    private boolean latching = true;
    private boolean annunciating = false;

    private int delay = 0;

    /* Alarm when PV != OK more often than this count within delay */
    private int count = 0;

    private String filter = ""; //$NON-NLS-1$

    /** Current message of this item/subtree */
    private String current_message = SeverityLevel.OK.getDisplayName();

    private String value = null;

    /** Initialize
     *  @param parent Parent component in hierarchy
     *  @param name PV name
     *  @param id RDB ID
     */
    public AlarmTreePV(final AlarmTreeItem parent,
            final String name, final int id)
    {
        super(parent, name, id);
    }

    /** {@inheritDoc} */
    @Override
    public AlarmTreePosition getPosition()
    {
        return AlarmTreePosition.PV;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized String getToolTipText()
    {
        return NLS.bind(Messages.AlarmPV_TT,
            new Object[]
            {
                getPathName(),
                getSeverity().getDisplayName(),
                getMessage(),
                getValue(),
                getDuration(),
                getDelay(),
                getCurrentSeverity().getDisplayName(),
                getCurrentMessage()
            });
    }

    /** @return Current severity */
    @Override
    public synchronized SeverityLevel getCurrentSeverity()
    {
        return enabled ? super.getCurrentSeverity() : SeverityLevel.OK;
    }

    /** @return Current message */
    public synchronized String getCurrentMessage()
    {
        return current_message;
    }

    /** @return Highest or latched severity */
    @Override
    public synchronized SeverityLevel getSeverity()
    {
        return enabled ? super.getSeverity() : SeverityLevel.OK;
    }

    /** @return Highest or latched alarm message */
    @Override
    public synchronized String getMessage()
    {
        return enabled ? super.getMessage() : SeverityLevel.OK.getDisplayName();
    }

    /** @return <code>true</code> if alarms from PV are enabled */
    public synchronized boolean isEnabled()
    {
        return enabled;
    }

    /** Set filter expression for enablement
	 *  @param filter New filter
	 */
	public synchronized void setFilter(final String filter)
	{
		if (filter == null)
			this.filter = ""; //$NON-NLS-1$
		else
			this.filter = filter;
	}

	/** @param enable Enable the PV? */
    public synchronized void setEnabled(final boolean enable)
    {
        enabled = enable;
    }

    /** @return Filter expression for enablement (never <code>null</code>) */
    public synchronized String getFilter()
    {
		return filter;
	}

	/** @param annunciating New annunciating behavior */
    public synchronized void setAnnunciating(final boolean annunciating)
    {
        this.annunciating = annunciating;
    }

    /** @return <code>true</code> if alarms get annunciated */
    public synchronized boolean isAnnunciating()
    {
        return annunciating;
    }

    /** @param latching New latching behavior */
    public synchronized void setLatching(final boolean latching)
    {
        this.latching = latching;
    }

    /** @return <code>true</code> if alarms latch for acknowledgment */
    public synchronized boolean isLatching()
    {
        return latching;
    }

    /** @param seconds Alarm delay */
    public synchronized void setDelay(final double seconds)
    {
        delay = (int) Math.round(seconds);
    }

    /** @return Alarm delay in seconds */
    public synchronized int getDelay()
    {
        return delay;
    }

    /** @return count Alarm when PV != OK more often than this count within delay */
    public synchronized int getCount()
    {
        return count;
    }

    /** Alarm when PV != OK more often than this count within delay
     *  @param count New count
     */
    public synchronized void setCount(final int count)
    {
        this.count = count;
    }

    /** @return Value that triggered last status/severity update */
    public synchronized String getValue()
    {
        return value;
    }

    /** Update status/message/time stamp and maximize
     *  severities of parent entries.
     *
     *  @param current_severity Current severity of PV
     *  @param current_message Current message of the PV
     *  @param severity Alarm severity
     *  @param message Alarm message
     *  @param value Value that triggered the update
     *  @param timestamp Time stamp for this update
     */
    public void setAlarmState(final SeverityLevel current_severity,
            final String current_message,
            final SeverityLevel severity, final String message,
            final String value,
            final Timestamp timestamp)
    {
    	// Changing the alarm state will eventually recurse up to the root
    	// to maximize the severities.
    	// To prevent deadlock, first lock the root, then this and other affected items
    	final AlarmTreeRoot root = getRoot();
    	final boolean parent_changed;
    	synchronized (root)
        {
        	synchronized (this)
            {
                if (setAlarmState(current_severity, severity, message, timestamp)  ||
                    ! current_message.equals(this.current_message) )
                {   // Alarm state or at least message changed
                    this.current_message = current_message;
                    this.value = value;
                    parent_changed = true;
                }
                else
                    parent_changed = false;
            }
        }
    	// Send events outside of lock
    	root.notifyListeners(this, parent_changed);
    }

    /** Called either directly or recursively from parent item.
     *  @see AlarmTree#acknowledge()
     */
    @Override
    public void acknowledge(final boolean acknowledge)
    {
        getRoot().acknowledge(this, acknowledge);
    }

    /** @return XML tag for this tree item */
    @Override
    protected String getXMLTag()
    {
        return XMLTags.PV;
    }

	/** Add PV config detail.
	 *  @see AlarmTree#writeConfigXML(PrintWriter, String)
	 */
    @Override
    protected synchronized void writeConfigXML(final PrintWriter out, final int level)
    {
        XMLWriter.XML(out, level, XMLTags.DESCRIPTION, getDescription());
        if (!enabled)
            XMLWriter.XML(out, level, XMLTags.ENABLED, Boolean.FALSE.toString());
        if (latching)
            XMLWriter.XML(out, level, XMLTags.LATCHING, Boolean.TRUE.toString());
        if (annunciating)
            XMLWriter.XML(out, level, XMLTags.ANNUNCIATING, Boolean.TRUE.toString());
        if (delay > 0)
        {
            XMLWriter.XML(out, level, XMLTags.DELAY, Double.toString(delay));
            XMLWriter.XML(out, level, XMLTags.COUNT, Integer.toString(count));
        }
        if (filter.length() > 0)
            XMLWriter.XML(out, level, XMLTags.FILTER, filter);
        super.writeConfigXML(out, level);
    }

    /** @return Verbose, multi-line description of the current alarm
     *          meant for elog entry or usage as drag/drop text
     */
    @Override
    public synchronized String getVerboseDescription()
    {
        return NLS.bind(Messages.VerboseAlarmPVDescriptionFmt,
                new Object[]
                {
                    getDescription(),
                    getName(),
                    getTimestampText(),
                    getDuration(),
                    getSeverity().getDisplayName(),
                    getMessage(),
                    getValue(),
                    getCurrentSeverity().getDisplayName(),
                    getCurrentMessage()
                });
    }
}
