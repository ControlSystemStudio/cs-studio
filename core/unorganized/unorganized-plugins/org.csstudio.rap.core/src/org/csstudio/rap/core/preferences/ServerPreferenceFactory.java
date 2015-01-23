package org.csstudio.rap.core.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScope;

public class ServerPreferenceFactory implements IScope{

	public IEclipsePreferences create(IEclipsePreferences parent, String name) {
		return new ServerPreferenceNode(parent, name);
	}

	
	
}
