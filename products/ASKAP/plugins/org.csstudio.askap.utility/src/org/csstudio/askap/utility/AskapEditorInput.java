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
	public static final String TOOLTIP_KEY = "EditorInputTooltip";
	private String title;
	private String tooltip;

	public AskapEditorInput(String title) {
		this.title = title;
	}
	
	public AskapEditorInput(String title, String tooltip) {
		this.title = title;
		this.tooltip = tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
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
		if (tooltip==null || tooltip.trim().length()==0)
			return title;
		
		return tooltip;
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
		memento.putString(TOOLTIP_KEY, tooltip);
	}

	@Override
	public String getFactoryId() {
		return ElementFactory.ID;
	}
	
}
