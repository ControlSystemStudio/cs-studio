/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.multistate;

import org.csstudio.swt.widgets.datadefinition.IManualStringValueChangeListener;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.eclipse.draw2d.IFigure;

/**
 * @author Fred Arnaud (Sopra Group)
 */
public class ControlMultiSymbolEditPart extends CommonMultiSymbolEditPart {

	public ControlMultiSymbolModel getWidgetModel() {
		return (ControlMultiSymbolModel) getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		// create & initialize the view properly (edit or runtime mode)
		final ControlMultiSymbolFigure figure = new ControlMultiSymbolFigure(this);
		super.initializeCommonFigureProperties(figure);

		ControlMultiSymbolModel model = getWidgetModel();
		figure.setShowConfirmDialog(model.getShowConfirmDialog());
		figure.setConfirmTip(model.getConfirmTip());
		figure.setPassword(model.getPassword());
		figure.addManualValueChangeListener(new IManualStringValueChangeListener() {
			public void manualValueChanged(final String newValue) {
				if (getExecutionMode() == ExecutionMode.RUN_MODE) {
					setPVValue(AbstractPVWidgetModel.PROP_PVNAME, newValue.trim());
				}
			}
		});
		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);
		return figure;
	}
	
	/**
	 * Configures a listener for performing a {@link AbstractWidgetActionModel}.
	 * 
	 * @param figure The figure of the widget
	 */
	private void configureButtonListener(final ControlMultiSymbolFigure figure) {
		figure.addManualValueChangeListener(new IManualStringValueChangeListener() {
			public void manualValueChanged(String newValue) {
				// If the display is not in run mode, don't do anything.
				if (getExecutionMode() != ExecutionMode.RUN_MODE)
					return;

				int actionIndex = getWidgetModel().getPushActionIndex();
				if (actionIndex >= 0
						&& getWidgetModel().getActionsInput().getActionsList()
								.size() > actionIndex)
					getWidgetModel().getActionsInput().getActionsList()
							.get(actionIndex).run();
			}
		});
	}
	
	/**
	 * Registers property change handlers for the properties defined in
	 * {@link AbstractBoolWidgetModel}. This method is provided for the
	 * convenience of subclasses, which can call this method in their
	 * implementation of {@link #registerPropertyChangeHandlers()}.
	 */
	protected void registerPropertyChangeHandlers() {
		super.registerCommonPropertyChangeHandlers();
		configureButtonListener((ControlMultiSymbolFigure) getFigure());

		// show confirm dialog
		IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ControlMultiSymbolFigure figure = (ControlMultiSymbolFigure) refreshableFigure;
				figure.setShowConfirmDialog(getWidgetModel()
						.getShowConfirmDialog());
				return true;
			}
		};
		setPropertyChangeHandler(ControlMultiSymbolModel.PROP_CONFIRM_DIALOG,
				handler);

		// confirm tip
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ControlMultiSymbolFigure figure = (ControlMultiSymbolFigure) refreshableFigure;
				figure.setConfirmTip((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ControlMultiSymbolModel.PROP_CONFIRM_TIP,
				handler);

		// password
		handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				ControlMultiSymbolFigure figure = (ControlMultiSymbolFigure) refreshableFigure;
				figure.setPassword((String) newValue);
				return true;
			}
		};
		setPropertyChangeHandler(ControlMultiSymbolModel.PROP_PASSWORD, handler);
	}

}
