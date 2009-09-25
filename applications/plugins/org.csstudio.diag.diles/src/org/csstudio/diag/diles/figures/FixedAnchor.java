package org.csstudio.diag.diles.figures;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

public class FixedAnchor extends AbstractConnectionAnchor {
	int horizontal = 0, vertical = 0;

	public FixedAnchor(IFigure owner) {
		super(owner);
	}

	public Point getLocation(Point loc) {
		Rectangle r = getOwner().getBounds();
		int x = r.x + horizontal;
		int y = r.y + vertical;
		Point p = new PrecisionPoint(x, y);
		getOwner().translateToAbsolute(p);
		return p;
	}
}