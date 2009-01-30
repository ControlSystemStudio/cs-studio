package org.csstudio.sns.passwordprovider;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

public class SNSPasswordProvider extends PasswordProvider {

	public SNSPasswordProvider() {
		
	}

	@Override
	public PBEKeySpec getPassword(IPreferencesContainer container,
			int passwordType) {
		String userdir; // the directory where the program is installed
		userdir = System.getProperty("user.dir"); //$NON-NLS-1$		
		return new PBEKeySpec(userdir.toCharArray());
	}

}
