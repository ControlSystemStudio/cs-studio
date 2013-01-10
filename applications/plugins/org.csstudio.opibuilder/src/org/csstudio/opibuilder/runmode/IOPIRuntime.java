package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**The common interface for OPI runtime, which could be an editor or view.
 * @author Xihui Chen
 *
 */
public interface IOPIRuntime extends IWorkbenchPart, IAdaptable{

	/**Set workbench part name. It calls setPartName() from editor or view to make it
	 * public visible.
	 * @param name
	 */
	public void setWorkbenchPartName(String name);
	
	/**Set the OPI input. The OPI Runtime will reload OPI from the input.
	 * @param input
	 * @throws PartInitException
	 */
	public void setOPIInput(IEditorInput input) throws PartInitException;
	
	/**
	 * @return the OPI input of the runtime.
	 */
	public IEditorInput getOPIInput();
	
	/**
	 * @return the display model in this runtime.
	 */
	public DisplayModel getDisplayModel();
	
}
