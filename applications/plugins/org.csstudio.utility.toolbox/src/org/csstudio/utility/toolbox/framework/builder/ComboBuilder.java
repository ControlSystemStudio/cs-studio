package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.Property.PropertyNameHint;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class ComboBuilder extends AbstractControlWithLabelBuilder<ComboBuilder> {

	private final Map<Property, AbstractListViewer> viewers;
	private final Binder<?> binder;
	private final boolean isSearchMode;

	private WritableList observableData = null;

	private boolean isJoined = false;
	private boolean selectFirst = false;
	private Object selectObject = null;
	private Property accessor = null;

	public ComboBuilder(Composite composite, String property, Map<Property, Widget> properties,
				GenericEditorInput<?> editorInput, Map<Property, AbstractListViewer> viewers, Binder<?> binder,
				boolean isSearchMode) {
		super(composite, property, properties, editorInput, SearchTermType.STRING);
		this.viewers = viewers;
		this.binder = binder;
		this.isSearchMode = isSearchMode;
		notEditable();
	}

	public ComboBuilder isJoined() {
		this.isJoined = true;
		return this;
	}

	public ComboBuilder selectFirst() {
		this.selectFirst = true;
		return this;
	}

	public ComboBuilder select(Object object) {
		this.selectObject = object;
		return this;
	}

	public ComboBuilder data(WritableList data, Property accessor) {
		this.observableData = data;
		this.accessor = accessor;
		return this;
	}

	private int calculateStyle() {
		int style = SWT.SINGLE | SWT.BORDER;
		if ((!isSearchMode) && (!isEditable())) {
			style = style | SWT.READ_ONLY;
		}
		return style;
	}

	private static class SimpleLabelProvider extends LabelProvider {
		public String getText(Object element) {
			TextValue textValue = (TextValue) element;
			return textValue.getValue();
		}
	}

	private void setData(ComboViewer comboViewer) {
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setUseHashlookup(true);
		comboViewer.setInput(getData());
		comboViewer.setLabelProvider(new SimpleLabelProvider());
		if (selectObject != null) {
			int index = getData().indexOf(selectObject);
			if (index != -1) {
				comboViewer.getCombo().select(index);
			}
		}
	}

	private void setObservableData(ComboViewer comboViewer) {
		if (accessor == null) {
			throw new IllegalStateException("Accesor must not be null.");
		}
		ViewerSupport.bind(comboViewer, observableData, BeanProperties.values(new String[] { accessor.getName() }));
		if (observableData.size() == 1) {
			comboViewer.getCombo().select(0);
		} else {
			if (selectObject != null) {
				int index = observableData.indexOf(selectObject);
				if (index != -1) {
					comboViewer.getCombo().select(index);
				}
			}
		}
	}

	public Combo build() {

		if (getLabel() != null) {
			buildLabel();
		}

		int style = calculateStyle();

		Combo combo = new Combo(getComposite(), style);
		combo.setLayoutData(getLayoutData());
		combo.pack();

		ComboViewer comboViewer = new ComboViewer(combo);

		if (getData() != null) {
			setData(comboViewer);
		} else if (observableData != null) {
			setObservableData(comboViewer);
		}

		if (!isNoBinding()) {
			if (isJoined) {
				getProperty().setHint(PropertyNameHint.SubQueryOnly);
			}
			binder.bindPropertyToCombo(getProperty(), comboViewer.getCombo());
		}

		// set flag if we use binding or not
		combo.setData(BuilderConstant.NO_BINDING, Boolean.valueOf(isNoBinding()));

		if (selectFirst) {
			if (combo.getSelectionIndex() == -1) {
				combo.select(0);
			}
		}
		
		getProperties().put(getProperty(), combo);
		viewers.put(getProperty(), comboViewer);

		getProperty().setType(SearchTermType.STRING_SEARCH_EXACT);

		return combo;

	}
}