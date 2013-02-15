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

import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.AbstractOpenOPIAction;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgets.figures.NativeButtonFigure;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

/**
 * EditPart controller delegate for Native Button widget. 
 * @author Xihui Chen
 * 
 */
public final class NativeButtonEditPartDelegate implements IButtonEditPartDelegate{
	  
	private Button button;
	private ActionButtonEditPart editpart;

	public NativeButtonEditPartDelegate(ActionButtonEditPart editPart) {
		this.editpart = editPart;
	}
	
	public IFigure doCreateFigure() {
		ActionButtonModel model = editpart.getWidgetModel();
		int style=SWT.None;
		style|= model.isToggleButton()?SWT.TOGGLE:SWT.PUSH;
		style |= SWT.WRAP;
		final NativeButtonFigure buttonFigure = 
				new NativeButtonFigure(editpart, style);
		button = buttonFigure.getSWTWidget();
		button.setText(model.getText());
		buttonFigure.setImagePath(model.getImagePath());
		return buttonFigure;
	}
	
	
	
	public void hookMouseClickAction() {
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<AbstractWidgetAction> actions = editpart.getHookedActions();
				if(actions!= null){
					for(AbstractWidgetAction action: actions){
						if(action instanceof AbstractOpenOPIAction){
							((AbstractOpenOPIAction) action).setCtrlPressed(false);
							((AbstractOpenOPIAction) action).setShiftPressed(false);
							if((e.stateMask & SWT.CTRL) !=0){
								((AbstractOpenOPIAction) action).setCtrlPressed(true);
							}else if ((e.stateMask & SWT.SHIFT) !=0){
								((AbstractOpenOPIAction) action).setShiftPressed(true);
							}	
						}
						action.run();
					}					
				}		
			}
		});
		
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void registerPropertyChangeHandlers() {

		// text
		IWidgetPropertyChangeHandler textHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				button.setText(newValue.toString());
				button.setSize(button.getSize());
				return true;
			}
		};
		editpart.setPropertyChangeHandler(ActionButtonModel.PROP_TEXT, textHandler);


		//image
		IWidgetPropertyChangeHandler imageHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				NativeButtonFigure figure = (NativeButtonFigure) refreshableFigure;				
				IPath absolutePath = (IPath)newValue;
				if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
					absolutePath = ResourceUtil.buildAbsolutePath(
							editpart.getWidgetModel(), absolutePath);
				figure.setImagePath(absolutePath);
				return true;
			}
		};
		editpart.setPropertyChangeHandler(ActionButtonModel.PROP_IMAGE, imageHandler);		
	
		// button style
		final IWidgetPropertyChangeHandler buttonStyleHandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {			
				editpart.updatePropSheet((Boolean) newValue);
				return true;
			}			
		};
		editpart.getWidgetModel().getProperty(ActionButtonModel.PROP_TOGGLE_BUTTON).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					buttonStyleHandler.handleChange(evt.getOldValue(), evt.getNewValue(), editpart.getFigure());
				}
			});		
	}
		
	
	public void setValue(Object value) {
		button.setText(value.toString());
	}
	
	public Object getValue() {
		return button.getText();
	}

	@Override
	public void deactivate() {
		
	}

	@Override
	public boolean isSelected() {
		return button.getSelection();
	}
}
