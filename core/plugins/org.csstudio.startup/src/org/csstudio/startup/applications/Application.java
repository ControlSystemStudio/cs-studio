package org.csstudio.startup.applications;

import org.csstudio.platform.CSSPlatformPlugin;
import org.csstudio.platform.LocaleService;
import org.csstudio.platform.security.AuthenticationService;
import org.csstudio.platform.ui.dialogs.LoginDialog;
import org.csstudio.platform.ui.workbench.CssWorkbenchAdvisor;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The RCP application for the Control System Studio.
 * 
 * @author awill
 * @version $Revision$
 * 
 */
public class Application implements IPlatformRunnable {
	/**
	 * {@inheritDoc}
	 */
	public final Object run(final Object args) throws Exception {
		IPreferenceStore coreStore = new ScopedPreferenceStore(
				new InstanceScope(), CSSPlatformPlugin.getDefault().getBundle()
						.getSymbolicName());

		applyLocaleSetting(coreStore);
		
		Display display = PlatformUI.createDisplay();
		
		try {
			boolean canLogin = handleLogin(display, coreStore);

			int returnCode = EXIT_OK;

			if (canLogin) {
				// create the workbench with this advisor and run it until it
				// exits
				// N.B. createWorkbench remembers the advisor, and also
				// registers
				// the workbench globally so that all UI plug-ins can find it
				// using
				// PlatformUI.getWorkbench() or AbstractUIPlugin.getWorkbench()
				returnCode = PlatformUI.createAndRunWorkbench(display,
						new CssWorkbenchAdvisor());

				// the workbench doesn't support relaunch yet (bug 61809) so
				// for now restart is used, and exit data properties are checked
				// here to substitute in the relaunch return code if needed
				if (returnCode != PlatformUI.RETURN_RESTART) {
					return EXIT_OK;
				}

				// if the exit code property has been set to the relaunch code,
				// then
				// return that code now, otherwise this is a normal restart
				return EXIT_RESTART;
			}

			return EXIT_OK;
		} finally {
			if (display != null) {
				display.dispose();
			}
		}
	}

	/**
	 * Open the platform's login dialog if the according system property is set.
	 * 
	 * @param display
	 *            The standard display.
	 * @param coreStore
	 *            The core preference store.
	 * @return true, if the authentication succeeded.
	 */
	private boolean handleLogin(final Display display,
			final IPreferenceStore coreStore) {
		boolean result = true;

		boolean performAuthentication = coreStore
				.getBoolean(AuthenticationService.PROP_AUTH_LOGIN);

		if (performAuthentication) {
			LoginDialog d = new LoginDialog(display.getActiveShell());
			int ret = d.open();

			if (ret == Dialog.CANCEL) {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Set the system's default locate according to the CSS settings.
	 * 
	 * @param coreStore
	 *            The core preference store.
	 */
	private void applyLocaleSetting(final IPreferenceStore coreStore) {
		String locale = coreStore.getString(LocaleService.PROP_LOCALE);
		LocaleService.setSystemLocale(locale);
	}
}
