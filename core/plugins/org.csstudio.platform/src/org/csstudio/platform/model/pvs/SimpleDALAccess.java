package org.csstudio.platform.model.pvs;

import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.spi.PropertyFactory;

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
