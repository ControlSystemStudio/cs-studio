/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.commands;

import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;


/**
 * A command, which sets predefined value to predefined property.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class DynamicValueCommand<T> implements Command
{
	private SimpleProperty<T> property;
	private T value;
	private String name;
	private CommandContext owner;

	/**
	 * Creates a new DynamicValueCommand object.
	 *
	 * @param owner The context that this command belongs to
	 * @param name Command name
	 * @param property Property to set value to
	 * @param value Value to set to the property
	 */
	public DynamicValueCommand(CommandContext owner, String name,
	    SimpleProperty<T> property, T value)
	{
		super();

		if (property == null) {
			throw new IllegalArgumentException("property==null");
		}

		if (value == null) {
			throw new IllegalArgumentException("value==null");
		}

		this.property = property;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#name()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#execute(java.lang.Object...)
	 */
	public Object execute(Object... parameters) throws RemoteException
	{
		property.setValue(value);

		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getParameterTypes()
	 */
	public Class[] getParameterTypes()
	{
		return null;
	}

	/**
	 * Returns the property to set the value to.
	 *
	 * @return property
	 */
	public SimpleProperty getProperty()
	{
		return property;
	}

	/**
	 * Returns the value which is set to the property
	 *
	 * @return value
	 */
	public T getValue()
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getOwner()
	 */
	public CommandContext getOwner()
	{
		return owner;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.commands.Command#getREturnedType()
	 */
	public Class getReturnedType()
	{
		return null;
	}

	public boolean isAsynchronous()
	{
		return false;
	}
} /* __oOo__ */


/* __oOo__ */
