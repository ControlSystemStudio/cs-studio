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

package org.epics.css.dal.directory;

public class DALDescriptorImpl extends Attributes implements DALDescriptor
{
	private static final long serialVersionUID = 8667590130691133207L;

	public DALDescriptorImpl()
	{
		super();
	}

	public DALDescriptorImpl(DescriptorType type, String name, Class ctype)
	{
		super();
		putAttributeValue(DESCRIPTOR_TYPE, type);
		putAttributeValue(NAME, name);
		putAttributeValue(CLASS_TYPE, ctype);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.directory.DALDescriptor#getName()
	 */
	public String getName()
	{
		return (String)getAttributeValue(NAME);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.directory.DALDescriptor#getClassType()
	 */
	public Class getClassType()
	{
		return (Class)getAttributeValue(CLASS_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.directory.DALDescriptor#getDescriptorType()
	 */
	public DescriptorType getDescriptorType()
	{
		return (DescriptorType)getAttributeValue(DESCRIPTOR_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.directory.DALDescriptor#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		put(NAME, name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.directory.DALDescriptor#setClassType(java.lang.Class)
	 */
	public void setClassType(Class type)
	{
		put(CLASS_TYPE, type);
	}
}

/* __oOo__ */
