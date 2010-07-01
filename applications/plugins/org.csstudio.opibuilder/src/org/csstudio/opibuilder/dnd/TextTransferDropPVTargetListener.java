package org.csstudio.opibuilder.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.dnd.TextTransfer;

/**The Drop PV target listener for text transfer.
 * @author Xihui Chen
 *
 */
public class TextTransferDropPVTargetListener extends AbstractDropPVTargetListener {

	public TextTransferDropPVTargetListener(EditPartViewer viewer) {
		super(viewer, TextTransfer.getInstance());
	}

	@Override
	protected String[] getPVNamesFromTransfer() {
		if(getCurrentEvent().data == null)
			return null;
		String text = (String)getCurrentEvent().data;
		String[] pvNames = text.trim().split("\\s+"); //$NON-NLS-1$
		return pvNames;
	}

}
