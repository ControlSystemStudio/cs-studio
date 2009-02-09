package org.csstudio.sns.passwordprovider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

public class SNSPasswordProvider extends PasswordProvider {

	public SNSPasswordProvider() {
		
	}

	@Override
	public PBEKeySpec getPassword(IPreferencesContainer container,
			int passwordType) {	
		
		String installLoc = Platform.getInstallLocation().getURL().toString();
		return new PBEKeySpec(installLoc.toCharArray());
	}

}
