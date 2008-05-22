package org.csstudio.nams.configurator.treeviewer;

import java.text.Collator;
import java.util.Locale;

import org.eclipse.jface.viewers.ViewerSorter;

public class ConfigurationSorter extends ViewerSorter {
	public ConfigurationSorter() {
		super(Collator.getInstance(Locale.getDefault()));
	}
}
