package org.csstudio.platform.ui.dnd;

import org.eclipse.swt.dnd.DropTargetAdapter;

/**
 * Empty default implementation of the <code>ICssDropTargetListener</code>
 * interface. Clients will only have to implement the <code>doDrop()</code>
 * operation.
 * 
 * @author Alexander Will
 * 
 */
public abstract class CssDropTargetAdapter extends DropTargetAdapter implements
		ICssDropTargetListener {
}
