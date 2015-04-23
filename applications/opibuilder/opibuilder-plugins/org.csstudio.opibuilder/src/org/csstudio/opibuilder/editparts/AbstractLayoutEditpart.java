/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.editparts;

import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**The Abstract editpart for layout widgets.
 * @author Xihui Chen
 *
 */
public abstract class AbstractLayoutEditpart extends AbstractBaseEditPart {

	@Override
	protected IFigure doCreateFigure() {
		LayoutterFigure figure = new LayoutterFigure(getExecutionMode());
		figure.setIcon(getIcon());
		return figure;
	}
	
	protected abstract Image getIcon();
	
	/**Get the new bounds after layout.
	 * @param widgetModelList the children widget models to be layout.
	 * @param containerBounds the bounds of the container which contains the widget models.
	 * @return
	 */
	public abstract List<Rectangle> getNewBounds(
			List<AbstractWidgetModel> widgetModelList, Rectangle containerBounds);
	
	/**Layout widgets.
	 * @param widgetModelList
	 * @param containerBounds
	 */
	public void layout(List<AbstractWidgetModel> widgetModelList, Rectangle containerBounds){
		int i=0;
		List<Rectangle> newBounds = getNewBounds(widgetModelList, containerBounds);
		for(AbstractWidgetModel model : widgetModelList){
			model.setBounds(newBounds.get(i));
			i++;
		}
	}
	
	/**
	 * Refresh container's layout.
	 */
	protected void refreshParentLayout(){
		if(getParent() instanceof AbstractContainerEditpart){
			((AbstractContainerEditpart)getParent()).layout();
		}
	}
	
	/**The figure for layout widgets.
	 * @author Xihui Chen
	 *
	 */
	static class LayoutterFigure extends Label{
		
		private ExecutionMode executionMode;
		
		public LayoutterFigure(ExecutionMode executionMode) {
			this.executionMode = executionMode;
			setVisible(true);
		}
		
		@Override
		public void setVisible(boolean visible) {
			super.setVisible(executionMode == ExecutionMode.EDIT_MODE);
		}
		
		@Override
		public Dimension getMinimumSize(int w, int h) {
			return new Dimension(16, 16);
		}
	}

}
