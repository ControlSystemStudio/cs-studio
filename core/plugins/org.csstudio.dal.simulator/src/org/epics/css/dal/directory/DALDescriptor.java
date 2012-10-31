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

import javax.naming.directory.Attributes;


public interface DALDescriptor extends Attributes
{
	public static final String DESCRIPTOR_TYPE = "descriptorType";
	public static final String NAME = "name";
	public static final String CLASS_TYPE = "classType";

	/**
	 * Name of this descriptor.
	 * This value is available also as attribute, this method is for conveninence.
	 * @return name
	 */
	public String getName();

	/**
	 * Class of object, which si represented by this descriptor.
	 * This value is available also as attribute, this method is for conveninence.
	 * @return class of reperesnted type or object
	 */
	public Class getClassType();

	/**
	 * Returns type of this descriptor.
	 * This value is available also as attribute, this method is for conveninence.
	 * @return type of this descriptor
	 */
	public DescriptorType getDescriptorType();

	/**
	 * Sets descriptor name.
	 * @param name the name of descriptor
	 */
	public void setName(String name);

	/**
	 * Set type (class) of by this descriptor represented object.
	 * @param type the type of represented object
	 */
	public void setClassType(Class type);

	/**
	 * Returns value from attributes.
	 * @param attrId attribute name
	 * @return attribute value
	 */
	public Object getAttributeValue(String attrId);

	/**
	 * Sets attribute value.
	 * @param objId attribute name
	 * @param value attribute value
	 * @return of value already existed odl value, otherwise <code>null</code>
	 */
	public Object putAttributeValue(String objId, Object value);
}

/* __oOo__ */
