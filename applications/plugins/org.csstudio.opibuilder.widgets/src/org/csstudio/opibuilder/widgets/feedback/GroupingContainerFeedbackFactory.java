/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.feedback;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.feedback.DefaultGraphicalFeedbackFactory;
import org.csstudio.opibuilder.widgets.editparts.GroupingContainerEditPart;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.tools.DragEditPartsTracker;
import org.eclipse.swt.SWT;

/**Feedback Factory for Grouping Contianer
 * @author Xihui Chen
 *
 */
public class GroupingContainerFeedbackFactory extends DefaultGraphicalFeedbackFactory {


	@Override
	public List<Handle> createCustomHandles(GraphicalEditPart editPart) {
		
		if(editPart instanceof GroupingContainerEditPart && 
				((GroupingContainerEditPart)editPart).getWidgetModel().isLocked()){
			Handle handle = new LockIndicatorHandle(editPart);
			return Arrays.asList(handle);
		}
		return super.createCustomHandles(editPart);
	}
	
	
	private final class LockIndicatorHandle extends AbstractHandle{

		private static final String LOCKED = "Locked";

		public LockIndicatorHandle(final GraphicalEditPart owner) {
			super(owner, new Locator() {
				
				@Override
				public void relocate(IFigure target) {
					IFigure ownerFigure = owner.getFigure();
					Dimension preferedSize = target.getPreferredSize();
					Rectangle targetBounds = ownerFigure.getBounds().getCopy();
					ownerFigure.translateToAbsolute(targetBounds);
					target.translateToRelative(targetBounds);	
					targetBounds.expand(preferedSize.height,preferedSize.height);
					target.setBounds(targetBounds);
				}
			});
		}
		
		@Override
		protected DragTracker createDragTracker() {
			DragEditPartsTracker tracker = new DragEditPartsTracker(getOwner());
			tracker.setDefaultCursor(getCursor());
			return tracker;
		}
		
		@Override
		protected void paintFigure(Graphics graphics) {
			graphics.setForegroundColor(ColorConstants.lightBlue);			
			Dimension textExtents = TextUtilities.INSTANCE.getTextExtents(LOCKED, getFont());
			graphics.drawText(LOCKED, getLocation().translate(textExtents.height, 0));
			graphics.setLineStyle(SWT.LINE_DOT);
			graphics.drawRectangle(getBounds().getCopy().shrink(textExtents.height-3,textExtents.height-3));
			
		}
		
		@Override
		public Dimension getPreferredSize(int wHint, int hHint) {			
			return TextUtilities.INSTANCE.getTextExtents(LOCKED, getFont());
		}
		
		
		
	}
	
}
