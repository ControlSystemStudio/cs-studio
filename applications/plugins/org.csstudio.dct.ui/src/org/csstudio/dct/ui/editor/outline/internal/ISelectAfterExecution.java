package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;

/**
 * Interface that can be implemented by commands to force the immediate
 * selection of new model elements in the outline after the command has been
 * executed.
 * 
 * @author Sven Wende
 * 
 */
public interface ISelectAfterExecution {
	/**
	 * Returns the element that should be selected in the outline view after the
	 * command has been executed.
	 * 
	 * @return the element to select
	 */
	IElement getElementToSelect();
}
