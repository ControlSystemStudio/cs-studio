package org.csstudio.startuphelper.extensions.impl;

import java.util.Map;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.LocaleService;
import org.csstudio.startuphelper.extensions.LocaleSettingsExtPoint;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * 
 * <code>DefaultLocaleSettingsExtPoint</code> set the system's default locale 
 * according to the {@link CSSPlatformPlugin}'s default bundle.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultLocaleSettingsExtPoint implements LocaleSettingsExtPoint {

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.LocaleSettingsExtPoint#applyLocaleSettings(org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object applyLocaleSettings(IApplicationContext context, Map<String, Object> parameters) throws Exception {
		IPreferenceStore coreStore = new ScopedPreferenceStore(
				new InstanceScope(), CSSPlatformPlugin.getDefault().getBundle()
						.getSymbolicName());
		String locale = coreStore.getString(LocaleService.PROP_LOCALE);
		LocaleService.setSystemLocale(locale);
		return null;
	}
}
