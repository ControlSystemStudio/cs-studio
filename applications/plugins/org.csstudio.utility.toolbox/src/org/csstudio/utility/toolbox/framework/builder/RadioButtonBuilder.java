package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.framework.property.Property;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class RadioButtonBuilder {

	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;

	private String text = "";
	private String layoutData = "";
	private Binder<?> binder;

	public RadioButtonBuilder(Composite composite, String property, Map<Property, Widget> properties,  Binder<?> binder) {
		this.composite = composite;
		this.property = new Property(property);
		this.properties = properties;
		this.binder = binder;
	}

	public RadioButtonBuilder text(String text) {
		this.text = text;
		return this;
	}

	public RadioButtonBuilder hint(String layoutData) {
		this.layoutData = layoutData;
		return this;
	}

	public Button build() {

		Button radio = new Button(composite, SWT.RADIO);
		radio.setText(text);
		if (!layoutData.isEmpty()) {
			radio.setLayoutData(layoutData);
		}

		properties.put(property, radio);
		
		binder.bindPropertyToRadioButton(property, radio);

		return radio;
	}
}