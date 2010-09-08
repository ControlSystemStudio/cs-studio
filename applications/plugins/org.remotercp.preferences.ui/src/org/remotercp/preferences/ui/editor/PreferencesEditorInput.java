package org.remotercp.preferences.ui.editor;

import java.util.SortedMap;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class PreferencesEditorInput implements IEditorInput {

	private final SortedMap<String, String> preferences;

	private ID userId;

	public PreferencesEditorInput(SortedMap<String, String> preferences,
			ID userId) {
		this.preferences = preferences;
		this.userId = userId;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	public String getName() {
		return "Preference Editor";
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return "Edit Remote Preferences";
	}

	@Override
	public int hashCode() {
		return preferences.hashCode();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public SortedMap<String, String> getPreferences() {
		return preferences;
	}

	public ID getUserId() {
		return userId;
	}

}
