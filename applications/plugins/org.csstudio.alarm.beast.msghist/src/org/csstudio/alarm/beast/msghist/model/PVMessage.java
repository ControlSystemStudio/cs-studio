/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.model;

import java.util.Map;

/** A log message that provides IProcessVariable via the NAME property.
 *  @author Kay Kasemir
 */
public class PVMessage extends Message
{
    public PVMessage(final int sequence, final int id, final Map<String, String> properties)
    {
    	super(sequence, id, properties);
    }

    /** @return "NAME" property
     *  @see IProcessVariable
     */
    public String getName()
	{
		return getProperty(Message.NAME);
	}
}
