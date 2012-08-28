package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class CheckboxBuilder {

	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;
	private final Binder<?> binder;

	private String text = "";
	private String layoutData = "";

	public CheckboxBuilder(Composite composite, String property, Map<Property, Widget> properties, Binder<?> binder) {
		this.composite = composite;
		this.property = new Property(property);
		this.properties = properties; 
		this.binder = binder;
	}

	public CheckboxBuilder text(String text) {
		this.text = text;
		return this;
	}

	public CheckboxBuilder hint(String layoutData) {
		this.layoutData = layoutData;
		return this;
	}

	public Button build() {

		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setText(text);
		if (!layoutData.isEmpty()) {
			checkbox.setLayoutData(layoutData);
		}

		property.setType(SearchTermType.BOOLEAN);
		properties.put(property, checkbox);
		binder.bindPropertyToCheckbox(property, checkbox);

		return checkbox;
	}
}