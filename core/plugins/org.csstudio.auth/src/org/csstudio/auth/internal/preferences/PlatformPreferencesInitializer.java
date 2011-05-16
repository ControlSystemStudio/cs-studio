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
package org.csstudio.auth.internal.preferences;

import org.csstudio.auth.internal.AuthActivator;
import org.csstudio.auth.internal.subnet.OnsiteSubnetPreferences;
import org.csstudio.auth.security.SecurityFacade;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.Preferences;

/**
 * Preference initializer implementation. This class initializes all core
 * preferences. New preference settings should be initialized in this class,
 * too.
 *
 * @author Jan Hatje, Jörg Penning
 */
public final class PlatformPreferencesInitializer extends
		AbstractPreferenceInitializer {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences node = new DefaultScope()
				.getNode(AuthActivator.ID);

		initializeSystemPropertyPreferences(node);
		initializeOnsitePreferences(node);
		initializeAuthenticationPreferences(node);
	}


	/**
	 * Initializes the preferences for the onsite networks.
	 * @param node the preferences node to use.
	 */
	private void initializeOnsitePreferences(IEclipsePreferences node) {
		node.put(OnsiteSubnetPreferences.PREFERENCE_KEY, "131.169.0.0/255.255.0.0,");
	}

	/**
	 * Initializes preferences for system property defaults.
	 * @param node the preferences node to use.
	 */
	@SuppressWarnings("nls")
    private void initializeSystemPropertyPreferences(
			final IEclipsePreferences node) {
		Preferences propNode = node.node("systemProperties");
		propNode.put("java.security.krb5.realm", "DESY.DE");
		propNode.put("java.security.krb5.kdc", "kdc1.desy.de:kdc2.desy.de:kdc3.desy.de");
	}
	
	/**
	 * Initializes all preference settings for the authentication mechanism.
	 *
	 * @param node
	 *            the preferences node to use
	 */
	private void initializeAuthenticationPreferences(
			final IEclipsePreferences node) {
		node.put(SecurityFacade.ONSITE_LOGIN_PREFERECE, "true"); //$NON-NLS-1$
		node.put(SecurityFacade.OFFSITE_LOGIN_PREFERENCE, "false"); //$NON-NLS-1$
	}
	
}
