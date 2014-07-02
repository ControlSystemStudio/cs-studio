/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.passwordprovider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

public class ITERPasswordProvider extends PasswordProvider {

	@Override
	public PBEKeySpec getPassword(final IPreferencesContainer container,
			final int passwordType) {
		File keyFile = new File("/etc/opt/codac/css/css.key");
		BufferedReader reader = null;
		String key = "";
		try {
			reader = new BufferedReader(new FileReader(keyFile));
			key = reader.readLine();
			reader.close();
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE,
					"Error reading password: " + e.getMessage());
		}
		return new PBEKeySpec(key.toCharArray());
	}
}
