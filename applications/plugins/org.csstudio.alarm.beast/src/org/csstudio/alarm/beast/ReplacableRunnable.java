/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast;

/** A {@link Runnable} for the {@link WorkQueue}
 *  that can be replaced with an updated Runnable
 *  for the same object.
 *  
 *  <p>This is used to queue RDB updates:
 *  If a PV changes its state several times,
 *  it is not necessary to send every single state
 *  change to the RDB.
 *  Only writing the 'last' change will be sufficient,
 *  so any entries on the work queue that have not, yet,
 *  been written to the RDB are replaced with the latest
 *  one.
 *  
 *  <p>The comparison is based on an {@link Object},
 *  which could be a PV.
 * 
 *  @author Kay
 */
abstract public class ReplacableRunnable<T> implements Runnable
{
	final private T object;

	public ReplacableRunnable(final T object)
    {
		this.object = object;
    }

	@Override
    public int hashCode()
    {
		return object.hashCode();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj)
    {
	    if (obj instanceof ReplacableRunnable)
	    {
		    final ReplacableRunnable other = (ReplacableRunnable) obj;
		    return object.equals(other.object);
	    }
    	return false;
    }
}
