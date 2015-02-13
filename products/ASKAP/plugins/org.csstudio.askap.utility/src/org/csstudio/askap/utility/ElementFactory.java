package org.csstudio.askap.utility;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class ElementFactory implements IElementFactory {

	public static final String ID = "org.csstudio.askap.utility.ElementFactory";
	
	@Override
	public IAdaptable createElement(IMemento memento) {
		String title = memento.getString(AskapEditorInput.TITLE_KEY);
		String tooltip = memento.getString(AskapEditorInput.TOOLTIP_KEY);
		return new AskapEditorInput(title, tooltip);
	}

}
