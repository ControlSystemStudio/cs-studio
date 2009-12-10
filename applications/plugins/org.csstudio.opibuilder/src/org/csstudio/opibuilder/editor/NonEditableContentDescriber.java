package org.csstudio.opibuilder.editor;

import java.io.IOException;
import java.io.InputStream;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;

public class NonEditableContentDescriber implements IContentDescriber {

	public int describe(InputStream contents, IContentDescription description)
			throws IOException {
		return PreferencesHelper.isNoEdit() ? VALID : INDETERMINATE;
	}

	public QualifiedName[] getSupportedOptions() {
		return null;
	}

}
