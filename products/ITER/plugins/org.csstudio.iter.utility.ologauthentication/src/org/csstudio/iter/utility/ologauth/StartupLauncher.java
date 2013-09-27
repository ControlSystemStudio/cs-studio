package org.csstudio.iter.utility.ologauth;

import org.eclipse.ui.IStartup;

/**
 * Waiting for user authentication.
 * 
 * @author Davy Dequidt (Sopra Group) - ITER
 * 
 */
public class StartupLauncher implements IStartup {

	@Override
	public void earlyStartup() {
		new OlogAuthAdapter();
	}
}
