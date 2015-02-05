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

package org.csstudio.dal.context;

import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.device.DeviceCollection;
import org.csstudio.dal.group.GroupDataAccessProvider;


/**
 * Family is container for properties, it's contents can be changed only by
 * creating new property or deleting old one, yet this is not directly
 * implemented in the family but rather delegated to the underalying pluggable
 * implementation. In contrast to ordinary <code>PropertyCollection</code>
 * family is default container for properties, which cooperates with property
 * factories and manages lifecycle of properties.
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>
 */
public interface DeviceFamily<T extends AbstractDevice> extends DeviceCollection<T>,
	GroupDataAccessProvider
{
	/**
	 * Destroys and releases all properties and remote respurces and
	 * removes itself from paretn application context.
	 */
	public void destroyAll();

	/**
	 * If this family is owner of property, then destroys the property
	 * and releases all properties and remote respurces and removes it from
	 * itself.
	 *
	 * @param prop property
	 */
	public void destroy(T prop);

	/**
	 * Parent application context.
	 *
	 * @return parent application context
	 */
	public AbstractApplicationContext getApplicationContext();
} /* __oOo__ */


/* __oOo__ */
