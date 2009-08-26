/**
 * 
 */
package org.csstudio.config.ioconfig.config.view.helper;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

/**
 * @author hrickens
 *
 */
public class GSDLabelProvider extends LabelProvider implements ILabelProvider,
		IFontProvider, IColorProvider {

	
	private final boolean _master;
	private final Color _gray = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
	private final Color _red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

	/**
	 * 
	 * @param master is only true if preferred master GSD file to be displayed. 
	 */
	public GSDLabelProvider(boolean master) {
		_master = master;
		
	}

	/**
	 * {@inheritDoc}
	 */
	public Font getFont(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Color getBackground(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Color getForeground(Object element) {
		if (element instanceof GSDFile) {
			GSDFile file = (GSDFile) element;
			if((_master && file.isMasterNonHN())||(!_master && file.isSlaveNonHN())) {
				return null;
			}
			if(file.isMasterNonHN()||file.isSlaveNonHN()) {
			    return _gray;
			}
		}
		return _red;
	}

}
