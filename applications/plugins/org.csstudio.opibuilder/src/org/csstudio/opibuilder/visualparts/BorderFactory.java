package org.csstudio.opibuilder.visualparts;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.SchemeBorder.Scheme;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**The factory to create borders for {@link IFigure}
 * @author Xihui Chen
 *
 */
public class BorderFactory {

	public static AbstractBorder createBorder(BorderStyle style, int width, RGB rgbColor){
		Color color = CustomMediaFactory.getInstance().getColor(rgbColor);
		
		switch (style) {
		case LINE:
			return createLineBorder(SWT.LINE_SOLID, width, color);
		case RAISED:
			return createSchemeBorder(SchemeBorder.SCHEMES.RAISED);
		case LOWERED: 
			return createSchemeBorder(SchemeBorder.SCHEMES.LOWERED);	
		case ETCHED:
			return createSchemeBorder(SchemeBorder.SCHEMES.ETCHED);
		case RIDGED:
			return createSchemeBorder(SchemeBorder.SCHEMES.RIDGED);		
		case BUTTON_RAISED: 
			return createSchemeBorder(SchemeBorder.SCHEMES.BUTTON_CONTRAST);
		case BUTTON_PRESSED: 
			return createSchemeBorder(SchemeBorder.SCHEMES.BUTTON_PRESSED);	
		case DASH_DOT:
			return createLineBorder(SWT.LINE_DASHDOT, width, color);
		case DASHED:
			return createLineBorder(SWT.LINE_DASH, width, color);
		case DOTTED:
			return createLineBorder(SWT.LINE_DOT, width, color);			
		case DASH_DOT_DOT:
			return createLineBorder(SWT.LINE_DASHDOTDOT, width, color);
		case NONE:			
		default:
			return null;
		}
	}
	
	/**
	 * Creates a LineBorder.
	 * 
	 * @return AbstractBorder The requested Border
	 */
	private static AbstractBorder createLineBorder(int style, int width, Color color) {
		if (width>0) {
			VersatileLineBorder border = new VersatileLineBorder(style, color, width);
			return border;	
		}
		return null;
	}
	/**
	 * Creates a SchemeBorder.
	 * @param scheme the scheme for the {@link SchemeBorder}
	 * @return AbstractBorder The requested Border
	 */
	private static AbstractBorder createSchemeBorder(final Scheme scheme) {
		SchemeBorder border = new SchemeBorder(scheme);
		return border;
	}
	
}
