/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.components.ui.internal.figures;

import org.csstudio.sds.ui.figures.IBorderEquippedWidget;
import org.csstudio.sds.util.CustomMediaFactory;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.FocusBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.SimpleEtchedBorder;
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
		if (_borderWidth > 0) {
			LineBorder border = new LineBorder();

			border.setWidth(_borderWidth);
			border.setColor(_borderColor);
			// TODO: Vary Border Style !
			_figure.setBorder(border);
			_figure.repaint();
		} else {
			_figure.setBorder(null);
		}
	}

}
