package org.csstudio.utility.toolbox.guice.provider;

import org.eclipse.swt.widgets.Display;

import com.google.inject.Provider;

public class DisplayProvider implements Provider<Display> {

	@Override
	public Display get() {
		Display display = Display.getCurrent();
		// may be null if outside the UI thread
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

}
