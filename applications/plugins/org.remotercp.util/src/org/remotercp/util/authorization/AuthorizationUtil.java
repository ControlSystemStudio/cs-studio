package org.remotercp.util.authorization;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ecf.core.identity.ID;
import org.remotercp.common.authorization.IOperationAuthorization;

/**
 * This helper class is responsible for processes regarding the extension
 * registry.
 * 
 * @author Eugen Reiswich
 * @date 27.10.2008
 * 
 */
public class AuthorizationUtil {

	/**
	 * This method checks whether a user is allowed to perform remote operations
	 * 
	 * @param fromId
	 *            The requesting user instance
	 * 
	 * @param methodId
	 *            The method the user wishes to perform
	 * 
	 * @return True, if user is allowed to perfrom remote operations, otherwise
	 *         false
	 */
	public static boolean checkAuthorization(ID fromId, String methodId) {
		boolean authorized = false;
		try {
			List<Object> executablesForExtensionPoint = AuthorizationUtil
					.getExecutablesForExtensionPoint("org.remotercp.authorization");
			if (executablesForExtensionPoint.isEmpty()) {
				/*
				 * no extension provided, ignore authorization
				 */
				authorized = true;
			} else {
				// authorization provided, check authorization
				for (Object executable : executablesForExtensionPoint) {
					if (executable instanceof IOperationAuthorization) {
						IOperationAuthorization operation = (IOperationAuthorization) executable;
						authorized = operation.canExecute(fromId, methodId);
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return authorized;
	}

	/**
	 * Returns the executable objects for a given extension point if any exist.
	 * 
	 * @param extensionPointId
	 *            The ID of the extension point
	 * @return List with executable objects
	 * @throws NullPointerException
	 *             , CoreException
	 */
	public static List<Object> getExecutablesForExtensionPoint(
			String extensionPointId) throws CoreException, NullPointerException {

		List<Object> executables = new ArrayList<Object>();

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(extensionPointId);
		if (extensionPoint == null) {
			throw new NullPointerException("No extension point found for id: "
					+ extensionPointId);
		} else {
			IConfigurationElement[] configurationElements = extensionPoint
					.getConfigurationElements();
			// are extensions available for the given extension point?
			if (configurationElements != null
					&& configurationElements.length > 0) {
				for (IConfigurationElement configurationElement : configurationElements) {
					Object ecutableExtension = configurationElement
							.createExecutableExtension("class");
					executables.add(ecutableExtension);
				}
			}
		}

		return executables;
	}
}
