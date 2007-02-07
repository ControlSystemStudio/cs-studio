package org.csstudio.diag.rmtcontrol;

import java.io.File;

import org.csstudio.diag.rmtcontrol.Activator;
import org.csstudio.diag.rmtcontrol.Preference.SampleService;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(SampleService.RMT_XML_FILE_PATH,
				new File(Activator.getDefault().getStateLocation().toFile(),"rmt.xml").getAbsolutePath()); //$NON-NLS-1$
	}

}
