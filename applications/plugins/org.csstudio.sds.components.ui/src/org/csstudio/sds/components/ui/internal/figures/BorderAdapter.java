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
import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.SchemeBorder;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * This adapter enriches <code>IFigure</code> instances with the abilities
 * that are defined by the <code>IBorderEquippedWidget</code> interface.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public class BorderAdapter implements IBorderEquippedWidget {
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
	private Color _borderColor = CustomMediaFactory.getInstance().getColor(0, 0, 0);

	/**
	 * The border style.
	 */
	private Integer _borderStyle = 1;

	/**
	 * The text for the border.
	 */
	private String _borderText;

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
	public final void setBorderWidth(final int width) {
		_borderWidth = width;
		refreshBorder();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setBorderColor(final Color borderColor) {
		_borderColor = borderColor;
		refreshBorder();
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setBorderStyle(final int style) {
		_borderStyle = style;
		refreshBorder();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final void setBorderText(final String borderText) {
		_borderText = borderText;
		refreshBorder();
	}

	/**
	 * Refresh the border.
	 */
	private void refreshBorder() {
		if (_borderWidth > 0) {
			AbstractBorder border;
			switch (_borderStyle) {
				case 0 : border = this.createLineBorder(); break;
				case 1 : border = this.createLabeledBorder(); break;
				case 2 : border = this.createSchemeBorder(); break;
				case 3 : border = this.createStriatedBorder(); break;
				case 4 : border = this.createShapeBorder(_borderWidth, _borderColor); break;
				default : border = this.createLineBorder(); break;
			}
			_figure.setBorder(border);
			//_figure.repaint();
		} else {
			_figure.setBorder(null);
		}
	}
	
	/**
	 * Creates a LineBorder.
	 * @return AbstractBorder
	 * 			The requested Border
	 */
	private AbstractBorder createLineBorder() {
		LineBorder border = new LineBorder();
		border.setWidth(_borderWidth);
		border.setColor(_borderColor);
		return border;
	}
	
	/**
	 * Creates a AbstractLabeledBorder.
	 * @return AbstractBorder
	 * 			The requested Border
	 */
	private AbstractBorder createLabeledBorder() {
		AbstractLabeledBorder border = new TitleBarBorder(_borderText);
		return border;
	}
	
	/**
	 * Creates a SchemeBorder.
	 * @return AbstractBorder
	 * 			The requested Border
	 */
	private AbstractBorder createSchemeBorder() {
		SchemeBorder border = new SchemeBorder(SchemeBorder.SCHEMES.RAISED);
		return border;
	}
	
	/**
	 * Creates a StriatedBorder.
	 * @return AbstractBorder
	 * 			The requested Border
	 */
	private AbstractBorder createStriatedBorder() {
		StriatedBorder border = new StriatedBorder(_borderWidth);
		border.setBorderColor(_borderColor);
		return border;
	}
	
	/**
	 * Creates a ShapedBorder.
	 * @return AbstractBorder
	 * 			The requested Border
	 */
	protected AbstractBorder createShapeBorder(final int borderWidth, final Color borderColor) {
		LineBorder border = new LineBorder();
		border.setWidth(borderWidth);
		border.setColor(borderColor);
		return border;
	}
	
	/**
	 * A striated Border.
	 * @author Kai Meyer
	 */
	private final class StriatedBorder extends AbstractBorder {
		
		/**
		 * The insets for this Border.
		 */
		private Insets _insets;
		/**
		 * The Height of the Border.
		 */
		private int _borderHeight;
		/**
		 * The Width of the Border.
		 */
		private final int _fixBorderWide = 5;
		/**
		 * The Color of the border.
		 */
		private Color _borderColor;
		
		/**
		 * Constructor.
		 * @param borderWidth
		 * 		The width of the Border
		 */
		public StriatedBorder(final int borderWidth) {
			_insets = new Insets(borderWidth);
			_borderHeight = borderWidth;
		}
		
		/**
		 * Sets the Color of the border.
		 * @param borderColor
		 * 			The Color for the border
		 */
		public void setBorderColor(final Color borderColor) {
			_borderColor = borderColor;
		}

		/**
		 * {@inheritDoc}
		 */
		public Insets getInsets(final IFigure figure) {
			return _insets;
		}

		/**
		 * {@inheritDoc}
		 */
		public void paint(final IFigure figure, final Graphics graphics, final Insets insets) {
			Rectangle bounds = figure.getBounds();
			graphics.setForegroundColor(_borderColor);
			graphics.setBackgroundColor(_borderColor);
			graphics.setLineStyle(SWT.LINE_DOT);
			graphics.setLineWidth(_borderHeight);
			
			graphics.drawLine(bounds.x, bounds.y+_borderHeight/2,
					bounds.x+bounds.width/2 ,bounds.y+_borderHeight/2);
			graphics.drawLine(bounds.x+bounds.width, bounds.y+_borderHeight/2,
					bounds.x+bounds.width/2 ,bounds.y+_borderHeight/2);
			graphics.drawLine(bounds.x+bounds.width-_borderHeight/2, bounds.y,
					bounds.x+bounds.width-_borderHeight/2, bounds.y+bounds.height/2);
			graphics.drawLine(bounds.x+bounds.width-_borderHeight/2, bounds.y+bounds.height,
					bounds.x+bounds.width-_borderHeight/2, bounds.y+bounds.height/2);
			graphics.drawLine(bounds.x, bounds.y+bounds.height-_borderHeight/2,
					bounds.x+bounds.width/2, bounds.y+bounds.height-_borderHeight/2);
			graphics.drawLine(bounds.x+bounds.width, bounds.y+bounds.height-_borderHeight/2,
					bounds.x+bounds.width/2, bounds.y+bounds.height-_borderHeight/2);
			graphics.drawLine(bounds.x+_borderHeight/2, bounds.y,
					bounds.x+_borderHeight/2, bounds.y+bounds.height/2);
			graphics.drawLine(bounds.x+_borderHeight/2, bounds.y+bounds.height,
					bounds.x+_borderHeight/2, bounds.y+bounds.height/2);
			
			graphics.setLineStyle(SWT.LINE_SOLID);
		}
		
	}

}
