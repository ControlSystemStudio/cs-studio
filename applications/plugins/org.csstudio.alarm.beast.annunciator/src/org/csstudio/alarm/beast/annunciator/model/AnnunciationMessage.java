/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.model;

import org.csstudio.data.values.ITimestamp;
import org.csstudio.data.values.TimestampFactory;

/** Combination of message and Severity, used by SpeechPriorityQueue.
 *
 *  @author Delphy Armstrong
 *  @author Kay Kasemir
 *
 *         reviewed by Delphy 1/29/09
 */
public class AnnunciationMessage implements Comparable<AnnunciationMessage>
{
    private static final String STANDOUT_PREFIX = "!"; //$NON-NLS-1$
    final private ITimestamp time;
    final private Severity severity;
    final private boolean standout;
    final private String message;

    /** Initialize a message-with-severity
     *  @param severity Severity of the message
     *  @param message  Message that was received with that severity
     */
    public AnnunciationMessage(final Severity severity, final String message)
    {
        this.time = TimestampFactory.now();
        this.severity = severity;
        this.standout = message.trim().startsWith(STANDOUT_PREFIX);
        this.message = message;
    }

    /** @return <code>true</code> if this is a 'standout' message that must not be
     *          suppressed even though it's part of a flurry of messages
     */
    public boolean isStandoutMessage()
    {
        return standout;
    }

    /** @return Time when message was received */
    public ITimestamp getTimestamp()
    {
        return time;
    }

    /** @return Severity */
    public Severity getSeverity()
    {
        return severity;
    }

    /** @return Message for the annunciator */
    public String getMessage()
    {
        return message;
    }

    /** Compare by severity, but invert the order so that highest severity is
     *  queued first, not last
     */
    @Override
    public int compareTo(final AnnunciationMessage other)
    {
        return other.getSeverity().compareTo(severity);
    }

    /** Compare by Severity
     *  {@inheritDoc}
     */
	@Override
    public boolean equals(Object obj)
    {
		if (! (obj instanceof AnnunciationMessage))
			return false;
	    AnnunciationMessage other = (AnnunciationMessage) obj;
	    return other.getSeverity().equals(severity);
    }

    /** Compare by Severity
     *  {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
    	return severity.hashCode();
    }

	/** @return String representation for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return time + ", " + severity.getName() + ": " + message;
    }
}
