package org.csstudio.iter.passwordprovider;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

/** PasswordProvider for
 *  extension point org.eclipse.equinox.security.secureStorage.
 *  Generates a password without requiring user input
 *  @author Sopra Group
 */
public class ITERPasswordProvider extends PasswordProvider {
	@SuppressWarnings("nls")
    @Override
	public PBEKeySpec getPassword(IPreferencesContainer container,
			int passwordType) {	
		//the master password cannot include spaces
		String installLoc = Platform.getInstallLocation().getURL().toString().replaceAll("\\s", "");
		return new PBEKeySpec(installLoc.toCharArray());
	}

}
