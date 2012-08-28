package org.csstudio.utility.toolbox.framework.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

public abstract class AbstractControlWithLabelBuilder<T> {

	public static final String CONTENT_PROPOSAL_PROVIDER = "CONTENT_PROPOSAL_PROVIDER";

	private final Composite composite;
	private final Property property;
	private final Map<Property, Widget> properties;
	private final GenericEditorInput<?> editorInput;
	
	private String label;
	private String layoutData = "";
	private String layoutDataLabel = "";
	private boolean noBinding = false;
	private int style = SWT.NONE;
	private boolean readOnly = false;
	private String message;
	private boolean editable = true;
	private boolean searchExact = false;
	
	private List<? extends TextValue> data = null;

	AbstractControlWithLabelBuilder(Composite composite, String property, Map<Property, Widget> properties,
				GenericEditorInput<?> editorInput, SearchTermType type) {
		this.composite = composite;
		this.property = new Property(property);
		this.property.setType(type);
		this.properties = properties;
		this.editorInput = editorInput;
	}
	
	AbstractControlWithLabelBuilder(Composite composite, Property property,
				Map<Property, Widget> properties, GenericEditorInput<?> editorInput, SearchTermType type) {
		this.composite = composite;
		this.property = property;
		this.property.setType(type);
		this.properties = properties;
		this.editorInput = editorInput;
	}

	@SuppressWarnings("unchecked")
	public T label(String label, String layoutDataLabel) {
		this.label = label;
		this.layoutDataLabel = layoutDataLabel;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T data(List<? extends TextValue> data) {
		this.data = data;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T emptyData() {
		this.data = new ArrayList<TextValue>();
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T label(String label) {
		this.label = label;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T message(String text) {
		this.message = text;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T hint(String layoutData) {
		this.layoutData = layoutData;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T noBinding() {
		this.noBinding = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T searchExact() {
		this.searchExact = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T readOnly() {
		this.readOnly = true;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T style(int style) {
		this.style = style;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T notEditable() {
		editable = false;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T editable() {
		editable = true;
		return (T) this;
	}

	protected void setStyle(int style) {
		this.style = style;
	}

	protected void buildLabel() {
		LabelBuilder lb = new LabelBuilder(composite, getEditorInput());
		lb.text(label);
		lb.hint(layoutDataLabel);
		if ((!noBinding) && (editorInput.isRequiredProperty(property))) {
			lb.bold();
		}
		lb.build();
	}

	public List<? extends TextValue> getData() {
		return data;
	}
	
	public Composite getComposite() {
		return composite;
	}

	public Property getProperty() {
		return property;
	}

	public String getLabel() {
		return label;
	}

	public String getLayoutData() {
		return layoutData;
	}

	public String getLayoutDataLabel() {
		return layoutDataLabel;
	} 

	public boolean isNoBinding() {
		return noBinding;
	}
	
	public boolean isSearchExact() {
		return searchExact;
	}
	
	public GenericEditorInput<?> getEditorInput() {
		return editorInput;
	}
	
	public String getMessage() {
		return message;
	}

	protected int getStyle() {
		return style;
	}

	public Map<Property, Widget> getProperties() {
		return properties;
	}
	
	public boolean isReadOnly() {
		return readOnly; 
	}

	protected boolean isEditable() {
		return editable; 
	}

	public boolean isReadOnlyStyle(int style) {
		return ((style & SWT.READ_ONLY) == SWT.READ_ONLY); 
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}
}
