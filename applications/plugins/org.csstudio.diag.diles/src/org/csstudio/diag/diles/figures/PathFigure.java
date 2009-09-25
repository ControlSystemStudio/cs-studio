package org.csstudio.diag.diles.figures;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoutingAnimator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class PathFigure extends PolylineConnection {
	Label label;

	public PathFigure() {
		setConnectionRouter(new BendpointConnectionRouter());
		setAntialias(1);
		// setTargetDecoration(new PolygonDecoration());

		label = new Label();
		Font font = new Font(Display.getCurrent(), "Arial", 9, SWT.BOLD);
		setFont(font);

		label.setBackgroundColor(ColorConstants.yellow);
		label.setForegroundColor(ColorConstants.blue);
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));

		addRoutingListener(RoutingAnimator.getDefault());

	}

	/**
	 * Prints true or false in the middle of the path
	 * 
	 * @param s
	 *            the text
	 */
	public void setPathText(boolean s) {
		String text;
		if (s == true) {
			text = "TRUE";
		} else {
			text = "FALSE";
		}
		label.setText(text);
	}

}
