/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.editparts;

import java.util.List;

import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel;
import org.csstudio.opibuilder.widgets.model.ActionButtonModel.Style;
import org.csstudio.swt.widgets.figures.ITextFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

/**
 * EditPart controller for the ActioButton widget. The controller mediates
 * between {@link ActionButtonModel} and {@link ActionButtonFigure2}.
 * 
 * @author Sven Wende (class of same name in SDS)
 * @author Xihui Chen
 * 
 */
public class ActionButtonEditPart extends AbstractPVWidgetEditPart {

	private IButtonEditPartDelegate delegate;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		ActionButtonModel model = getWidgetModel();
		
		switch (model.getStyle()) {
		case NATIVE:
			this.delegate = new NativeButtonEditPartDelegate(this);
			break;
		case CLASSIC:
		default:
			this.delegate = new Draw2DButtonEditPartDelegate(this);
			break;
		}
		updatePropSheet(model.isToggleButton());	
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return delegate.doCreateFigure();
	}

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		if (getExecutionMode() == ExecutionMode.EDIT_MODE)
			installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
					new TextDirectEditPolicy());
	}

	@Override
	public void performRequest(Request request) {
		if (getExecutionMode() == ExecutionMode.EDIT_MODE
				&& (request.getType() == RequestConstants.REQ_DIRECT_EDIT || request
						.getType() == RequestConstants.REQ_OPEN))
			new TextEditManager(this, new LabelCellEditorLocator(getFigure()),
					false).show();
	}

	@Override
	protected void hookMouseClickAction() {
		delegate.hookMouseClickAction();
	}

	@Override
	public List<AbstractWidgetAction> getHookedActions() {
		ActionButtonModel widgetModel = getWidgetModel();
		boolean isSelected = delegate.isSelected();
		return getHookedActionsForButton(widgetModel, isSelected);

	}

	/**
	 * A shared static method for all button widgets.
	 * 
	 * @param widgetModel
	 * @param isSelected
	 * @return
	 */
	public static List<AbstractWidgetAction> getHookedActionsForButton(
			ActionButtonModel widgetModel, boolean isSelected) {
		int actionIndex;

		if (widgetModel.isToggleButton()) {
			if (isSelected) {
				actionIndex = widgetModel.getActionIndex();
			} else
				actionIndex = widgetModel.getReleasedActionIndex();
		} else
			actionIndex = widgetModel.getActionIndex();

		ActionsInput actionsInput = widgetModel.getActionsInput();
		if (actionsInput.getActionsList().size() <= 0)
			return null;
		if (actionsInput.isHookUpAllActionsToWidget())
			return actionsInput.getActionsList();

		if (actionIndex >= 0
				&& actionsInput.getActionsList().size() > actionIndex) {
			return widgetModel.getActionsInput().getActionsList()
					.subList(actionIndex, actionIndex + 1);
		}

		if (actionIndex == -1)
			return actionsInput.getActionsList();

		return null;
	}

	@Override
	public ActionButtonModel getWidgetModel() {
		return (ActionButtonModel) getModel();
	}

	@Override
	public void deactivate() {
		super.deactivate();
		delegate.deactivate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {

		IWidgetPropertyChangeHandler styleHandler = new IWidgetPropertyChangeHandler() {

			@Override
			public boolean handleChange(Object oldValue, Object newValue,
					IFigure figure) {
				AbstractWidgetModel model = getWidgetModel();
				WidgetDescriptor descriptor = 
						WidgetsService.getInstance().getWidgetDescriptor(model.getTypeID());
				String type = descriptor == null? model.getTypeID().substring(
						model.getTypeID().lastIndexOf(".")+1) :	descriptor.getName();
				model.setPropertyValue(AbstractWidgetModel.PROP_WIDGET_TYPE, type);
				AbstractContainerModel parent = model.getParent();
				parent.removeChild(model);
				parent.addChild(model);
				parent.selectWidget(model, true);
				return false;
			}
		};
		setPropertyChangeHandler(ActionButtonModel.PROP_STYLE, styleHandler);
		updatePropSheet(getWidgetModel().isToggleButton());	

		delegate.registerPropertyChangeHandlers();
	}

	/**
	 * @param newValue
	 */
	protected void updatePropSheet(final boolean newValue) {
		getWidgetModel().setPropertyVisible(
				ActionButtonModel.PROP_RELEASED_ACTION_INDEX, newValue);
		getWidgetModel().setPropertyDescription(
				ActionButtonModel.PROP_ACTION_INDEX,
				newValue ? "Push Action Index" : "Click Action Index");
	}

	@Override
	public void setValue(Object value) {
		delegate.setValue(value);
	}

	@Override
	public Object getValue() {
		return delegate.getValue();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class key) {
		if (key == ITextFigure.class)
			return getFigure();

		return super.getAdapter(key);
	}

}
