package org.csstudio.opibuilder.widgets.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**Figure for a Combo.
 * @author Xihui Chen
 *
 */
public class ComboFigure extends RectangleFigure {

	private Label label;
	
	private Triangle selector;
	
	private static final int SELECTOR_WIDTH = 20;
	private static final int MARGIN = 3;
	public ComboFigure() {
		label = new Label();
		selector = new Triangle();	
		selector.setDirection(PositionConstants.SOUTH);
		selector.setFill(true);
		add(label);
		add(selector);
		setOutline(false);
	}
	
	
	public void setText(String text){
		label.setText(text);
	}
	
	@Override
	public void setForegroundColor(Color fg) {
		super.setForegroundColor(fg);
		selector.setBackgroundColor(fg);
	}
	
	@Override
	public void setBackgroundColor(Color bg) {
		super.setBackgroundColor(bg);
		selector.setBackgroundColor(getForegroundColor());
	}
	
	@Override
	protected void layout() {
		super.layout();
		Rectangle clientArea = getClientArea();
		selector.setBounds(new Rectangle(clientArea.x + clientArea.width - SELECTOR_WIDTH,
				clientArea.y, SELECTOR_WIDTH, clientArea.height));
		label.setBounds(new Rectangle(clientArea.x + MARGIN, clientArea.y,
				clientArea.width - SELECTOR_WIDTH, clientArea.height));
	}
	
	
	
	
	
}
