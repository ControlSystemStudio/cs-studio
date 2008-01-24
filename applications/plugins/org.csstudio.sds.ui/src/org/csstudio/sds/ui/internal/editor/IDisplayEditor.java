package org.csstudio.sds.ui.internal.editor;

import org.csstudio.sds.model.DisplayModel;

/**
 * TODO: <b>This is a temporary solution!</b> (shofer)<br>
 * This is a temporary solution meant as an abstraction of a locked and an
 * editable display editor.
 * 
 * @author Stefan Hofer
 * @version $Revision$
 * 
 */
public interface IDisplayEditor {

	/**
	 * Return the edited model.
	 * 
	 * @return The edited model.
	 */
	DisplayModel getDisplayModel();

}
