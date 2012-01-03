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
import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.swt.widgets.figures.ActionButtonFigure;
import org.csstudio.swt.widgets.figures.ActionButtonFigure.ButtonActionListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link ActionButtonFigure2}.
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public final class ActionButtonEditPart extends AbstractPVWidgetEditPart {
	  
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ActionButtonModel model = getWidgetModel();

		final ActionButtonFigure buttonFigure = new ActionButtonFigure(getExecutionMode() == ExecutionMode.RUN_MODE);
		buttonFigure.setText(model.getText());
		buttonFigure.setToggleStyle(model.isToggleButton());
		buttonFigure.setImagePath(model.getImagePath());
		updatePropSheet(model.isToggleButton());	
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return buttonFigure;
	}
	
	@Override
	protected void hookMouseClickAction() {

		((ActionButtonFigure)getFigure()).addActionListener(new ButtonActionListener(){
			public void actionPerformed(int mouseEventState) {					
				List<AbstractWidgetAction> actions = getHookedActions();
				if(actions!= null){
					for(AbstractWidgetAction action: actions){
						if(action instanceof AbstractOpenOPIAction){
							((AbstractOpenOPIAction) action).setCtrlPressed(false);
							((AbstractOpenOPIAction) action).setShiftPressed(false);
							if(mouseEventState == SWT.CONTROL){
								((AbstractOpenOPIAction) action).setCtrlPressed(true);
							}else if (mouseEventState == SWT.SHIFT){
								((AbstractOpenOPIAction) action).setShiftPressed(true);
							}	
						}
						action.run();
					}					
				}							
			}
		});
	}
	
	@Override
	public List<AbstractWidgetAction> getHookedActions() {
		int actionIndex;
		
		if(getWidgetModel().isToggleButton()){
			if(((ActionButtonFigure)getFigure()).isSelected()){
				actionIndex = getWidgetModel().getActionIndex();
			}else
				actionIndex = getWidgetModel().getReleasedActionIndex();
		}else
			actionIndex = getWidgetModel().getActionIndex();
				
		ActionsInput actionsInput = getWidgetModel().getActionsInput();
		if(actionsInput.getActionsList().size() <=0)
			return null;
		if(actionsInput.isHookUpAllActionsToWidget())
			return actionsInput.getActionsList();
		
		if(actionIndex >= 0 && actionsInput.getActionsList().size() > actionIndex){
			return getWidgetModel().getActionsInput().
						getActionsList().subList(actionIndex, actionIndex +1);			
		}
		
		if(actionIndex == -1)
			return actionsInput.getActionsList();
		
		return null;
			
	}

	@Override
	public ActionButtonModel getWidgetModel() {
		return (ActionButtonModel)getModel();
	}
	
	@Override
	public void deactivate() {		
		((ActionButtonFigure)getFigure()).dispose();
		super.deactivate();
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {

		// text
		IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				figure.setText(newValue.toString());
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);


		//image
		IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;				
				IPath absolutePath = (IPath)newValue;
				if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							getWidgetModel(), absolutePath);
				figure.setImagePath(absolutePath);
				return true;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);		
	
		// button style
		final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ActionButtonFigure figure = (ActionButtonFigure) refreshableFigure;
				figure.setToggleStyle((Boolean) newValue);				
				updatePropSheet((Boolean) newValue);
				return true;
			}

			
		};
		getWidgetModel().getProperty(ActionButtonModel.PROP_TOGGLE_BUTTON).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					buttonStyleHandler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
			});		
	}
	
	/**
		* @param newValue
		*/
	private void updatePropSheet(final boolean newValue) {
		getWidgetModel().setPropertyVisible(
					ActionButtonModel.PROP_RELEASED_ACTION_INDEX, newValue);
		getWidgetModel().setPropertyDescription(ActionButtonModel.PROP_ACTION_INDEX, 
					newValue ? "Push Action Index" : "Click Action Index" );
	}
	
	
	@Override
	public void setValue(Object value) {
		((ActionButtonFigure)getFigure()).setText(value.toString());
	}
	
	@Override
	public Object getValue() {
		return ((ActionButtonFigure)getFigure()).getText();
	}

}
