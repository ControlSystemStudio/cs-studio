package org.csstudio.askap.utility;

/**
 * This is essentially a stately Editor.
 */
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class AskapEditorInput implements IEditorInput, IPersistableElement {

	public static final String TITLE_KEY = "EditorInputTitle";
	private String title;

	public AskapEditorInput(String title) {
		this.title = title;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IPersistableElement getPersistable() {
		return this;
	}

	@Override
	public String getToolTipText() {
		return title;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AskapEditorInput) {
			if (((AskapEditorInput) obj).getName().equals(this.getName()))
				return true;
		}
						
		return false;
	}

	@Override
	public void saveState(IMemento memento) {
		memento.putString(TITLE_KEY, title);
	}

	@Override
	public String getFactoryId() {
		return ElementFactory.ID;
	}
	
}
