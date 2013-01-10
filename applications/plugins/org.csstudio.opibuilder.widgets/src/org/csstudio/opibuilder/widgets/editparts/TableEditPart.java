/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.SpreadSheetTableFigure;
import org.csstudio.opibuilder.widgets.model.TableModel;
import org.csstudio.swt.widgets.natives.SpreadSheetTable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IActionFilter;

/**EditPart of Table widget.
 * @author Xihui Chen
 *
 */
public class TableEditPart extends AbstractBaseEditPart {
	
	private SpreadSheetTable spreadSheetTable;
	
	/**
	 * The cell under mouse when menu is triggered. point.x is row index. poing.y is column index.
	 */
	private Point menuTriggeredCell;

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#doCreateFigure()
	 */
	@Override
	protected IFigure doCreateFigure() {		
		SpreadSheetTableFigure figure = new SpreadSheetTableFigure(this);
		spreadSheetTable = figure.getSWTWidget();		
		spreadSheetTable.setContent(getWidgetModel().getDefaultContent());
		spreadSheetTable.setEditable(getWidgetModel().isEditable());
		spreadSheetTable.setColumnsCount(getWidgetModel().getColumnsCount());		
		spreadSheetTable.setColumnHeaders(
				getWidgetModel().getColumnHeaders());
		spreadSheetTable.setColumnWidths(getWidgetModel().getColumnWidthes());
		boolean editable[] = getWidgetModel().isColumnEditable();
		for(int i=0; i<Math.min(editable.length, spreadSheetTable.getColumnCount()); i++){
			spreadSheetTable.setColumnEditable(i, editable[i]);
		}
		
		spreadSheetTable.setColumnHeaderVisible(getWidgetModel().isColumnHeaderVisible());
				
		spreadSheetTable.getTableViewer().getTable().addMenuDetectListener(new MenuDetectListener() {
			
			@Override
			public void menuDetected(MenuDetectEvent e) {
				
				int[] index = spreadSheetTable.getRowColumnIndex(
						spreadSheetTable.getTableViewer().getTable().toControl(e.x, e.y));
				if(index != null)
					menuTriggeredCell = new Point(index[0], index[1]);
				else
					menuTriggeredCell = null;
			}
		});
		
		return figure;
	}
	
	/**Get the cell under mouse when menu is triggered. 
	 * @return the cell. point.x is row index. point.y is column index. null if no cell under mouse.
	 */
	public Point getMenuTriggeredCell() {
		return menuTriggeredCell;
	}
	
	@Override
	public TableModel getWidgetModel() {
		return (TableModel) super.getWidgetModel();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if (key == IActionFilter.class)
			return new BaseEditPartActionFilter(){
			@Override
			public boolean testAttribute(Object target, String name,
					String value) {
				if (name.equals("allowInsert") && value.equals("TRUE")) //$NON-NLS-1$ //$NON-NLS-2$						
					return spreadSheetTable.isEditable() && 
							(getMenuTriggeredCell() != null || spreadSheetTable.isEmpty());
				if (name.equals("allowDelete") && value.equals("TRUE")) //$NON-NLS-1$ //$NON-NLS-2$						
					return spreadSheetTable.isEditable() && 
							(getMenuTriggeredCell() != null);
				return super.testAttribute(target, name, value);
			}
		};
		return super.getAdapter(key);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.opibuilder.editparts.AbstractBaseEditPart#registerPropertyChangeHandlers()
	 */
	@Override
	protected void registerPropertyChangeHandlers() {

		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				spreadSheetTable.setEditable((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TableModel.PROP_EDITABLE, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				spreadSheetTable.setColumnHeaderVisible((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TableModel.PROP_COLUMN_HEADER_VISIBLE, handler);
		
		final IWidgetPropertyChangeHandler headersHandler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				String[] s = getWidgetModel().getColumnHeaders();
				spreadSheetTable.setColumnHeaders(s);
				int[] w = getWidgetModel().getColumnWidthes();
				spreadSheetTable.setColumnWidths(w);
				setPropertyValue(TableModel.PROP_COLUMNS_COUNT, s.length);
				getWidgetModel().updateContentPropertyTitles();
				return false;
			}
		};
		//update prop sheet immediately
		getWidgetModel().getProperty(TableModel.PROP_COLUMN_HEADERS).addPropertyChangeListener(
				new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				headersHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
			}
		});
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				String[][] headers = (String[][])getPropertyValue(TableModel.PROP_COLUMN_HEADERS);
				if(headers.length > (Integer)newValue){
					String[][] newHeaders =	Arrays.copyOf(headers, (Integer)newValue);
					setPropertyValue(TableModel.PROP_COLUMN_HEADERS, newHeaders);
				}
				spreadSheetTable.setColumnsCount((Integer)newValue);
				getWidgetModel().updateContentPropertyTitles();
				return false;
			}
		};
		setPropertyChangeHandler(TableModel.PROP_COLUMNS_COUNT, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			@Override
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				spreadSheetTable.setContent((String[][])newValue);
				return false;
			}
		};
		setPropertyChangeHandler(TableModel.PROP_DEFAULT_CONTENT, handler);
				
	}
	
	/**Get the native spread sheet table held by this widget.
	 * @return the native spread sheet table.
	 */
	public SpreadSheetTable getTable(){
		return spreadSheetTable;
	}

}
