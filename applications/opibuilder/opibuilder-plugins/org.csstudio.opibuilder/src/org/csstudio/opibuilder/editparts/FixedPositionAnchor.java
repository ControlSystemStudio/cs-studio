/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

/**The anchor is on a fixed position of the figure. 
 * For example, left, right, top left, ...
 * @author Xihui Chen
 *
 */
public class FixedPositionAnchor extends AbstractConnectionAnchor {

	public enum AnchorPosition{		
		TOP,	
		LEFT,
//		CENTER,
		RIGHT,
		BOTTOM,
		TOP_LEFT,
		TOP_RIGHT,	
		BOTTOM_LEFT,
		BOTTOM_RIGHT;
	}

	private AnchorPosition anchorPosition;	

	
	public FixedPositionAnchor(IFigure owner, AnchorPosition anchorPosition) {
		super(owner);
		this.anchorPosition = anchorPosition;
	}

	/**
	 * Returns the bounds of this ChopboxAnchor's owner. Subclasses can override
	 * this method to adjust the box the anchor can be placed on. For instance,
	 * the owner figure may have a drop shadow that should not be included in
	 * the box.
	 * 
	 * @return The bounds of this ChopboxAnchor's owner
	 * @since 2.0
	 */
	protected Rectangle getBox() {
		return getOwner().getBounds();
	}
	
	@Override
	public Point getReferencePoint() {
		return getLocation(null);
	}
	

	@Override
	public Point getLocation(Point reference) {
		Rectangle box = getBox();
		int x=box.x, y=box.y;
		switch (anchorPosition) {
		case BOTTOM:
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
			y=box.y + box.height;
			break;
//		case CENTER:
		case LEFT:
		case RIGHT:
			y=box.y + box.height/2;
			break;
		case TOP:
		case TOP_LEFT:
		case TOP_RIGHT:
			y=box.y;
			break;
		default:
			break;
		}
		
		switch (anchorPosition) {
		case LEFT:
		case BOTTOM_LEFT:
		case TOP_LEFT:
			x=box.x;
			break;
//		case CENTER:
		case TOP:
		case BOTTOM:
			x=box.x + box.width/2;
			break;
		case BOTTOM_RIGHT:
		case RIGHT:
		case TOP_RIGHT:
			x=box.x + box.width;
			break;
		default:
			break;
		}
		Point p= new Point(x, y);
		getOwner().translateToAbsolute(p);
		return p;
	}

	/**
	 * Returns <code>true</code> if the other anchor has the same owner and box.
	 * 
	 * @param obj
	 *            the other anchor
	 * @return <code>true</code> if equal
	 */
	public boolean equals(Object obj) {
		if (obj instanceof FixedPositionAnchor) {
			FixedPositionAnchor other = (FixedPositionAnchor) obj;
			return other.getOwner() == getOwner()
					&& other.getBox().equals(getBox()) &&
							other.anchorPosition == anchorPosition;
		}
		return false;
	}

	/**
	 * The owning figure's hashcode is used since equality is approximately
	 * based on the owner.
	 * 
	 * @return the hash code.
	 */
	public int hashCode() {
		if (getOwner() != null)
			return getOwner().hashCode()^(anchorPosition.ordinal()+31);
		else
			return super.hashCode();
	}
	
}
