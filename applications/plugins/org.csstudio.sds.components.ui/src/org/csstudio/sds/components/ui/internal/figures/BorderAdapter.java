package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.uil.CustomMediaFactory;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;

/**
 * This adapter enriches <code>IFigure</code> instances with the abilities
 * that are defined by the <code>IBorderEquippedWidget</code> interface.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class BorderAdapter implements IBorderEquippedWidget {
	/**
	 * The enriched <code>IFigure</code> instance.
	 */
	private IFigure _figure;

	/**
	 * The border width.
	 */
	private int _borderWidth = 1;

	/**
	 * The border color.
	 */
	private Color _borderColor = CustomMediaFactory.getInstance().getColor(0,
			0, 0);

	/**
	 * The border style.
	 */
	private Integer _borderStyle = 1;

	/**
	 * Standard constructor.
	 * 
	 * @param figure
	 *            The enriched <code>IFigure</code> instance.
	 */
	public BorderAdapter(final IFigure figure) {
		_figure = figure;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBorderWidth(final int width) {
		_borderWidth = width;
		refreshBorder();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBorderColor(final Color borderColor) {
		_borderColor = borderColor;
		refreshBorder();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setBorderStyle(final int style) {
		_borderStyle = style;
		refreshBorder();
	}

	/**
	 * Refresh the border.
	 */
	private void refreshBorder() {
		LineBorder border = new LineBorder();
		border.setWidth(_borderWidth);
		border.setColor(_borderColor);
		switch (_borderStyle) {

		case 0:
			// TODO: Vary Border Style !
			break;
		default:
			// TODO: Vary Border Style !
			break;
		}
		_figure.setBorder(border);
	}
}
