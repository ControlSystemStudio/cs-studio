package org.csstudio.utility.toolbox.framework.builder;

import java.util.Map;

import org.csstudio.utility.toolbox.ToolboxPlugin;
import org.csstudio.utility.toolbox.framework.listener.SimpleSelectionListener;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class ButtonBuilder {
	
	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;
	
	private String text = "";
	private String hint = "";
	private SimpleSelectionListener listener;
	private boolean withSearchImage = false;
	private boolean defaultButton = false;

	private boolean disable = false;

	public ButtonBuilder(Composite composite, String property, Map<Property, Widget> properties) {
		this.composite = composite;
		this.property = new Property(property);
		this.properties = properties;
	}
	
	public ButtonBuilder listener(SimpleSelectionListener listener) {
		this.listener = listener;
		return this;
	}

	public ButtonBuilder hint(String hint) {
		this.hint = hint;
		return this;
	}

	public ButtonBuilder withSearchImage() {
		this.withSearchImage = true;
		return this;
	}

	public ButtonBuilder disable() {
		this.disable = true;
		return this;
	}

	public ButtonBuilder defaultButton() {
		this.defaultButton = true;
		return this;
	}

	public ButtonBuilder text(String text) {
		this.text = text;
		return this;
	}
	
	public Button build() {
	
		Button button = new Button(composite, SWT.PUSH);
		button.setText(text);
		
		if (listener != null) {
			button.addSelectionListener(listener);
		}
		
		if (withSearchImage) {
			ImageDescriptor imageDescriptor = ToolboxPlugin.getImageDescriptor("icons/viewmag.png");
			Image image = imageDescriptor.createImage();
			button.setImage(image);
			if (hint.isEmpty()) {
				button.setLayoutData("h 24!");				
			} else {
				button.setLayoutData(hint + ",h 24!");								
			}
		} else {
			button.setLayoutData(hint);
		}
		
		if (disable) {
			button.setEnabled(false);
		}
		
		if (defaultButton) {
			Shell shell = composite.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}

		properties.put(property, button);
		return button;
		
	}
	
}
