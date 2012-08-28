package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.converter.BigDecimalToStringConverter;
import org.csstudio.utility.toolbox.framework.converter.DateToStringConverter;
import org.csstudio.utility.toolbox.framework.converter.NullToStringConverter;
import org.csstudio.utility.toolbox.framework.converter.StringToBigDecimalConverter;
import org.csstudio.utility.toolbox.framework.converter.StringToDateConverter;
import org.csstudio.utility.toolbox.framework.converter.StringToNullConverter;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.validator.DateValidator;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.google.inject.Inject;

public class Binder<T extends BindingEntity> {

	private DataBindingContext ctx;
	
	private GenericEditorInput<T> editorInput;
	
	private DirtyFlag dirtyFlag;
	
	private  boolean isSearchMode;
		
	@Inject
	private DateToStringConverter dateToStringConverter;

	@Inject
	private StringToDateConverter stringToDateConverter;

	@Inject
	private BigDecimalToStringConverter bigDecimalToStringConverter;

	@Inject
	private StringToBigDecimalConverter stringToBigDecimalConverter;

	@Inject
	private NullToStringConverter nullToStringConverter;

	@Inject
	private StringToNullConverter stringToNullConverter;
	
	public void init (GenericEditorInput<T> editorInput, Option<CrudController<T>> crudController, boolean isSearchMode) {
		this.ctx = new DataBindingContext();
		this.editorInput = editorInput;
		this.isSearchMode = isSearchMode;
		if (crudController.hasValue()) {
			this.dirtyFlag = crudController.get();
		} else {
			this.dirtyFlag = new DirtyFlag() {				
				@Override
				public void setDirty(boolean value) {
					throw new IllegalStateException("Trying to set dirty flag, but that is currently unexpected.");
				}
			};
		}		
	}
		
		
	public void bindPropertyToText(Property property, Text text, ControlDecoration controlDecoration,
				boolean useBigDecimalConverter) {

		UpdateValueStrategy targetToModel = new UpdateValueStrategy();
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		int event = SWT.Modify;

		if (editorInput.isDatePropertyField(property)) {
			event = SWT.FocusOut;
			modelToTarget.setConverter(dateToStringConverter);
			targetToModel.setConverter(stringToDateConverter);
			targetToModel.setAfterConvertValidator(new DateValidator(controlDecoration, new Func1Void<IStatus>() {
				@Override
				public void apply(IStatus status) {
					dirtyFlag.setDirty(true);
				}
			}));

		} else if (useBigDecimalConverter) {
			modelToTarget.setConverter(bigDecimalToStringConverter);
			targetToModel.setConverter(stringToBigDecimalConverter);
		}

		IObservableValue modelValue = editorInput.createObservableValueForProperty(property);
		IObservableValue widgetValue = WidgetProperties.text(event).observe(text);

		ctx.bindValue(widgetValue, modelValue, targetToModel, modelToTarget);
	}

	public void bindPropertyToCombo(Property property, Combo combo) {
		if (!isSearchMode) {
			IObservableValue modelValue = editorInput.createObservableValueForProperty(property);
			IObservableValue widgetValue = SWTObservables.observeText(combo);
			
			UpdateValueStrategy targetToModel = new UpdateValueStrategy();
			UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
			
			targetToModel.setConverter(stringToNullConverter);
			modelToTarget.setConverter(nullToStringConverter);
			
			ctx.bindValue(widgetValue, modelValue);
		}
	}

	public void bindPropertyToCheckbox(Property property, Button checkbox) {
		if (!isSearchMode) {
			IObservableValue widgetValue = WidgetProperties.selection().observe(checkbox);
			IObservableValue modelValue = editorInput.createObservableValueForProperty(property);
			ctx.bindValue(widgetValue, modelValue);
		}
	}

	public void bindPropertyToRadioButton(Property property, Button radioButton) {
		if (!isSearchMode) {
			IObservableValue widgetValue = WidgetProperties.selection().observe(radioButton);
			IObservableValue modelValue = editorInput.createObservableValueForProperty(property);
			ctx.bindValue(widgetValue, modelValue);
		}
	}

	public void updateModels() {
		ctx.updateModels();
	}
	
	public void replaceBindings(Map<Property, Widget> properties, T data) {

		this.ctx.dispose();
		this.ctx = new DataBindingContext();
					
		editorInput.setData(data);

		for (Map.Entry<Property, Widget> entry : properties.entrySet()) {

			Property property = entry.getKey();
			Widget widget = entry.getValue();

			Boolean noBinding = (Boolean) widget.getData(BuilderConstant.NO_BINDING);

			if ((noBinding != null) && (!noBinding)) {
				
				if (widget instanceof Text) {
					
					Object controlDecoratorion = widget.getData(BuilderConstant.DECORATOR);

					Boolean booleanValue = (Boolean) ((Text) widget).getData(BuilderConstant.USE_BIG_DECIMAL_CONVERTER);
					boolean useBigDecimalConverter = false;

					if (booleanValue != null) {
						useBigDecimalConverter = booleanValue;
					}

					if (controlDecoratorion != null) {
						bindPropertyToText(property, (Text) widget, (ControlDecoration) controlDecoratorion,
									useBigDecimalConverter);
					} else {
						bindPropertyToText(property, (Text) widget, null, useBigDecimalConverter);
					}

				} else if (widget instanceof Combo) {
					bindPropertyToCombo(property, (Combo) widget);
				}
				
			}

		}		
		this.ctx.updateTargets();
	}

}
