/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.model.LabelModel;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.csstudio.swt.widgets.figures.TextFigure;
import org.csstudio.swt.widgets.figures.TextFigure.H_ALIGN;
import org.csstudio.swt.widgets.figures.TextFigure.V_ALIGN;
import org.csstudio.swt.widgets.figures.WrappableTextFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.widgets.Display;

/**The editpart for Label widget.
 * @author jbercic (class of same name in SDS)
 * @author Xihui Chen
 *
 */
public class LabelEditPart extends AbstractWidgetEditPart {

	
	@Override
	protected IFigure doCreateFigure() {
		WrappableTextFigure labelFigure = new WrappableTextFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
		labelFigure.setOpaque(!getWidgetModel().isTransparent());
		labelFigure.setHorizontalAlignment(getWidgetModel().getHorizontalAlignment());
		labelFigure.setVerticalAlignment(getWidgetModel().getVerticalAlignment());
		labelFigure.setSelectable(determinSelectable());
		labelFigure.setText(getWidgetModel().getText());		
		labelFigure.setWrapWords(getWidgetModel().isWrapWords());
		labelFigure.setShowScrollbar(getWidgetModel().isShowScrollbar());
		return labelFigure;
	}
	
	@Override
	public void activate() {
		super.activate();
		if(getWidgetModel().isAutoSize()){
			getWidgetModel().setSize(((WrappableTextFigure)figure).getAutoSizeDimension());
			figure.revalidate();
		}
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if(getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new TextDirectEditPolicy());
		
	}
	
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				((WrappableTextFigure)figure).setText((String)newValue);
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize())
							getWidgetModel().setSize(((WrappableTextFigure)figure).getAutoSizeDimension());
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TEXT, handler);
	
		IWidgetPropertyChangeHandler clickableHandler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((WrappableTextFigure)figure).setSelectable(determinSelectable());
				return false;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ACTIONS, clickableHandler);
		setPropertyChangeHandler(LabelModel.PROP_TOOLTIP, clickableHandler);

		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					final IFigure figure) {
				Display.getCurrent().timerExec(10, new Runnable() {					
					public void run() {
						if(getWidgetModel().isAutoSize()){
							getWidgetModel().setSize(((WrappableTextFigure)figure).getAutoSizeDimension());
							figure.revalidate();
						}
					}
				});
				
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_FONT, handler);		
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handler);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((WrappableTextFigure)figure).setOpaque(!(Boolean)newValue);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_TRANSPARENT, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {				
				if((Boolean)newValue){
					getWidgetModel().setSize(((WrappableTextFigure)figure).getAutoSizeDimension());
					figure.revalidate();
				}
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_AUTOSIZE, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((WrappableTextFigure)figure).setHorizontalAlignment(H_ALIGN.values()[(Integer)newValue]);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ALIGN_H, handler);
		
		handler = new IWidgetPropertyChangeHandler(){
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				((WrappableTextFigure)figure).setVerticalAlignment(V_ALIGN.values()[(Integer)newValue]);
				return true;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_ALIGN_V, handler);
		
		handler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((WrappableTextFigure)figure).setWrapWords((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_WRAP_WORDS, handler);
	
		handler = new IWidgetPropertyChangeHandler() {
			
			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				((WrappableTextFigure)figure).setShowScrollbar((Boolean)newValue);
				return false;
			}
		};
		setPropertyChangeHandler(LabelModel.PROP_SHOW_SCROLLBAR, handler);
		
	}

	private void performDirectEdit(){
		new TextEditManager(this, new LabelCellEditorLocator((WrappableTextFigure)getFigure())).show();
	}
	
	@Override
	public void performRequest(Request request){
		if (getExecutionMode() == ExecutionMode.EDIT_MODE &&( 
				request.getType() == RequestConstants.REQ_DIRECT_EDIT || 
				request.getType() == RequestConstants.REQ_OPEN))
			performDirectEdit();
	}
	
	
	@Override
	public LabelModel getWidgetModel() {
		return (LabelModel)getModel();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class key) {
		if(key == ITextFigure.class)
			return ((TextFigure)getFigure());

		return super.getAdapter(key);
	}
	
	private boolean determinSelectable(){
		return !getWidgetModel().getActionsInput().getActionsList().isEmpty() ||
		getWidgetModel().getTooltip().trim().length() > 0;
	}

}
