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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.data.values.IEnumeratedMetaData;
import org.csstudio.data.values.IValue;
import org.csstudio.opibuilder.editparts.AbstractPVWidgetEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.widgets.figures.ComboFigure;
import org.csstudio.opibuilder.widgets.model.ComboModel;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/**The editpart of a combo.
 *
 * @author Xihui Chen
 *
 */
public final class ComboEditPart extends AbstractPVWidgetEditPart {

	private PVListener loadItemsFromPVListener;

	private IEnumeratedMetaData meta = null;

	private Combo combo;
	private SelectionListener comboSelectionListener;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure doCreateFigure() {
		final ComboModel model = getWidgetModel();
		updatePropSheet(model.isItemsFromPV());
		ComboFigure comboFigure = new ComboFigure(this);
		combo = comboFigure.getSWTWidget();
		
		List<String> items = getWidgetModel().getItems();

		updateCombo(items);

		markAsControlPV(AbstractPVWidgetModel.PROP_PVNAME, AbstractPVWidgetModel.PROP_PVVALUE);

		return comboFigure;
	}

	/**
	 * @param items
	 */
	private void updateCombo(List<String> items) {
		if(items !=null && getExecutionMode() == ExecutionMode.RUN_MODE){
			combo.removeAll();

			for(String item : items){
				combo.add(item);
			}

			//write value to pv if pv name is not empty
			if(getWidgetModel().getPVName().trim().length() > 0){
				if(comboSelectionListener !=null)
					combo.removeSelectionListener(comboSelectionListener);
				comboSelectionListener = new SelectionAdapter(){
						@Override
						public void widgetSelected(SelectionEvent e) {
							setPVValue(AbstractPVWidgetModel.PROP_PVNAME, combo.getText());
						}
				};
				combo.addSelectionListener(comboSelectionListener);
			}

		}
	}

	@Override
	public ComboModel getWidgetModel() {
		return (ComboModel)getModel();
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		registerLoadItemsListener();
	}

	/**
	 *
	 */
	private void registerLoadItemsListener() {
		//load items from PV
		if(getExecutionMode() == ExecutionMode.RUN_MODE){
			if(getWidgetModel().isItemsFromPV()){
				PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
				if(pv != null){
					if(loadItemsFromPVListener == null)
						loadItemsFromPVListener = new PVListener() {
							public void pvValueUpdate(PV pv) {
								IValue value = pv.getValue();
								if (value != null && value.getMetaData() instanceof IEnumeratedMetaData){
									IEnumeratedMetaData new_meta = (IEnumeratedMetaData)value.getMetaData();
									if(meta  == null || !meta.equals(new_meta)){
										meta = new_meta;
										List<String> itemsFromPV = new ArrayList<String>();
										for(String writeValue : meta.getStates()){
											itemsFromPV.add(writeValue);
										}
										getWidgetModel().setPropertyValue(
												ComboModel.PROP_ITEMS, itemsFromPV);
									}
								}
							}
							public void pvDisconnected(PV pv) {}
						};
					pv.addListener(loadItemsFromPVListener);
				}
			}
		}
	}

	@Override
	protected void doDeActivate() {
		super.doDeActivate();
		if(getWidgetModel().isItemsFromPV()){
			PV pv = getPV(AbstractPVWidgetModel.PROP_PVNAME);
			if(pv != null && loadItemsFromPVListener !=null){
				pv.removeListener(loadItemsFromPVListener);
			}
		}
//		((ComboFigure)getFigure()).dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void registerPropertyChangeHandlers() {
		IWidgetPropertyChangeHandler pvNameHandler = new IWidgetPropertyChangeHandler() {

			public boolean handleChange(Object oldValue, Object newValue, IFigure figure) {
				registerLoadItemsListener();
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, pvNameHandler);


		autoSizeWidget((ComboFigure) getFigure());
		// PV_Value
		IWidgetPropertyChangeHandler pvhandler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null && newValue instanceof IValue){
					String stringValue = ValueUtil.getString((IValue)newValue);
					if(Arrays.asList(combo.getItems()).contains(stringValue))
						combo.setText(stringValue);
					else
						combo.deselectAll();
//					
//					if(getWidgetModel().isBorderAlarmSensitve())
//							autoSizeWidget((ComboFigure) refreshableFigure);
				}

				return true;
			}
		};
		setPropertyChangeHandler(ComboModel.PROP_PVVALUE, pvhandler);

		// Items
		IWidgetPropertyChangeHandler itemsHandler = new IWidgetPropertyChangeHandler() {
			@SuppressWarnings("unchecked")
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				if(newValue != null && newValue instanceof List){
					updateCombo((List<String>)newValue);
					if(getWidgetModel().isItemsFromPV())
						combo.setText(ValueUtil.getString(getPVValue(AbstractPVWidgetModel.PROP_PVNAME)));
				}
				return true;
			}
		};
		setPropertyChangeHandler(ComboModel.PROP_ITEMS, itemsHandler);

		final IWidgetPropertyChangeHandler handler = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure refreshableFigure) {
				updatePropSheet((Boolean) newValue);
				return false;
			}
		};
		getWidgetModel().getProperty(ComboModel.PROP_ITEMS_FROM_PV).
			addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					handler.handleChange(evt.getOldValue(), evt.getNewValue(), getFigure());
				}
		});


		//size change handlers--always apply the default height
		IWidgetPropertyChangeHandler handle = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue, final Object newValue,
					final IFigure figure) {
				autoSizeWidget((ComboFigure)figure);
				return true;
			}
		};
		setPropertyChangeHandler(AbstractWidgetModel.PROP_WIDTH, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_HEIGHT, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_STYLE, handle);
		setPropertyChangeHandler(AbstractWidgetModel.PROP_BORDER_WIDTH, handle);
		setPropertyChangeHandler(ComboModel.PROP_FONT, handle);
	}

		/**
		* @param actionsFromPV
		*/
	private void updatePropSheet(final boolean itemsFromPV) {
		getWidgetModel().setPropertyVisible(
				ComboModel.PROP_ITEMS, !itemsFromPV);
	}

	private void autoSizeWidget(ComboFigure comboFigure) {
		Dimension d = comboFigure.getAutoSizeDimension();
		getWidgetModel().setSize(getWidgetModel().getWidth(), d.height);
	}

	@Override
	public String getValue() {
		return combo.getText();
	}

	@Override
	public void setValue(Object value) {
		if(value instanceof String)
			combo.setText((String) value);
		else if (value instanceof Number)
			combo.select(((Number)value).intValue());
		else
			super.setValue(value);
	}

}
