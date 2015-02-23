package org.eclipse.jface.viewers;

import org.eclipse.swt.widgets.Event;

/**
 * 
 * <code>StyledCellLabelProvider</code> is a dummy class that allows for compilation
 * of the sources on the RAP target. This class is not intended to provide any
 * proper functionality as does its non-RAP counterpart.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class StyledCellLabelProvider extends CellLabelProvider {

	public static final int COLORS_ON_SELECTION = 1 << 0;
	public static final int NO_FOCUS = 1 << 1;

	public StyledCellLabelProvider() {
		this(0);
	}

	public StyledCellLabelProvider(int style) {

	}
	
	protected void paint(final Event event, final Object element) {
		//
	}
	

	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		
	}
	
}
