package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.editor.OPIEditor;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;

/**Cut widgets to clipboard.
 *@author Joerg Rathlev (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class CutWidgetsAction extends CopyWidgetsAction {

	private DeleteAction deleteAction;
	
	public CutWidgetsAction(OPIEditor part, DeleteAction deleteAction) {
		super(part);
		this.deleteAction = deleteAction;
		setText("Cut");
		setActionDefinitionId("org.eclipse.ui.edit.cut"); //$NON-NLS-1$
		setId(ActionFactory.CUT.getId());
		ISharedImages sharedImages = 
			part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages
        .getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
	}
	
	@Override
	public void run() {
		super.run();
		deleteAction.run();
	}

}
