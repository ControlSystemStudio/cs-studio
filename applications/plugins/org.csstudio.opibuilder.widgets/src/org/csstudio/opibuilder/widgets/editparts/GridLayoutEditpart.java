/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.util.Arrays;
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractLayoutEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.Activator;
import org.csstudio.opibuilder.widgets.model.GridLayoutModel;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**The editpart for grid layout widgets.
 * @author Xihui Chen
 *
 */
public class GridLayoutEditpart extends AbstractLayoutEditpart {
	
	@Override
	public GridLayoutModel getWidgetModel() {
		return (GridLayoutModel)getModel();
	}
	
	@Override
	protected Image getIcon() {
		return CustomMediaFactory.getInstance().getImageFromPlugin(
				Activator.PLUGIN_ID, "icons/grid.gif");
	}

	@Override
	public List<Rectangle> getNewBounds(
			List<AbstractWidgetModel> widgetModelList, Rectangle containerBounds) {
		int numColumns = getWidgetModel().getNumberOfColumns();
		int numRows = (int) Math.ceil(widgetModelList.size() / (float)numColumns);
		int numChildren = widgetModelList.size();
		int[] maxWidths = new int[numColumns];
		int[] maxHeights = new int[numRows];
		
		int temp;
	
		//fill max widths
		for(int c= 0; c<numColumns; c++){
			for(int r = 0; r < numRows; r++){
				if(r*numColumns + c >= numChildren)
					continue;
				temp = widgetModelList.get(r*numColumns + c).getWidth();
				if(maxWidths[c] < temp){
					maxWidths[c] = temp;
				}
			}
		}
		
		//fill max heights
		for(int r = 0; r < numRows; r++){
			for(int c= 0; c<numColumns; c++){
				if(r*numColumns + c >= numChildren)
					continue;
				temp = widgetModelList.get(r*numColumns + c).getHeight();
				if(maxHeights[r] < temp){
					maxHeights[r] = temp;
				}
			}
		}
		int gap = getWidgetModel().getGridGap();
		boolean fill = getWidgetModel().isFillGrids();
		Rectangle[] newBounds = new Rectangle[numChildren];
		
		//get bounds
		for(int c= 0; c<numColumns; c++){
			for(int r = 0; r < numRows; r++){
				int index = r*numColumns + c;
				if( index >= numChildren)
					continue;
				int x = sumSubArray(c-1, maxWidths, gap);
				int y = sumSubArray(r-1, maxHeights, gap);
				int w = fill ? maxWidths[c] : widgetModelList.get(index).getWidth();
				int h = fill ? maxHeights[r] : widgetModelList.get(index).getHeight();
				newBounds[index]=new Rectangle(x, y, w, h);
			}
		}
		
		return Arrays.asList(newBounds);
	}
	
	private int sumSubArray(int index, int[] array, int gap){
		if(array.length ==0)
			return 0;
		if(index >=array.length)
			index = array.length -1;
		int sum =0;
		for(int i=0; i<=index; i++){
			sum += array[i] + gap;
		}
		return sum;
	}

	@Override
	protected void registerPropertyChangeHandlers() {
//		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
//			
//			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
//				refreshParentLayout();
//				return false;
//			}
//		};
//		setPropertyChangeHandler(GridLayoutModel.PROP_FILL_GRIDS, handler);
//		setPropertyChangeHandler(GridLayoutModel.PROP_NUMBER_OF_COLUMNS, handler);
//		setPropertyChangeHandler(GridLayoutModel.PROP_GRID_GAP, handler);
		
	}

}
