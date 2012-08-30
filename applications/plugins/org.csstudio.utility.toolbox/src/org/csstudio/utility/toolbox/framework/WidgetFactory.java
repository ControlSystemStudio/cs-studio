package org.csstudio.utility.toolbox.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.Path;

import org.apache.commons.lang.StringUtils;
import org.csstudio.utility.toolbox.AppLogger;
import org.csstudio.utility.toolbox.common.Environment;
import org.csstudio.utility.toolbox.framework.binding.BindingEntity;
import org.csstudio.utility.toolbox.framework.binding.TextValue;
import org.csstudio.utility.toolbox.framework.builder.AbstractControlWithLabelBuilder;
import org.csstudio.utility.toolbox.framework.builder.Binder;
import org.csstudio.utility.toolbox.framework.builder.BuilderConstant;
import org.csstudio.utility.toolbox.framework.builder.ButtonBuilder;
import org.csstudio.utility.toolbox.framework.builder.CheckboxBuilder;
import org.csstudio.utility.toolbox.framework.builder.ComboBuilder;
import org.csstudio.utility.toolbox.framework.builder.DateBuilder;
import org.csstudio.utility.toolbox.framework.builder.LabelBuilder;
import org.csstudio.utility.toolbox.framework.builder.RadioButtonBuilder;
import org.csstudio.utility.toolbox.framework.builder.TableViewerBuilder;
import org.csstudio.utility.toolbox.framework.builder.TextBuilder;
import org.csstudio.utility.toolbox.framework.controller.CrudController;
import org.csstudio.utility.toolbox.framework.editor.GenericEditorInput;
import org.csstudio.utility.toolbox.framework.property.Property;
import org.csstudio.utility.toolbox.framework.property.SearchTermType;
import org.csstudio.utility.toolbox.framework.proposal.TextValueProposalProvider;
import org.csstudio.utility.toolbox.framework.searchterm.SearchTerm;
import org.csstudio.utility.toolbox.func.Func1;
import org.csstudio.utility.toolbox.func.Func1Void;
import org.csstudio.utility.toolbox.func.Option;
import org.csstudio.utility.toolbox.guice.provider.SimpleDateFormatProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.AbstractListViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.google.inject.Inject;

public class WidgetFactory<T extends BindingEntity> implements Iterable<SearchTerm> {

	private Map<Property, Widget> properties = new HashMap<Property, Widget>();

	private Map<Property, AbstractListViewer> viewers = new HashMap<Property, AbstractListViewer>();

	private Map<Property, TableViewer> tableViewers = new HashMap<Property, TableViewer>();

	private GenericEditorInput<T> editorInput;

	private boolean isSearchMode;

	@Inject
	private AppLogger logger;

	@Inject
	private SimpleDateFormatProvider simpleDateFormatProvider;

	private Binder<T> binder;

	private static WidgetFactory<?> focusedWidgetFactory;

	public static void setFocusedWidgetFactory(WidgetFactory<?> wf) {
		WidgetFactory.focusedWidgetFactory = wf;
	}

	public static WidgetFactory<?> getFocusedWidgetFactory() {
		return WidgetFactory.focusedWidgetFactory;
	}

	public void init() {
		this.isSearchMode = true;
		this.editorInput = null;
	}
	
	public void init(GenericEditorInput<T> editorInput, Option<CrudController<T>> crudController, boolean isSearchMode,
				Binder<T> binder) {
		this.isSearchMode = isSearchMode;
		this.editorInput = editorInput;
		this.binder = binder;
	}

	public void setText(Property property, String text) {
		Widget widget = getWidget(property);
		if (widget instanceof Text) {
			((Text) widget).setText(StringUtils.trimToEmpty(text));
		} else if (widget instanceof Combo) {
			((Combo) widget).setText(StringUtils.trimToEmpty(text));
		} else {
			throw new IllegalStateException("Unsupported widget for setText");
		}
	}

	public void setReadOnly(Property property) {
		Widget widget = getWidget(property);
		if (widget instanceof Text) {			
			((Text) widget).setBackground(AbstractControlWithLabelBuilder.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			((Text) widget).setEditable(false);
		} else {
			throw new IllegalStateException("Unsupported widget for setText");
		}
	}

	public int getSelectionIndex(Property property) {
		Widget widget = getWidget(property);
		if (widget instanceof Combo) {
			return ((Combo) widget).getSelectionIndex();
		} else {
			throw new IllegalStateException("Unsupported widget for setText");
		}
	}

	public boolean isSelected(Property property) {
		Widget widget = getWidget(property);
		if (widget instanceof Button) {
			return ((Button) widget).getSelection();
		} else {
			throw new IllegalStateException("Unsupported widget for isSelected");
		}
	}

	public void select(Property property, int index) {
		Widget widget = getWidget(property);
		if (widget instanceof Combo) {
			((Combo) widget).select(index);
		} else {
			throw new IllegalStateException("Unsupported widget for setText");
		}
	}

	public void notifyListenersWithSelectionEvent(Property property) {
		Widget widget = getWidget(property);
		if (widget instanceof Combo) {
			((Combo) widget).notifyListeners(SWT.Selection, new Event());
		} else {
			throw new IllegalStateException("Unsupported widget for setText");
		}
	}

	public IStructuredSelection getSelection(Property property) {
		AbstractListViewer listViewer = viewers.get(property);
		return (IStructuredSelection) listViewer.getSelection();
	}

	public String getText(Property property) {
		Widget widget = getWidget(property);
		if (widget instanceof Text) {
			return ((Text) widget).getText();
		} else if (widget instanceof Combo) {
			return ((Combo) widget).getText();
		} else {
			throw new IllegalStateException("Unsupported widget for getText");
		}
	}

	public void setInput(Property property, List<? extends TextValue> data) {
		AbstractListViewer listViewer = viewers.get(property);
		if (listViewer == null) {
			Widget widget = getWidget(property);
			if (widget instanceof Text) {
				TextValueProposalProvider proposalProvider = (TextValueProposalProvider) widget
							.getData(AbstractControlWithLabelBuilder.CONTENT_PROPOSAL_PROVIDER);
				if (proposalProvider == null) {
					throw new IllegalStateException("proposalProvider must not be null");
				}
				proposalProvider.setData(data);
			} else {
				throw new IllegalStateException("Unsupported widget type: " + property);
			}
		} else {
			ComboViewer comboViewer = (ComboViewer) listViewer;
			comboViewer.setInput(data);
		}
	}

	public void setEnabled(Property property, boolean enabled) {
		Widget widget = getWidget(property);
		if (widget instanceof Text) {
			((Text) widget).setEnabled(enabled);
		} else if (widget instanceof Combo) {
			((Combo) widget).setEnabled(enabled);
		} else if (widget instanceof Button) {
			((Button) widget).setEnabled(enabled);
		} else {
			throw new IllegalStateException("Unsupported widget for setEnabled");
		}
	}

	private Widget getWidget(Property property) {
		Widget widget = properties.get(property);
		if (widget == null) {
			throw new IllegalStateException("Property not found: " + property);
		}
		return widget;
	}

	public void doCommand(Property property, Func1Void<Widget> widgetCommand) {
		Widget widget = properties.get(property);
		if (widget != null) {
			widgetCommand.apply(widget);
		} else {
			logger.logInfo("=== Property not found: " + property.getName());
		}
	}

	public void doCommandForViewer(Property property, Func1Void<AbstractListViewer> widgetCommand) {
		AbstractListViewer viewer = viewers.get(property);
		if (viewer != null) {
			widgetCommand.apply(viewer);
		} else {
			logger.logInfo("=== Property not found: " + property.getName());
		}
	}

	public void doCommandForTableViewer(Property property, Func1Void<TableViewer> widgetCommand) {
		TableViewer viewer = tableViewers.get(property);
		if (viewer != null) {
			widgetCommand.apply(viewer);
		} else {
			logger.logInfo("=== Property not found: " + property.getName());
		}
	}

	public int doCommandReturnInt(Property property, Func1<Integer, Widget> widgetCommand) {
		if (!Environment.isTestMode()) {
			throw new IllegalStateException("Only supported in testmode");
		}
		Widget widget = properties.get(property);
		if (widget == null) {
			throw new IllegalStateException("=== Property not found: " + property.getName());
		}
		return (widgetCommand.apply(widget));
	}

	public String doCommandReturnString(Property property, Func1<String, Widget> widgetCommand) {
		if (!Environment.isTestMode()) {
			throw new IllegalStateException("Only supported in testmode");
		}
		Widget widget = properties.get(property);
		if (widget == null) {
			throw new IllegalStateException("=== Property not found: " + property.getName());
		}
		return (widgetCommand.apply(widget));
	}

	public Boolean doCommandReturnBoolean(Property property, Func1<Boolean, Widget> widgetCommand) {
		if (!Environment.isTestMode()) {
			throw new IllegalStateException("Only supported in testmode");
		}
		Widget widget = properties.get(property);
		if (widget == null) {
			throw new IllegalStateException("=== Property not found: " + property.getName());
		}
		return (widgetCommand.apply(widget));
	}

	public void replaceBindings(T data) {
		binder.replaceBindings(properties,data);
	}

	public boolean markError(Path propertyPath, String message) {
		return markError(new Property(propertyPath.toString()), message);
	}

	public boolean markError(Property property, String message) {
		Widget widget = properties.get(property);
		if (widget == null) {
			return false;
		}
		ControlDecoration controlDecoration = (ControlDecoration) widget.getData(BuilderConstant.DECORATOR);
		if (controlDecoration != null) {
			controlDecoration.setDescriptionText(message);
			controlDecoration.show();
		}
		return true;
	}

	public void resetErrorMarkers() {
		for (Widget widget : getWidgets()) {
			ControlDecoration controlDecoration = (ControlDecoration) widget.getData(BuilderConstant.DECORATOR);
			if (controlDecoration != null) {
				controlDecoration.hide();
			}
		}
	}

	public void clearWidgetContent() {
		List<Widget> widgets = getWidgets();
		for (Widget widget : widgets) {
			if (widget instanceof Text) {
				Text text = (Text) widget;
				text.setText("");
			} else if (widget instanceof Combo) {
				Combo combo = (Combo) widget;
				combo.setText("");
			} else if (widget instanceof Button) {
				Button button = (Button) widget;
				int style = button.getStyle();
				if ((style & SWT.CHECK) == SWT.CHECK) {
					button.setSelection(false);
				}
			}
		}
	}

	private List<Widget> getWidgets() {
		Set<Entry<Property, Widget>> entries = properties.entrySet();
		List<Widget> widgets = new ArrayList<Widget>();
		for (Entry<Property, Widget> entry : entries) {
			widgets.add(entry.getValue());
		}
		return widgets;
	}

	public void createExpandItem(ExpandBar bar, String title, Composite composite, Property property) {
		ExpandItem expandItem = new ExpandItem(bar, SWT.NONE, 0);
		expandItem.setText(title);
		expandItem.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setControl(composite);
		expandItem.setExpanded(true);
		properties.put(property, expandItem);
	}

	public CheckboxBuilder checkbox(Composite composite, String propertyName) {
		return new CheckboxBuilder(composite, propertyName, properties, binder);
	}

	public RadioButtonBuilder radioButton(Composite composite, String propertyName) {
		return new RadioButtonBuilder(composite, propertyName, properties, binder);
	}

	public LabelBuilder label(Composite composite) {
		return new LabelBuilder(composite, editorInput);
	}

	public TextBuilder text(Composite composite, String propertyName) {
		return new TextBuilder(composite, propertyName, properties, editorInput, binder, SearchTermType.STRING,
					isSearchMode);
	}

	public TextBuilder numericText(Composite composite, String propertyName) {
		return new TextBuilder(composite, propertyName, properties, editorInput, binder, SearchTermType.NUMERIC,
					isSearchMode);
	}

	public ComboBuilder combo(Composite composite, String propertyName) {
		return new ComboBuilder(composite, propertyName, properties, editorInput, viewers, binder, isSearchMode);
	}

	public DateBuilder date(Composite composite, String propertyName) {
		return new DateBuilder(composite, propertyName, properties, editorInput, binder, isSearchMode,
					simpleDateFormatProvider.get());
	}

	public TableViewerBuilder tableViewer(Composite composite, String propertyName) {
		return new TableViewerBuilder(composite, propertyName, properties, tableViewers);
	}

	public ButtonBuilder button(Composite composite, String propertyName) {
		return new ButtonBuilder(composite, propertyName, properties);
	}

	public TabFolder createTabFolder(Composite composite) {
		return new TabFolder(composite, SWT.BORDER);
	}

	public TabItem createTabItem(String text, TabFolder tf) {
		TabItem ti = new TabItem(tf, SWT.BORDER);
		ti.setText(text);
		return ti;
	}

	public static class WidgetFactoryIterator implements Iterator<SearchTerm> {

		private final Map<Property, Widget> map;

		private Iterator<Property> it;

		public WidgetFactoryIterator(Map<Property, Widget> map) {
			this.map = map;
			it = map.keySet().iterator();
		}

		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public SearchTerm next() {
			Property property = it.next();
			Widget widget = map.get(property);
			String value = null;
			if (widget instanceof Text) {
				value = ((Text) widget).getText();
			} else if (widget instanceof Button) {
				Button button = (Button) widget;
				int style = button.getStyle();
				if (((style & SWT.CHECK) == SWT.CHECK) && (button.getSelection())) {
					value = "1";
				}
			} else if (widget instanceof Combo) {
				value = ((Combo) widget).getText();
			}
			return new SearchTerm(property, value, property.getType());
		}

		@Override
		public void remove() {
			throw new IllegalStateException("Collection is Read Only");
		}

	}

	@Override
	public Iterator<SearchTerm> iterator() {
		return new WidgetFactoryIterator(this.properties);
	}

}
