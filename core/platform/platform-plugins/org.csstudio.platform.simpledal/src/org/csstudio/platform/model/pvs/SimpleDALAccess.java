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
 package org.csstudio.platform.model.pvs;

import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.spi.PropertyFactory;

/**
 * Simple access to the control systems via DAL. (Proposed by Igor Kriznar)
 * 
 * @author jhatje
 * 
 */
public class SimpleDALAccess {

	private static PropertyFactory factory;

	public static DynamicValueProperty<?> getProperty(String name)
			throws RemoteException, InstantiationException {
		IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance()
				.createProcessVariableAdress(name);
		factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(pv.getControlSystem());

		DynamicValueProperty<?> property = factory.getProperty(name);

		return property;
	}

	/**
	 * Return a {@link DynamicValueProperty} via DAL-channel of given type.
	 * 
	 * @param <T>
	 *            The suggested type.
	 * @param channel
	 *            The name of the channel/property.
	 * @param suggestedDataType
	 *            The class of suggested data type of the property (see
	 *            {@link DataAccess#getDataType()}).
	 * @return The {@link DynamicValueProperty} of suggested type.
	 * @throws RemoteException ?
	 * @throws InstantiationException ?
	 * @throws ClassCastException
	 *             If suggested property type is not assignable from type of
	 *             channel property.
	 */
	public static <T> DynamicValueProperty<T> getProperty(String channel,
			Class<T> suggestedDataType) throws RemoteException,
			InstantiationException, ClassCastException {
		IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance()
				.createProcessVariableAdress(channel);
		factory = DALPropertyFactoriesProvider.getInstance()
				.getPropertyFactory(pv.getControlSystem());

		DynamicValueProperty<?> property = factory.getProperty(channel);
		if (suggestedDataType.isAssignableFrom(property.getDataType())) {
			return convertWildcardPropertyToTypedProperty(property);
		}

		throw new ClassCastException(
				"Suggested property type is not assignable from type of channel property!");
	}

	@SuppressWarnings("unchecked")
	private static <T> DynamicValueProperty<T> convertWildcardPropertyToTypedProperty(
			DynamicValueProperty<?> property) {
		return (DynamicValueProperty<T>) property;
	}

	public static void dispose(DynamicValueProperty<?> property) {
		if (property == null)
			return;
		factory.getPropertyFamily().destroy(property);
	}
}
