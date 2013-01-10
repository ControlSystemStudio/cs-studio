/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

/** Info about person in chat
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Person implements Comparable<Person>
{
	final private String name;
	final private String address;
	
	/** Initialize
	 *  @param name (Nick-)name
	 *  @param address Chat address
	 */
	public Person(final String name, final String address)
    {
	    this.name = name;
	    this.address = address;
    }
	
	/** @return name (Nick-)name */
	public String getName()
    {
    	return name;
    }
	
	/** @return Chat address */
	public String getAddress()
    {
    	return address;
    }

	/** Hash on name */
	@Override
    public int hashCode()
    {
		return name.hashCode();
    }

	/** Compare on name */
	@Override
    public boolean equals(final Object other)
    {
		if (! (other instanceof Person))
			return false;
		return ((Person)other).name.equals(name);
    }

	/** Compare on name */
	@Override
    public int compareTo(final Person other)
    {
	    return name.compareTo(other.name);
    }

    @Override
    public String toString()
    {
	    return name + " (" + address + ")";
    }
}
