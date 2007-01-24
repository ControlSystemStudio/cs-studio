package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;

public class BorderAdapter implements IBorderEquippedWidget {
	private IFigure _figure;
	
	private int _borderWidth = 1;

	private Color _borderColor = CustomMediaFactory.getInstance().getColor(0,
			0, 0);

	private Integer _borderStyle = 1;

	public BorderAdapter(IFigure figure) {
		_figure = figure;
	}

	public void setBorderWidth(int width) {
		_borderWidth = width;
		refreshBorder();
	}

	public void setBorderColor(Color borderColor) {
		_borderColor = borderColor;
		refreshBorder();
	}

	public void setBorderStyle(int style) {
		_borderStyle = style;
		refreshBorder();
	}

	private void refreshBorder() {
		LineBorder border = new LineBorder();
		border.setWidth(_borderWidth);
		border.setColor(_borderColor);
		switch(_borderStyle) {
		
		case 0:
			//TODO: Vary Border Style !
			break;
		default:
			//TODO: Vary Border Style !
			break;
		}
		_figure.setBorder(border);
	}
}
