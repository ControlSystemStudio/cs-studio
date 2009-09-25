package org.csstudio.diag.diles.dnd;

import org.csstudio.diag.diles.model.ModelFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

public class FlowchartDropTargetListener extends
		TemplateTransferDropTargetListener {
	
	public FlowchartDropTargetListener(EditPartViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.TemplateTransferDropTargetListener#getFactory(java.lang.Object)
	 */
	@Override
	protected CreationFactory getFactory(Object template) {
		return new ModelFactory(template);
	}
}