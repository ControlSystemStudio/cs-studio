package org.csstudio.diag.diles.figures;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

abstract public class ActivityFigure extends Figure {
	Rectangle r = new Rectangle();
	Hashtable targetAnchors = new Hashtable();
	Hashtable sourceAnchors = new Hashtable();
	String message = new String();

	public String getSourceAnchorName(ConnectionAnchor c) {
		Enumeration enumeration = sourceAnchors.keys();
		String name;
		while (enumeration.hasMoreElements()) {
			name = (String) enumeration.nextElement();
			if (sourceAnchors.get(name).equals(c))
				return name;
		}
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchor(String name) {
		return (ConnectionAnchor) sourceAnchors.get(name);
	}

	public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;
		Enumeration e = getSourceConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public Hashtable getSourceConnectionAnchors() {
		return sourceAnchors;
	}

	public String getTargetAnchorName(ConnectionAnchor c) {
		Enumeration enumeration = targetAnchors.keys();
		String name;
		while (enumeration.hasMoreElements()) {
			name = (String) enumeration.nextElement();
			if (targetAnchors.get(name).equals(c))
				return name;
		}
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(String name) {
		return (ConnectionAnchor) targetAnchors.get(name);
	}

	public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
		ConnectionAnchor closest = null;
		long min = Long.MAX_VALUE;
		Enumeration e = getTargetConnectionAnchors().elements();
		while (e.hasMoreElements()) {
			ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
			Point p2 = c.getLocation(null);
			long d = p.getDistance2(p2);
			if (d < min) {
				min = d;
				closest = c;
			}
		}
		return closest;
	}

	public Hashtable getTargetConnectionAnchors() {
		return targetAnchors;
	}

	public void setName(String msg) {
		message = msg;
		repaint();
	}
}